package dunca.github.io.logpurchasemanager.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.activities.util.StringFormatUtil;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import io.github.dunca.logpurchasemanager.shared.model.Acquirer;
import io.github.dunca.logpurchasemanager.shared.model.Acquisition;
import io.github.dunca.logpurchasemanager.shared.model.AcquisitionItem;
import io.github.dunca.logpurchasemanager.shared.model.Supplier;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;

public class PrintingActivity extends AppCompatActivity {
    public static final int ACTION_REQUEST_FILE_STORAGE_PERMISSIONS = 1;
    public static final String EXTRA_ACQUISITION_ID = "extra_acquisition_id";

    private static final String TAG = PrintingActivity.class.getName();
    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String NEW_LINE = System.lineSeparator();
    private static final String TEMPLATE_INVOICE = "invoice_template2.txt";
    private static final String TEMPLATE_INVOICE_LINE = "invoice_line_template2.txt";
    private static final int ACQUISITION_LINE_ELEMENT_WIDTH = 15;

    private static final String COMPANY_NAME = "S.C Example S.R.L";
    private static final String COMPANY_FISCAL_CODE = "RO 123456";
    private static final String COMPANY_COUNTRY = "Romania";
    private static final String COMPANY_STATE = "Cluj";
    private static final String COMPANY_CITY = "Cluj-Napoca";
    private static final String COMPANY_ADDRESS = "Programmer's street, no. 10a";
    private static final String COMPANY_TEL = "+40 0700123456";
    private static final String COMPANY_FAX = "+40 0700654321";

    private String INVOICE_DIRECTORY_NAME;

    private String mInvoiceTemplate;
    private String mInvoiceLineTemplate;

    private boolean mNetInvoice;
    private Acquisition mAcquisition;

    private DatabaseHelper mDbHelper;

    private View mRootLayout;
    private Button mBtnGeneratePdf;
    private TextView mTvInvoiceContent;
    private CheckBox mCbPrintUsingNetValues;

