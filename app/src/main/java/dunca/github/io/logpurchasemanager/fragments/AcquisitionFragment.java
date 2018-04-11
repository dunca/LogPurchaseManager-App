package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquirer;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionStatus;
import dunca.github.io.logpurchasemanager.data.model.Supplier;
import dunca.github.io.logpurchasemanager.data.model.WoodCertification;
import dunca.github.io.logpurchasemanager.data.model.WoodRegion;

public class AcquisitionFragment extends Fragment {
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
        mEtSerialNumber = mFragment.findViewById(R.id.etSerialNumber);

        mSpinnerAcquirer = mFragment.findViewById(R.id.spinnerAcquirer);

        mTvDate = mFragment.findViewById(R.id.tvDate);
        mTvSupplierName = mFragment.findViewById(R.id.tvSupplierName);

        mSpinnerWoodRegion = mFragment.findViewById(R.id.spinnerWoodRegionSymbol);

        mEtRegionZone = mFragment.findViewById(R.id.etRegionZone);

        mSpinnerWoodCertification = mFragment.findViewById(R.id.spinnerWoodCertification);

        mEtObservations = mFragment.findViewById(R.id.edObservations);

        mEtDiscountPercentage = mFragment.findViewById(R.id.etDiscountPercentage);

        mBtnSave = mFragment.findViewById(R.id.btnSave);
    }

    public static AcquisitionFragment newInstance() {
        AcquisitionFragment fragment = new AcquisitionFragment();

        Bundle args = new Bundle();
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

        return mFragment;
    }

    private void setupOnClickActions() {
        mBtnSave.setOnClickListener((source) -> persistAcquisitionChanges());
    }

    private void persistAcquisitionChanges() {
        String serialNumber = mEtSerialNumber.getText().toString();
        String regionZone = mEtRegionZone.getText().toString();
        String observations = mEtObservations.getText().toString();
        double discountPercentage = Double.valueOf(mEtDiscountPercentage.getText().toString());

        Acquisition acquisition = new Acquisition(serialNumber, getSelectedAcquirer(),
                getSelectedSupplier(), getSelectedDate(), getAcquisitionStatus(), regionZone,
                getSelectedWoodRegion(), getSelectedWoodCertification(), observations,
                0, 0, 0, discountPercentage, 0, false, false);


        // TODO, if its an existing acquisition, modify it

        DatabaseHelper.getLatestInstance().getAcquisitionDao().create(acquisition);

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
}
