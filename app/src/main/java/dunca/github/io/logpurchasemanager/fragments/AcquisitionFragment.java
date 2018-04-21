package dunca.github.io.logpurchasemanager.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.j256.ormlite.stmt.DeleteBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.AcquisitionListActivity;
import dunca.github.io.logpurchasemanager.activities.util.InputValidationUtil;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.activities.util.StringFormatUtil;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionIdEvent;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionTotalPriceUpdateRequestEvent;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionTotalVolumeUpdateRequestEvent;
import dunca.github.io.logpurchasemanager.fragments.util.FragmentUtil;
import io.github.dunca.logpurchasemanager.shared.model.Acquirer;
import io.github.dunca.logpurchasemanager.shared.model.Acquisition;
import io.github.dunca.logpurchasemanager.shared.model.AcquisitionItem;
import io.github.dunca.logpurchasemanager.shared.model.Supplier;
import io.github.dunca.logpurchasemanager.shared.model.WoodCertification;
import io.github.dunca.logpurchasemanager.shared.model.WoodRegion;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;
import io.github.dunca.logpurchasemanager.shared.model.interfaces.Model;

public class AcquisitionFragment extends Fragment {
    private static final String PROP_LAST_ACQUISITION_ID = "prop_last_acquisition_id";

    private static final DateFormat ISO_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static int sCurrentAcquisitionId = MethodParameterConstants.INVALID_INDEX;

    private Acquisition mExistingAcquisition;

    private View mFragmentView;

    private EditText mEtSerialNumber;
    private Spinner mSpinnerAcquirer;
    private TextView mTvDate;
    private TextView mTvSupplierName;
    private Spinner mSpinnerWoodRegion;
    private EditText mEtRegionZone;
    private Spinner mSpinnerWoodCertification;
    private EditText mEtObservations;
    private EditText mEtDiscountPercentage;
    private TextView mTvDiscountValue;
    private TextView mTvTotalValue;
    private TextView mTvTotalNetVolume;
    private TextView mTvTotalGrossVolume;
    private CheckBox mCbNetTotalValue;
    private Button mBtnSave;
    private Button mBtnDelete;

    private TextInputLayout mTilSerialNumber;
    private TextInputLayout mTilRegionZone;
    private TextInputLayout mTilObservations;
    private TextInputLayout mTilDiscountPercentage;

    private final DatabaseHelper mDbHelper;

    private List<WoodRegion> mWoodRegionList;
    private List<WoodCertification> mWoodCertificationList;
    private List<Acquirer> mAcquirerList;
    private List<Supplier> mSupplierList;

    private Supplier mSelectedSupplier;

    public AcquisitionFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();

