package dunca.github.io.logpurchasemanager.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.common.StringUtils;

import org.apache.commons.text.StringSubstitutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.util.StringFormatUtil;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionItem;
import dunca.github.io.logpurchasemanager.data.model.Supplier;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;

public class PrintingActivity extends AppCompatActivity {
    private static final String COMPANY_NAME = "S.C Example S.R.L";
    private static final String COMPANY_FISCAL_CODE = "RO 123456";
    private static final String COMPANY_COUNTRY = "Romania";
    private static final String COMPANY_STATE = "Cluj";
    private static final String COMPANY_CITY = "Cluj-Napoca";
    private static final String COMPANY_ADDRESS = "Programmer's street, no. 10a";
    private static final String COMPANY_TEL = "+40 0700123456";
    private static final String COMPANY_FAX = "+40 0700654321";

    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final String NEW_LINE = System.lineSeparator();

    public static final String EXTRA_ACQUISITION_ID = "extra_acquisition_id";

    private static final String TEMPLATE_INVOICE = "invoice_template2.txt";
    private static final String TEMPLATE_INVOICE_LINE = "invoice_line_template2.txt";

    private static final int ACQUISITION_LINE_ELEMENT_WIDTH = 15;

    private String mInvoiceTemplate;
    private String mInvoiceLineTemplate;
    private Acquisition mAcquisition;

    private DatabaseHelper mDbHelper;

    private boolean mNetInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printing);

        try {
            mInvoiceTemplate = readResourceFile(TEMPLATE_INVOICE);
            mInvoiceLineTemplate = readResourceFile(TEMPLATE_INVOICE_LINE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int acquisitionId = getIntent().getIntExtra(EXTRA_ACQUISITION_ID, MethodParameterConstants.INVALID_INDEX);

        if (acquisitionId == MethodParameterConstants.INVALID_INDEX) {
            throw new RuntimeException("Invalid acquisition index");
        }

        initAcquisition(acquisitionId);

        String invoice = getInvoice();

        System.out.println(invoice);
    }

    private void initAcquisition(int acquisitionId) {
        mAcquisition = mDbHelper.getAcquisitionDao().queryForId(acquisitionId);
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

        String netOrGross = getString(mNetInvoice ? R.string.activity_printing_net_value : R.string.activity_printing_gross_value);
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

        values.put("log_count", "XX");
        values.put("total_log_volume", "XX");
        values.put("length_average", "XX");
        values.put("diameter_average", "XX");
        values.put("volume_average", "XX");

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

        List<AcquisitionItem> acquisitionItemList;

        try {
            acquisitionItemList = mDbHelper.getAcquisitionItemDao().queryBuilder()
                    .where()
                    .eq(CommonFieldNames.ACQUISITION_ID, mAcquisition.getId())
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (AcquisitionItem acquisitionItem : acquisitionItemList) {
            String invoiceLine = getInvoiceLine(acquisitionItem);

            invoiceLines.append(invoiceLine).append(NEW_LINE).append(NEW_LINE);
        }

        return invoiceLines.toString();
    }

    private String padAcquisitionLineElement(String element) {
        return String.format("%-" + ACQUISITION_LINE_ELEMENT_WIDTH + "s", element);
    }

    private String readResourceFile(String fileName) throws IOException {
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
}
