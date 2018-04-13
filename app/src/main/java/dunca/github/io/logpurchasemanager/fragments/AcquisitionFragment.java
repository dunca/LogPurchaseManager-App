package dunca.github.io.logpurchasemanager.fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.j256.ormlite.stmt.DeleteBuilder;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.AcquisitionListActivity;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionItem;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionStatus;
import dunca.github.io.logpurchasemanager.data.model.LogPrice;
import dunca.github.io.logpurchasemanager.data.model.Supplier;
import dunca.github.io.logpurchasemanager.data.model.WoodCertification;
import dunca.github.io.logpurchasemanager.data.model.WoodRegion;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;
import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;

public class AcquisitionFragment extends Fragment {
    public static final String ACQUISITION_ID_PARAM = "acquisition_id_param";

    private static final String LAST_ACQUISITION_ID_PROP = "last_acquisition_id_prop";


    private Acquisition mModifiedAcquisition;

    private DateFormat mIsoDateFormat;


    private View mFragment;

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

    private CheckBox mCbNetTotalValue;

    private Button mBtnSave;

    private Button mBtnDelete;

    private DatabaseHelper mDbHelper;

    private List<WoodRegion> mWoodRegionList;
    private List<WoodCertification> mWoodCertificationList;
    private List<Acquirer> mAcquirerList;
    private List<Supplier> mSupplierList;

    private Supplier mSelectedSupplier;

    public AcquisitionFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();

        mIsoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

        if (acquisitionId != MethodParameterConstants.NO_ELEMENT_INDEX) {
            args.putInt(ACQUISITION_ID_PARAM, acquisitionId);
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        mFragment = inflater.inflate(R.layout.fragment_acquisition, container, false);

        initViews();
        setupOnClickActions();

        Bundle args = getArguments();
        int acquisitionId = args.getInt(ACQUISITION_ID_PARAM, 0);

        if (acquisitionId != 0) {
            // the user is trying to update an existing object
            setModifiedAcquisition(acquisitionId);
        } else {
            updateUiWithDefaults();
        }

        updateDeleteButtonState();

        return mFragment;
    }

    private void updateDeleteButtonState() {
        mBtnDelete.setEnabled(mModifiedAcquisition != null);
    }