    private List<AcquisitionItem> mAcquisitionItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);

        initViews();
        setupOnClickActions();

        try {
            mInvoiceTemplate = readAssetAsString(TEMPLATE_INVOICE);
            mInvoiceLineTemplate = readAssetAsString(TEMPLATE_INVOICE_LINE);
        } catch (IOException e) {
            Log.e(TAG, "Cannot read invoice templates");
            throw new RuntimeException(e);
        }

        // points to the application's name
        INVOICE_DIRECTORY_NAME = getApplicationInfo().loadLabel(getPackageManager()).toString();

        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    private void initViews() {
        mRootLayout = findViewById(R.id.rootLayout);
        mBtnGeneratePdf = findViewById(R.id.btnGeneratePdf);
        mTvInvoiceContent = findViewById(R.id.tvInvoiceContent);
        mCbPrintUsingNetValues = findViewById(R.id.cbPrintUsingNetValues);
    }

    private void setupOnClickActions() {
        mCbPrintUsingNetValues.setOnCheckedChangeListener((view, isChecked) -> {
            int labelId = isChecked ? R.string.activity_printing_use_net_values_label
                    : R.string.activity_printing_use_gross_values_label;
            mCbPrintUsingNetValues.setText(labelId);

            mNetInvoice = isChecked;

            updateInvoiceTextView();
        });

        mBtnGeneratePdf.setOnClickListener(view -> generatePdfDocument());
    }

    @Override
    protected void onResume() {
        super.onResume();

        int acquisitionId = getIntent().getIntExtra(EXTRA_ACQUISITION_ID, MethodParameterConstants.INVALID_INDEX);

        if (acquisitionId == MethodParameterConstants.INVALID_INDEX) {
            throw new RuntimeException("Invalid acquisition id");
        }

        initAcquisition(acquisitionId);
        initAcquisitionItemList(acquisitionId);

        updateInvoiceTextView();
    }

    private void initAcquisition(int acquisitionId) {
        mAcquisition = mDbHelper.getAcquisitionDao().queryForId(acquisitionId);
    }

    private void initAcquisitionItemList(int acquisitionId) {
        try {
            mAcquisitionItemList = mDbHelper.getAcquisitionItemDao().queryBuilder().where()
                    .eq(CommonFieldNames.ACQUISITION_ID, acquisitionId).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateInvoiceTextView() {
        mTvInvoiceContent.setText(getInvoice());
    }

    private String getInvoice() {
        String invoiceLines = getInvoiceLines();
        String invoiceTemplate = getInvoiceTemplate();

        Map<String, String> values = new HashMap<>();
        values.put("acquisition_lines", invoiceLines);

        return new StringSubstitutor(values).replace(invoiceTemplate);
    }

    private String getInvoiceTemplate() {
        // should not populate ${acquisition_line}

        Map<String, String> values = new HashMap<>();

        values.put("company_name", COMPANY_NAME);
        values.put("company_fiscal_code", COMPANY_FISCAL_CODE);
        values.put("company_country", COMPANY_COUNTRY);
        values.put("company_state", COMPANY_STATE);
        values.put("company_city", COMPANY_CITY);
        values.put("company_address", COMPANY_ADDRESS);
        values.put("company_tel", COMPANY_TEL);
        values.put("company_fax", COMPANY_FAX);

        String netOrGross = getString(mNetInvoice ? R.string.activity_printing_net_placeholder
                : R.string.activity_printing_gross_placeholder);
        values.put("net_or_gross", netOrGross);

        values.put("acquirer_username", mAcquisition.getAcquirer().getUsername());
        values.put("acquisition_serial_number", mAcquisition.getSerialNumber());
        values.put("acquisition_date", ISO_DATE_FORMAT.format(mAcquisition.getReceptionDate()));

        Supplier supplier = mAcquisition.getSupplier();
        values.put("supplier_code", supplier.getCode());
        values.put("supplier_name", supplier.getName());
        values.put("supplier_country", supplier.getCountry());
        values.put("supplier_address", supplier.getAddress());
        values.put("supplier_street", supplier.getStreet());

        values.put("acquisition_observations", mAcquisition.getObservations());

        Acquirer acquirer = mAcquisition.getAcquirer();
        values.put("acquirer_username", acquirer.getUsername());
        values.put("acquirer_first_name", acquirer.getFirstName());
        values.put("acquirer_last_name", acquirer.getLastName());

        values.put("log_count", String.valueOf(getLogCount()));
        values.put("total_log_volume", StringFormatUtil.round(getTotalVolume()));
        values.put("length_average", StringFormatUtil.round(getLengthAverage()));
        values.put("diameter_average", StringFormatUtil.round(getDiameterAverage()));
        values.put("volume_average", StringFormatUtil.round(getVolumeAverage()));

        return new StringSubstitutor(values).replace(mInvoiceTemplate);
    }

    private String getInvoiceLine(AcquisitionItem acquisitionItem) {
        Map<String, String> values = new HashMap<>();

        values.put("log_code", padAcquisitionLineElement(acquisitionItem.getLogBarCode()));
        values.put("species", padAcquisitionLineElement(acquisitionItem.getTreeSpecies().getSymbol()));
        values.put("quality_class", padAcquisitionLineElement(acquisitionItem.getLogQualityClass().getSymbol()));

        double length = mNetInvoice ? acquisitionItem.getNetLength() : acquisitionItem.getGrossLength();
        values.put("length", padAcquisitionLineElement(StringFormatUtil.round(length)));

        double diameter = mNetInvoice ? acquisitionItem.getNetDiameter() : acquisitionItem.getGrossDiameter();
        values.put("diameter", padAcquisitionLineElement(StringFormatUtil.round(diameter)));

        double volume = mNetInvoice ? acquisitionItem.getNetVolume() : acquisitionItem.getGrossVolume();
        values.put("volume", padAcquisitionLineElement(StringFormatUtil.round(volume)));

        values.put("log_observations", padAcquisitionLineElement(acquisitionItem.getObservations().trim()));

        return new StringSubstitutor(values).replace(mInvoiceLineTemplate);
    }

    public String getInvoiceLines() {
        StringBuilder invoiceLines = new StringBuilder();

        for (AcquisitionItem acquisitionItem : mAcquisitionItemList) {
            String invoiceLine = getInvoiceLine(acquisitionItem);

            invoiceLines.append(invoiceLine).append(NEW_LINE).append(NEW_LINE);
        }

        return invoiceLines.toString();
    }

    private String padAcquisitionLineElement(String element) {
        return String.format("%-" + ACQUISITION_LINE_ELEMENT_WIDTH + "s", element);
    }

    private String readAssetAsString(String fileName) throws IOException {
        try (InputStream is = getAssets().open(fileName);
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder lines = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {
                lines.append(line).append(NEW_LINE);
            }

            return lines.toString();
        }
    }

    private int getLogCount() {
        return mAcquisitionItemList.size();
    }

    private double getTotalVolume() {
        return mNetInvoice ? mAcquisition.getTotalNetVolume() : mAcquisition.getTotalGrossVolume();
    }

    private double getLengthAverage() {
        return getAverageOf(item -> mNetInvoice ? item.getNetLength() : item.getGrossLength());
    }

    private double getDiameterAverage() {
        return getAverageOf(item -> mNetInvoice ? item.getNetDiameter() : item.getGrossDiameter());
    }

    private double getVolumeAverage() {
        return getAverageOf(item -> mNetInvoice ? item.getNetVolume() : item.getGrossVolume());
    }

    private double getAverageOf(ToDoubleFunction<AcquisitionItem> doublePropertyGetter) {
        return mAcquisitionItemList.stream().mapToDouble(doublePropertyGetter).average().getAsDouble();
    }

    private void generatePdfDocument() {
        if (!hasFileStoragePermissions()) {
            promptForFileStoragePermissions();
        }

        File pdfInvoiceDirectory = new File(Environment.getExternalStorageDirectory(), INVOICE_DIRECTORY_NAME);

        Boolean rootDirectoryCreated;

        if (!pdfInvoiceDirectory.exists()) {
            rootDirectoryCreated = pdfInvoiceDirectory.mkdirs();

            if (!rootDirectoryCreated) {
                PopupUtil.snackbar(mRootLayout, R.string.activity_printing_could_not_create_invoice_directory_msg);
                return;
            }
        }

        String acquisitionReceptionNumber = mAcquisition.getAcquirer().getUsername() + mAcquisition.getSerialNumber();

        String pdfFileName = String.format("invoice-%s.pdf", acquisitionReceptionNumber);
        File pdfInvoiceFile = new File(pdfInvoiceDirectory.getPath(), pdfFileName);

        if (pdfInvoiceFile.exists()) {
            pdfInvoiceFile.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(pdfInvoiceFile)) {
            pdfInvoiceFile.createNewFile();

            PdfDocument pdfDocument = new PdfDocument();

            mTvInvoiceContent.measure(0, 0);
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(mTvInvoiceContent.getMeasuredWidth(),
                    mTvInvoiceContent.getMeasuredHeight(), 1).create();

            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            mTvInvoiceContent.draw(page.getCanvas());

            pdfDocument.finishPage(page);

            pdfDocument.writeTo(fos);

        } catch (IOException e) {
            PopupUtil.snackbar(mRootLayout, R.string.activity_printing_pdf_could_not_generate_pdf_msg);
            e.printStackTrace();
        }

        PopupUtil.snackbar(mRootLayout, R.string.activity_printing_pdf_generated_msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTION_REQUEST_FILE_STORAGE_PERMISSIONS && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            generatePdfDocument();
        }
    }

    private boolean hasFileStoragePermissions() {
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void promptForFileStoragePermissions() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, ACTION_REQUEST_FILE_STORAGE_PERMISSIONS);
    }
}
