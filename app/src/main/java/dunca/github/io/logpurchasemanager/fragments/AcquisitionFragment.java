package dunca.github.io.logpurchasemanager.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionStatus;
import dunca.github.io.logpurchasemanager.data.model.Supplier;
import dunca.github.io.logpurchasemanager.data.model.WoodCertification;
import dunca.github.io.logpurchasemanager.data.model.WoodRegion;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;
import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;

public class AcquisitionFragment extends Fragment {
    public static final String ACQUISITION_ID_PARAM = "acquisition_id_param";

    private static final String LAST_ACQUISITION_ID_PROP = "last_acquisition_id_prop";

    private Acquisition mModifiedAcquisition;

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
    private Button mBtnSave;

    public AcquisitionFragment() {

    }

    private void initViews() {
        DatabaseHelper dbHelper = DatabaseHelper.getLatestInstance();

        mEtSerialNumber = mFragment.findViewById(R.id.etSerialNumber);
        mEtSerialNumber.setText(String.valueOf(getLastAcquisitionSerialNumber() + 1));

        mSpinnerAcquirer = mFragment.findViewById(R.id.spinnerAcquirer);
        List<Acquirer> acquirerList = dbHelper.getAcquirerDao().queryForAll();
        ArrayAdapter acquirerAdapter = createDefaultSpinnerAdapter(acquirerList);
        mSpinnerAcquirer.setAdapter(acquirerAdapter);


        mTvDate = mFragment.findViewById(R.id.tvDate);
        mTvSupplierName = mFragment.findViewById(R.id.tvSupplierName);

        mSpinnerWoodRegion = mFragment.findViewById(R.id.spinnerWoodRegionSymbol);
        List<WoodRegion> woodRegionList = dbHelper.getWoodRegionDao().queryForAll();
        ArrayAdapter woodRegionAdapter = createDefaultSpinnerAdapter(woodRegionList);
        mSpinnerWoodRegion.setAdapter(woodRegionAdapter);

        mEtRegionZone = mFragment.findViewById(R.id.etRegionZone);

        mSpinnerWoodCertification = mFragment.findViewById(R.id.spinnerWoodCertification);
        List<WoodCertification> woodCertificationList;

        try {
            woodCertificationList = dbHelper.getWoodCertificationDao()
                    .queryBuilder().orderBy(CommonFieldNames.LIST_PRIORITY, true)
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ArrayAdapter woodCertificationAdapter = createDefaultSpinnerAdapter(woodCertificationList);
        mSpinnerWoodCertification.setAdapter(woodCertificationAdapter);

        mEtObservations = mFragment.findViewById(R.id.edObservations);

        mEtDiscountPercentage = mFragment.findViewById(R.id.etDiscountPercentage);

        mBtnSave = mFragment.findViewById(R.id.btnSave);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if (getArguments() != null) {
        //     mParam1 = getArguments().getString(ARG_PARAM1);
        //     mParam2 = getArguments().getString(ARG_PARAM2);
        // }
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

        return mFragment;
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
        // TODO update ui with data from mModifiedAcquisition


    }

    /**
     * Updates the UI inputs with default values (current date, a valid acquisition serial number,
     * etc.)
     */
    private void updateUiWithDefaults() {
        int currentAcquisitionSerialNumber = getLastAcquisitionSerialNumber() + 1;
        mEtSerialNumber.setText(String.valueOf(currentAcquisitionSerialNumber));

        DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        mTvDate.setText(isoDateFormat.format(currentDate));
    }

    private void setupOnClickActions() {
        mBtnSave.setOnClickListener((source) -> persistAcquisitionChanges());
    }

    private void persistAcquisitionChanges() {
        String serialNumber = mEtSerialNumber.getText().toString();
        String regionZone = mEtRegionZone.getText().toString();
        String observations = mEtObservations.getText().toString();
        double discountPercentage = Double.valueOf(mEtDiscountPercentage.getText().toString());

        // not working with an existing object
        if (mModifiedAcquisition == null) {
            Acquisition acquisition = new Acquisition(serialNumber, getSelectedAcquirer(),
                    getSelectedSupplier(), getSelectedDate(), getAcquisitionStatus(), regionZone,
                    getSelectedWoodRegion(), getSelectedWoodCertification(), observations,
                    0, 0, 0, discountPercentage, 0, false, false);


            DatabaseHelper.getLatestInstance().getAcquisitionDao().create(acquisition);
        } else {
            // TODO update all fields


            DatabaseHelper.getLatestInstance().getAcquisitionDao().update(mModifiedAcquisition);
        }


        // TODO add notifications
    }

    private Acquirer getSelectedAcquirer() {
        return null;
    }

    private Supplier getSelectedSupplier() {
        return null;
    }

    private Date getSelectedDate() {
        return null;
    }

    private AcquisitionStatus getAcquisitionStatus() {
        return null;
    }

    private WoodRegion getSelectedWoodRegion() {
        return null;
    }

    private WoodCertification getSelectedWoodCertification() {
        return null;
    }

    private void saveAcquisitionSerialNumber(Acquisition currentAcquisition) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putInt(LAST_ACQUISITION_ID_PROP, Integer.valueOf(
                currentAcquisition.getSerialNumber()));
    }

    private int getLastAcquisitionSerialNumber() {
        return getSharedPreferences().getInt(LAST_ACQUISITION_ID_PROP, 0);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        ArrayAdapter<T> arrayAdapter = new ArrayAdapter<T>(getContext(),
                android.R.layout.simple_spinner_item, modelInstanceList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return arrayAdapter;
    }

    private SharedPreferences getSharedPreferences() {
        return getActivity().getPreferences(Context.MODE_PRIVATE);
    }
}