    private void initViews() {
        mEtSerialNumber = mFragment.findViewById(R.id.etSerialNumber);
        // mEtSerialNumber.setText(String.valueOf(getLastAcquisitionSerialNumber() + 1));

        mSpinnerAcquirer = mFragment.findViewById(R.id.spinnerAcquirer);
        ArrayAdapter acquirerAdapter = createDefaultSpinnerAdapter(mAcquirerList);
        mSpinnerAcquirer.setAdapter(acquirerAdapter);


        mTvDate = mFragment.findViewById(R.id.tvDate);

        // set the first supplier as the default one
        mTvSupplierName = mFragment.findViewById(R.id.tvSupplierName);
        mTvSupplierName.setText(mSelectedSupplier.getName());

        mSpinnerWoodRegion = mFragment.findViewById(R.id.spinnerWoodRegionSymbol);
        ArrayAdapter woodRegionAdapter = createDefaultSpinnerAdapter(mWoodRegionList);
        mSpinnerWoodRegion.setAdapter(woodRegionAdapter);

        mEtRegionZone = mFragment.findViewById(R.id.etRegionZone);

        mSpinnerWoodCertification = mFragment.findViewById(R.id.spinnerWoodCertification);
        ArrayAdapter woodCertificationAdapter = createDefaultSpinnerAdapter(mWoodCertificationList);
        mSpinnerWoodCertification.setAdapter(woodCertificationAdapter);

        mEtObservations = mFragment.findViewById(R.id.edObservations);

        mEtDiscountPercentage = mFragment.findViewById(R.id.etDiscountPercentage);
        mEtDiscountPercentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUiDiscountValue();
            }
        });

        mTvDiscountValue = mFragment.findViewById(R.id.tvDiscountValue);

        mTvTotalValue = mFragment.findViewById(R.id.tvTotalValue);

        mCbNetTotalValue = mFragment.findViewById(R.id.cbNetTotalValue);
        mCbNetTotalValue.setChecked(true);

        // TODO update total when the checkbox changes

        mBtnSave = mFragment.findViewById(R.id.btnSave);

        mBtnDelete = mFragment.findViewById(R.id.btnDelete);
    }

    private void setupOnClickActions() {
        mBtnSave.setOnClickListener((source) -> persistAcquisitionChanges());

        mBtnDelete.setOnClickListener((source) -> {
            try {
                deleteCurrentAcquisition();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });


        DatePickerDialog.OnDateSetListener datePickListener = (view, year, month, dayOfMonth) -> {
            Date pickedDate = new Date(year - 1900, month, dayOfMonth);
            updateUiDate(pickedDate);
        };

        Calendar currentCalendar = Calendar.getInstance();

        mTvDate.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), datePickListener, currentCalendar.get(Calendar.YEAR),
                    currentCalendar.get(Calendar.MONTH),
                    currentCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        mTvSupplierName.setOnClickListener((source) -> {
            AlertDialog supplierListDialog = new AlertDialog.Builder(getContext()).create();
            LayoutInflater inflater = getActivity().getLayoutInflater();

            View supplierListDialogView = inflater.inflate(R.layout.supplier_dialog_fragment_acquisition, null);

            supplierListDialog.setView(supplierListDialogView);
            supplierListDialog.setTitle("Select the supplier");

            ListView supplierListView = supplierListDialogView.findViewById(R.id.lvSuppliers);

            supplierListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    supplierListDialog.dismiss();

                    // mSelectedSupplier = mSupplierList.get(position);

                    /*
                    we use getItemAtPosition instead of querying the list because the dialog
                    lets the user filter the list
                    */
                    mSelectedSupplier = (Supplier) supplierListView.getItemAtPosition(position);

                    mTvSupplierName.setText(mSelectedSupplier.getName());
                }
            });

            ArrayAdapter<Supplier> arrayAdapter = new ArrayAdapter<>(getActivity(),
                    android.R.layout.simple_list_item_1, mSupplierList);
            supplierListView.setAdapter(arrayAdapter);

            EditText etNamePrefix = supplierListDialogView.findViewById(R.id.etNamePrefix);

            etNamePrefix.addTextChangedListener(new TextWatcher() {
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
    }

    private void deleteCurrentAcquisition() throws SQLException {
        // TODO show dialog

        DeleteBuilder deleteBuilder = mDbHelper.getAcquisitionDao().deleteBuilder();
        deleteBuilder.where().eq(CommonFieldNames.ID, mModifiedAcquisition.getId());
        deleteBuilder.delete();

        Intent intent = new Intent(getContext(), AcquisitionListActivity.class);
        startActivity(intent);
    }

    /**
     * Sets {@link #mModifiedAcquisition} to the {@link Acquisition} instance corresponding to the
     * given id
     *
     * @param acquisitionId the {@link Acquisition#id} of the {@link Acquisition} instance
     */
    private void setModifiedAcquisition(int acquisitionId) {
        mModifiedAcquisition = DatabaseHelper.getLatestInstance().getAcquisitionDao()
                .queryForId(acquisitionId);

        syncUiWithAcquisition();
    }

    /**
     * Updates the UI inputs based on the values of {@link #mModifiedAcquisition}
     */
    private void syncUiWithAcquisition() {
        mEtSerialNumber.setText(mModifiedAcquisition.getSerialNumber());
        mEtRegionZone.setText(mModifiedAcquisition.getRegionZone());
        mEtObservations.setText(mModifiedAcquisition.getObservations());
        mEtDiscountPercentage.setText(String.valueOf(mModifiedAcquisition.getDiscountPercentage()));

        mSpinnerAcquirer.setSelection(mAcquirerList.indexOf(mModifiedAcquisition.getAcquirer()));
        mSpinnerWoodRegion.setSelection(mWoodRegionList.indexOf(mModifiedAcquisition.getWoodRegion()));
        mSpinnerWoodCertification.setSelection(mWoodCertificationList.indexOf(mModifiedAcquisition.getWoodCertification()));

        updateUiDate(mModifiedAcquisition.getReceptionDate());

        mTvSupplierName.setText(mModifiedAcquisition.getSupplier().getName());

        mCbNetTotalValue.setChecked(mModifiedAcquisition.isNet());
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
        double discountPercentage = Double.valueOf(mEtDiscountPercentage.getText().toString());

        acquisition.setSerialNumber(serialNumber);
        acquisition.setAcquirer(getSelectedAcquirer());
        acquisition.setSupplier(getSelectedSupplier());
        acquisition.setReceptionDate(getSelectedDate());
        acquisition.setAcquisitionStatus(getAcquisitionStatus());
        acquisition.setRegionZone(regionZone);
        acquisition.setWoodRegion(getSelectedWoodRegion());
        acquisition.setWoodCertification(getSelectedWoodCertification());
        acquisition.setObservations(observations);
        acquisition.setTotalValue(getTotalValue());
        acquisition.setTotalGrossVolume(0);
        acquisition.setTotalNetVolume(0);
        acquisition.setDiscountPercentage(discountPercentage);
        acquisition.setDiscountValue(getDiscountValue());
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
     * Updates the UI inputs with default values (current date, a valid acquisition serial number,
     * etc.)
     */
    private void updateUiWithDefaults() {
        int currentAcquisitionSerialNumber = getLastAcquisitionSerialNumber() + 1;
        mEtSerialNumber.setText(String.valueOf(currentAcquisitionSerialNumber));

        updateUiDate(new Date());
    }

    /**
     * Updates the date {@link TextView} from the UI, with information found in the given
     * {@link Date}
     *
     * @param date the {@link Date} to use when updating the date {@link TextView}
     */
    private void updateUiDate(Date date) {
        mTvDate.setText(mIsoDateFormat.format(date));
    }

    private void persistAcquisitionChanges() {
        // not working with an existing object
        if (mModifiedAcquisition == null) {
            Acquisition acquisition = createAcquisitionMatchingUi();
            DatabaseHelper.getLatestInstance().getAcquisitionDao().create(acquisition);

            saveAcquisitionSerialNumber(acquisition);

            PopupUtil.snackbar(getView(), "New acquisition persisted");

            mModifiedAcquisition = acquisition;

            updateDeleteButtonState();
        } else {
            syncAcquisitionWithUi(mModifiedAcquisition);
            DatabaseHelper.getLatestInstance().getAcquisitionDao().update(mModifiedAcquisition);

            PopupUtil.snackbar(getView(), "Updated existing acquisition");
        }
    }

    private Acquirer getSelectedAcquirer() {
        return (Acquirer) mSpinnerAcquirer.getSelectedItem();
    }

    private Supplier getSelectedSupplier() {
        // TODO not ok, there could be suppliers with identical names

        String selectedSupplierName = mTvSupplierName.getText().toString();

        try {
            return mDbHelper.getSupplierDao().queryBuilder()
                    .where().eq(CommonFieldNames.NAME, selectedSupplierName)
                    .queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Date getSelectedDate() {
        try {
            return mIsoDateFormat.parse(mTvDate.getText().toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Programming error!!!");
        }
    }

    private AcquisitionStatus getAcquisitionStatus() {
        // statuses seem to be unused, so pick the first one
        try {
            return mDbHelper.getAcquisitionStatusDao().queryBuilder().queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private WoodRegion getSelectedWoodRegion() {
        return (WoodRegion) mSpinnerWoodRegion.getSelectedItem();
    }

    private WoodCertification getSelectedWoodCertification() {
        return (WoodCertification) mSpinnerWoodCertification.getSelectedItem();
    }

    /**
     * Sets the value of the {@link #LAST_ACQUISITION_ID_PROP} property to match the
     * {@link Acquisition#serialNumber} of the given {@link Acquisition} instance
     *
     * @param currentAcquisition the {@link Acquisition} instance to use a the source
     */
    private void saveAcquisitionSerialNumber(Acquisition currentAcquisition) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putInt(LAST_ACQUISITION_ID_PROP, Integer.valueOf(
                currentAcquisition.getSerialNumber()));

        editor.apply();
    }

    /**
     * Gets the value of the {@link #LAST_ACQUISITION_ID_PROP} property
     *
     * @return the value of the {@link #LAST_ACQUISITION_ID_PROP} property
     */
    private int getLastAcquisitionSerialNumber() {
        return getSharedPreferences().getInt(LAST_ACQUISITION_ID_PROP, 0);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        ArrayAdapter<T> arrayAdapter = new ArrayAdapter<T>(getContext(),
                android.R.layout.simple_spinner_item, modelInstanceList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return arrayAdapter;
    }

    /**
     * Gets the discount value, based on {@link #mEtDiscountPercentage} and {@link #getTotalValue()}
     * If {@link #mEtDiscountPercentage} is empty / null, 0 is returned
     *
     * @return the discount value, based on {@link #mEtDiscountPercentage} and {@link #getTotalValue()}
     */
    private double getDiscountValue() {
        double discountPercentage;

        try {
            discountPercentage = Double.valueOf(mEtDiscountPercentage.getText().toString());
        } catch (NumberFormatException e) {
            // happens only when the EditText is empty, since
            return 0;
        }

        double totalValue = getTotalValue();

        return discountPercentage / 100 * totalValue;
    }

    private double getTotalValue() {
        List<AcquisitionItem> acquisitionItemList;

        List<LogPrice> logPriceList;

        try {
            acquisitionItemList = mDbHelper.getAcquisitionItemDao().queryBuilder()
                    .where()
                    .eq(CommonFieldNames.ACQUISITION_ID, mModifiedAcquisition.getId())
                    .and()
                    .eq(CommonFieldNames.IS_SPECIAL_PRICE, 0)
                    .query();

            logPriceList = mDbHelper.getLogPriceDao().queryBuilder()
                    .where()
                    .eq(CommonFieldNames.ACQUISITION_ID, mModifiedAcquisition.getId())
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        double totalValue = acquisitionItemList.stream()
                .mapToDouble(AcquisitionItem::getPrice)
                .sum();

        totalValue += logPriceList.stream()
                .mapToDouble(logPrice -> logPrice.getPrice() * logPrice.getQuantity())
                .sum();

        return totalValue;
    }

    /**
     * Called when the visibility of the fragment changes
     *
     * @param isVisibleToUser if true, the fragment is visible to the user
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        /*
        when isResumed() == false, onCreateView() has yet to run, thus UI objects are not
        instantiated, thus, there's nothing to update
        */
        if (!isResumed()) {
            return;
        }

        if (isVisibleToUser) {
            updateUiDiscountValue();
            updateTotalValue();
        }
    }

    /**
     * Updates {@link #mTvDiscountValue} with the value provided by {@link #getDiscountValue()}
     */
    private void updateUiDiscountValue() {
        double discountValue = getDiscountValue();
        mTvDiscountValue.setText(String.valueOf(discountValue));
    }

    private void updateTotalValue() {
        mTvTotalValue.setText(String.valueOf(getTotalValue()));
    }

    private SharedPreferences getSharedPreferences() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }
}