        initDbLists();
    }

    /**
     * Initializes lists of static data provided by the database (acquirers, wood regions, etc.)
     */
    private void initDbLists() {
        mAcquirerList = mDbHelper.getAcquirerDao().queryForAll();

        mWoodRegionList = mDbHelper.getWoodRegionDao().queryForAll();

        try {
            mWoodCertificationList = mDbHelper.getWoodCertificationDao()
                    .queryBuilder().orderBy(CommonFieldNames.LIST_PRIORITY, true)
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        mSupplierList = mDbHelper.getSupplierDao().queryForAll();
        mSelectedSupplier = mSupplierList.get(0);
    }

    public static AcquisitionFragment newInstance(int acquisitionId) {
        AcquisitionFragment fragment = new AcquisitionFragment();

        Bundle args = new Bundle();

        args.putInt(MethodParameterConstants.ACQUISITION_ID_PARAM, acquisitionId);
        sCurrentAcquisitionId = acquisitionId;

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_acquisition, container, false);

        initViews();
        setupOnClickActions();

        Bundle args = getArguments();
        int acquisitionId = args.getInt(MethodParameterConstants.ACQUISITION_ID_PARAM);

        if (acquisitionId != MethodParameterConstants.INVALID_INDEX) {
            // the user is trying to update an existing acquisition
            setExistingAcquisition(acquisitionId);
        } else {
            updateUiWithDefaults();
        }

        updateDeleteButtonState();

        return mFragmentView;
    }

    /**
     * Enables {@link #mBtnDelete} if {@link #mExistingAcquisition} is not null
     */
    private void updateDeleteButtonState() {
        mBtnDelete.setEnabled(mExistingAcquisition != null);
    }

    private void initViews() {
        mTilSerialNumber = mFragmentView.findViewById(R.id.tilSerialNumber);
        mEtSerialNumber = mFragmentView.findViewById(R.id.etSerialNumber);

        mSpinnerAcquirer = mFragmentView.findViewById(R.id.spinnerAcquirer);
        ArrayAdapter acquirerAdapter = createDefaultSpinnerAdapter(mAcquirerList);
        mSpinnerAcquirer.setAdapter(acquirerAdapter);

        mTvDate = mFragmentView.findViewById(R.id.tvDate);

        mTvSupplierName = mFragmentView.findViewById(R.id.tvSupplierName);

        mSpinnerWoodRegion = mFragmentView.findViewById(R.id.spinnerWoodRegionSymbol);
        ArrayAdapter woodRegionAdapter = createDefaultSpinnerAdapter(mWoodRegionList);
        mSpinnerWoodRegion.setAdapter(woodRegionAdapter);

        mTilRegionZone = mFragmentView.findViewById(R.id.tilRegionZone);
        mEtRegionZone = mFragmentView.findViewById(R.id.etRegionZone);

        mSpinnerWoodCertification = mFragmentView.findViewById(R.id.spinnerWoodCertification);
        ArrayAdapter woodCertificationAdapter = createDefaultSpinnerAdapter(mWoodCertificationList);
        mSpinnerWoodCertification.setAdapter(woodCertificationAdapter);

        mTilObservations = mFragmentView.findViewById(R.id.tilObservations);
        mEtObservations = mFragmentView.findViewById(R.id.edObservations);

        mTilDiscountPercentage = mFragmentView.findViewById(R.id.tilDiscountPercentage);
        mEtDiscountPercentage = mFragmentView.findViewById(R.id.etDiscountPercentage);
        mEtDiscountPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // don't allow discount values of over 100

                String discountPercentageString = mEtDiscountPercentage.getText().toString();

                if (discountPercentageString.length() > 0) {
                    double discountPercentage = Double.valueOf(discountPercentageString);

                    if (discountPercentage > 100) {
                        s.clear();
                        s.append("100");
                    }
                }

                updateUiDiscountValue();
                updateUiTotalValue();
            }
        });

        mTvDiscountValue = mFragmentView.findViewById(R.id.tvDiscountValue);

        mTvTotalValue = mFragmentView.findViewById(R.id.tvTotalValue);

        mTvTotalNetVolume = mFragmentView.findViewById(R.id.tvTotalNetVolume);

        mTvTotalGrossVolume = mFragmentView.findViewById(R.id.tvTotalGrossVolume);

        mCbNetTotalValue = mFragmentView.findViewById(R.id.cbNetTotalValue);

        mBtnSave = mFragmentView.findViewById(R.id.btnSave);

        mBtnDelete = mFragmentView.findViewById(R.id.btnDelete);
    }

    private void setupOnClickActions() {
        mBtnSave.setOnClickListener((source) -> persistAcquisitionChanges());

        mBtnDelete.setOnClickListener((source) -> deleteCurrentAcquisition());


        DatePickerDialog.OnDateSetListener datePickListener = (view, year, month, dayOfMonth) -> {
            Date pickedDate = new Date(year - 1900, month, dayOfMonth);
            updateUiDate(pickedDate);
        };

        Calendar currentCalendar = Calendar.getInstance();

        mTvDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), datePickListener, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH),
                    currentCalendar.get(Calendar.DAY_OF_MONTH))
                    .show();
        });


        mTvSupplierName.setOnClickListener((view) -> {
            View supplierListDialogView = getLayoutInflater().inflate(R.layout.fragment_acquisition_supplier_dialog, null);

            AlertDialog supplierListDialog = new AlertDialog.Builder(getContext()).create();

            supplierListDialog.setView(supplierListDialogView);
            supplierListDialog.setTitle(getString(R.string.fragment_acquisition_supplier_dialog_title));

            ListView supplierListView = supplierListDialogView.findViewById(R.id.lvSuppliers);

            supplierListView.setOnItemClickListener((parent, v, position, id) -> {
                supplierListDialog.dismiss();

                /*
                we use getItemAtPosition instead of querying the list because the dialog
                lets the user filter the list
                */
                mSelectedSupplier = (Supplier) supplierListView.getItemAtPosition(position);
                setSupplierNameTextBox(mSelectedSupplier);
            });

            ArrayAdapter<Supplier> arrayAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_list_item_1, mSupplierList);
            supplierListView.setAdapter(arrayAdapter);

            EditText etNameSupplierPrefix = supplierListDialogView.findViewById(R.id.etSupplierNamePrefix);

            etNameSupplierPrefix.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    arrayAdapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            supplierListDialog.show();
        });


        mCbNetTotalValue.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int checkboxLabel = isChecked ? R.string.fragment_acquisition_net_total_label : R.string.fragment_acquisition_gross_total_label;
            mCbNetTotalValue.setText(checkboxLabel);

            updateUiDiscountValue();
            updateUiTotalValue();
        });
    }

    private void setSupplierNameTextBox(Supplier supplier) {
        mTvSupplierName.setTag(supplier.getId());
        mTvSupplierName.setText(supplier.getName());
    }

    /**
     * Deletes the {@link Acquisition} instance referenced by the {@link #mExistingAcquisition}
     * variable. After deletion, the {@link AcquisitionListActivity} activity is started
     */
    private void deleteCurrentAcquisition() {
        try {
            List<DeleteBuilder> deleteBuilderList = Arrays.asList(
                    mDbHelper.getLogPriceDao().deleteBuilder(),
                    mDbHelper.getAcquisitionItemDao().deleteBuilder()
            );

            for (DeleteBuilder deleteBuilder : deleteBuilderList) {
                deleteBuilder.where().eq(CommonFieldNames.ACQUISITION_ID, mExistingAcquisition.getId());
                deleteBuilder.delete();
            }

            DeleteBuilder deleteBuilder = mDbHelper.getAcquisitionDao().deleteBuilder();
            deleteBuilder.where().eq(CommonFieldNames.ID, mExistingAcquisition.getId());
            deleteBuilder.delete();

            Intent intent = new Intent(getContext(), AcquisitionListActivity.class);
            startActivity(intent);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets {@link #mExistingAcquisition} to the {@link Acquisition} instance corresponding to the
     * given id, then calls {@link #syncUiWithAcquisition()} to update the UI
     *
     * @param acquisitionId the {@link Acquisition#id} of the {@link Acquisition} instance
     */
    private void setExistingAcquisition(int acquisitionId) {
        mExistingAcquisition = mDbHelper.getAcquisitionDao().queryForId(acquisitionId);

        syncUiWithAcquisition();
    }

    /**
     * Updates the UI inputs based on the values of {@link #mExistingAcquisition}
     */
    private void syncUiWithAcquisition() {
        mEtSerialNumber.setText(mExistingAcquisition.getSerialNumber());
        mEtRegionZone.setText(mExistingAcquisition.getRegionZone());
        mEtObservations.setText(mExistingAcquisition.getObservations());
        mEtDiscountPercentage.setText(String.valueOf(mExistingAcquisition.getDiscountPercentage()));

        mSpinnerAcquirer.setSelection(mAcquirerList.indexOf(mExistingAcquisition.getAcquirer()));
        mSpinnerWoodRegion.setSelection(mWoodRegionList.indexOf(mExistingAcquisition.getWoodRegion()));
        mSpinnerWoodCertification.setSelection(mWoodCertificationList.indexOf(mExistingAcquisition.getWoodCertification()));

        updateUiDate(mExistingAcquisition.getReceptionDate());

        setSupplierNameTextBox(mExistingAcquisition.getSupplier());

        mCbNetTotalValue.setChecked(mExistingAcquisition.isNet());

        mTvDiscountValue.setText(StringFormatUtil.round(mExistingAcquisition.getDiscountValue()));
        mTvTotalValue.setText(StringFormatUtil.round(mExistingAcquisition.getTotalValue()));
        mTvTotalNetVolume.setText(StringFormatUtil.round(mExistingAcquisition.getTotalNetVolume()));
        mTvTotalGrossVolume.setText(StringFormatUtil.round(mExistingAcquisition.getTotalGrossVolume()));
    }

    /**
     * Updates the provided {@link Acquisition} instance with values of the UI inputs
     *
     * @param acquisition the {@link Acquisition} instance to update
     */
    private void syncAcquisitionWithUi(Acquisition acquisition) {
        String serialNumber = mEtSerialNumber.getText().toString();
        String regionZone = mEtRegionZone.getText().toString();
        String observations = mEtObservations.getText().toString();
        double totalValueWithDiscount = Double.valueOf(mTvTotalValue.getText().toString());
        double discountValue = Double.valueOf(mTvDiscountValue.getText().toString());
        double totalGrossVolume = Double.valueOf(mTvTotalGrossVolume.getText().toString());
        double totalNetVolume = Double.valueOf(mTvTotalNetVolume.getText().toString());

        acquisition.setSerialNumber(serialNumber);
        acquisition.setAcquirer(getSelectedAcquirer());
        acquisition.setSupplier(getSelectedSupplier());
        acquisition.setReceptionDate(getSelectedDate());
        acquisition.setRegionZone(regionZone);
        acquisition.setWoodRegion(getSelectedWoodRegion());
        acquisition.setWoodCertification(getSelectedWoodCertification());
        acquisition.setObservations(observations);
        acquisition.setTotalValue(totalValueWithDiscount);
        acquisition.setTotalGrossVolume(totalGrossVolume);
        acquisition.setTotalNetVolume(totalNetVolume);
        acquisition.setDiscountPercentage(getDiscountPercentage());
        acquisition.setDiscountValue(discountValue);
        acquisition.setNet(mCbNetTotalValue.isChecked());
        acquisition.setSynced(false);
    }

    /**
     * Creates and returns a {@link Acquisition} instance based on the values in the UI
     *
     * @return a {@link Acquisition} instance based on the values in the UI
     */
    private Acquisition createAcquisitionMatchingUi() {
        Acquisition acquisition = new Acquisition();
        syncAcquisitionWithUi(acquisition);

        return acquisition;
    }

    /**
     * Updates some UI inputs with default values (current date, a valid acquisition serial number,
     * etc.)
     */
    private void updateUiWithDefaults() {
        int currentAcquisitionSerialNumber = getLastAcquisitionSerialNumber() + 1;
        mEtSerialNumber.setText(String.valueOf(currentAcquisitionSerialNumber));

        setSupplierNameTextBox(mSelectedSupplier);

        updateUiDate(new Date());
    }

    /**
     * Updates {@link #mTvDate} with information found in the given {@link Date} instance
     *
     * @param date the {@link Date} to use when updating {@link #mTvDate}
     */
    private void updateUiDate(Date date) {
        mTvDate.setText(ISO_DATE_FORMAT.format(date));
    }

    private void persistAcquisitionChanges() {
        if (!inputFormsAreValid()) {
            return;
        }

        // not working with an existing acquisition item
        if (mExistingAcquisition == null) {
            Acquisition acquisition = createAcquisitionMatchingUi();

            mDbHelper.getAcquisitionDao().create(acquisition);
            acquisition.setAppAllocatedId(acquisition.getId());
            mDbHelper.getAcquisitionDao().update(acquisition);

            saveAcquisitionSerialNumber(acquisition);

            PopupUtil.snackbar(getView(), getString(R.string.fragment_acquisition_new_acquisition_persisted_msg));

            mExistingAcquisition = acquisition;
            sCurrentAcquisitionId = acquisition.getId();

            // send the acquisition id to MainTabbedActivity, which needs to know it because it
            // uses it to figure out if the FAB on the AcquisitionListFragment should show up
            EventBus.getDefault().post(new AcquisitionIdEvent(acquisition.getId()));

            updateDeleteButtonState();
        } else {
            syncAcquisitionWithUi(mExistingAcquisition);
            mExistingAcquisition.setSynced(false);
            mDbHelper.getAcquisitionDao().update(mExistingAcquisition);

            PopupUtil.snackbar(getView(), getString(R.string.fragment_acquisition_updated_existing_acquisition_msg));
        }
    }

    private boolean inputFormsAreValid() {
        return InputValidationUtil.areNotEmpty(mTilSerialNumber, mTilRegionZone, mTilObservations, mTilDiscountPercentage);
    }

    private Acquirer getSelectedAcquirer() {
        return (Acquirer) mSpinnerAcquirer.getSelectedItem();
    }

    private Supplier getSelectedSupplier() {
        Supplier supplier = mDbHelper.getSupplierDao().queryForId((int) mTvSupplierName.getTag());

        if (supplier.getName().equals(mTvSupplierName.getText().toString())) {
            return supplier;
        }

        throw new RuntimeException("Programming error");
    }

    private Date getSelectedDate() {
        try {
            return ISO_DATE_FORMAT.parse(mTvDate.getText().toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Programming error");
        }
    }

    private WoodRegion getSelectedWoodRegion() {
        return (WoodRegion) mSpinnerWoodRegion.getSelectedItem();
    }

    private WoodCertification getSelectedWoodCertification() {
        return (WoodCertification) mSpinnerWoodCertification.getSelectedItem();
    }

    private double getDiscountPercentage() {
        try {
            return Double.valueOf(mEtDiscountPercentage.getText().toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Programming error", e);
        }
    }

    /**
     * Sets the value of the {@link #PROP_LAST_ACQUISITION_ID} property to match the
     * {@link Acquisition#serialNumber} of the given {@link Acquisition} instance
     *
     * @param currentAcquisition the {@link Acquisition} instance to use a the source
     */
    private void saveAcquisitionSerialNumber(Acquisition currentAcquisition) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putInt(PROP_LAST_ACQUISITION_ID, Integer.valueOf(
                currentAcquisition.getSerialNumber()));

        editor.apply();
    }

    /**
     * Gets the value of the {@link #PROP_LAST_ACQUISITION_ID} property
     *
     * @return the value of the {@link #PROP_LAST_ACQUISITION_ID} property
     */
    private int getLastAcquisitionSerialNumber() {
        return getSharedPreferences().getInt(PROP_LAST_ACQUISITION_ID, 0);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        return FragmentUtil.createDefaultSpinnerAdapter(getContext(), modelInstanceList);
    }

    /**
     * Calculates the discount value, based on {@link #mEtDiscountPercentage} and
     * {@link #calculateTotalValue()}
     * If {@link #mEtDiscountPercentage} is empty / null, 0 is returned
     *
     * @return the discount value, based on {@link #mEtDiscountPercentage} and {@link #calculateTotalValue()}
     */
    private double calculateDiscountValue() {
        double discountPercentage = getDiscountPercentage();

        if (discountPercentage == 0) {
            return 0;
        }

        double totalValue = calculateTotalValue();

        if (totalValue == 0) {
            return 0;
        }

        return discountPercentage / 100 * totalValue;
    }

    private double calculateTotalValue() {
        // there can't be any total, since this acquisition has yet to be saved
        if (mExistingAcquisition == null) {
            return 0;
        }

        double total = 0;

        for (AcquisitionItem acquisitionItem : getAcquisitionItemList()) {
            double currentVolume = mCbNetTotalValue.isChecked() ? acquisitionItem.getNetVolume()
                    : acquisitionItem.getGrossVolume();

            total += (acquisitionItem.getPrice() * currentVolume);
        }

        return total;
    }

    private double calculateTotalValueWithDiscount() {
        return calculateTotalValue() - calculateDiscountValue();
    }

    private double calculateTotalGrossVolume() {
        return calculateTotalVolume(false);
    }

    private double calculateTotalNetVolume() {
        return calculateTotalVolume(true);
    }

    private double calculateTotalVolume(boolean net) {
        if (mExistingAcquisition == null) {
            return 0;
        }

        Stream<AcquisitionItem> stream = getAcquisitionItemList().stream();

        if (net) {
            return stream.mapToDouble(AcquisitionItem::getNetVolume).sum();
        }

        return stream.mapToDouble(AcquisitionItem::getGrossVolume).sum();
    }

    /**
     * Updates {@link #mTvDiscountValue} with the value provided by {@link #calculateDiscountValue()}
     */
    private void updateUiDiscountValue() {
        mTvDiscountValue.setText(StringFormatUtil.round(calculateDiscountValue()));
    }

    /**
     * Updates {@link #mTvTotalValue} with the value provided by {@link #calculateTotalValueWithDiscount()}
     */
    private void updateUiTotalValue() {
        mTvTotalValue.setText(StringFormatUtil.round(calculateTotalValueWithDiscount()));
    }

    private SharedPreferences getSharedPreferences() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }

    private List<AcquisitionItem> getAcquisitionItemList() {
        try {
            return mDbHelper.getAcquisitionItemDao().queryBuilder().where()
                    .eq(CommonFieldNames.ACQUISITION_ID, mExistingAcquisition.getId()).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        EventBus.getDefault().unregister(this);

        super.onDetach();
    }

    @Subscribe
    public void onAcquisitionTotalPriceUpdateRequestEvent(AcquisitionTotalPriceUpdateRequestEvent event) {
        mTvDiscountValue.setText(StringFormatUtil.round(calculateDiscountValue()));
        mTvTotalValue.setText(StringFormatUtil.round(calculateTotalValueWithDiscount()));
        persistAcquisitionChanges();
    }

    @Subscribe
    public void onAcquistionTotalVolumeUpdateRequestEvent(AcquisitionTotalVolumeUpdateRequestEvent event) {
        mTvTotalNetVolume.setText(StringFormatUtil.round(calculateTotalNetVolume()));
        mTvTotalGrossVolume.setText(StringFormatUtil.round(calculateTotalGrossVolume()));
        persistAcquisitionChanges();
    }
}
