package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.Acquisition;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionItem;
import dunca.github.io.logpurchasemanager.data.model.LogQualityClass;
import dunca.github.io.logpurchasemanager.data.model.TreeSpecies;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;
import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import dunca.github.io.logpurchasemanager.fragments.util.FragmentUtil;

public class AcquisitionItemFragment extends Fragment {

    private View mFragmentView;

    private Spinner mSpinnerSpecies;
    private Spinner mSpinnerQualityClass;
    private EditText mEtBarCode;
    private CheckBox mCbSpecialPrice;
    private EditText mEtVolumetricPrice;
    private EditText mEtGrossLength;
    private EditText mEtGrossDiameter;
    private EditText mEtNetLength;
    private EditText mEtNetDiameter;
    private TextView mTvGrossVolume;
    private TextView mTvNetVolume;
    private EditText mEtObservations;
    private Button mBtnSave;

    private final DatabaseHelper mDbHelper;
    private List<TreeSpecies> mSpeciesList;
    private List<LogQualityClass> mLogQualityClassList;

    private Acquisition mAcquisition;
    private AcquisitionItem mExistingAcquisitionItem;

    public AcquisitionItemFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();

        initDbLists();
    }

    private void initDbLists() {
        try {
            mSpeciesList = mDbHelper.getTreeSpeciesDao().queryBuilder()
                    .orderBy(CommonFieldNames.LIST_PRIORITY, true)
                    .query();

            mLogQualityClassList = mDbHelper.getLogQualityClassDao().queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static AcquisitionItemFragment newInstance(int acquisitionId, int acquisitionItemId) {
        AcquisitionItemFragment fragment = new AcquisitionItemFragment();

        Bundle args = new Bundle();

        if (acquisitionId != MethodParameterConstants.NO_ELEMENT_INDEX) {
            args.putInt(MethodParameterConstants.ACQUISITION_ID_PARAM, acquisitionId);
        } else {
            throw new RuntimeException("Acquisition is required");
        }

        if (acquisitionItemId != MethodParameterConstants.NO_ELEMENT_INDEX) {
            args.putInt(MethodParameterConstants.ACQUISITION_ITEM_ID_PARAM, acquisitionItemId);
        }

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mFragmentView = inflater.inflate(R.layout.fragment_acquisition_item, container, false);

        initViews();
        setupOnClickActions();

        Bundle args = getArguments();

        int acquisitionId = args.getInt(MethodParameterConstants.ACQUISITION_ID_PARAM, 0);
        mAcquisition = mDbHelper.getAcquisitionDao().queryForId(acquisitionId);

        int acquisitionItemId = args.getInt(MethodParameterConstants.ACQUISITION_ITEM_ID_PARAM, 0);
        mExistingAcquisitionItem = mDbHelper.getAcquisitionItemDao().queryForId(acquisitionItemId);

        return mFragmentView;
    }

    private void initViews() {
        mSpinnerSpecies = findViewById(R.id.spinnerSpecies);
        ArrayAdapter speciesAdapter = createDefaultSpinnerAdapter(mSpeciesList);
        mSpinnerSpecies.setAdapter(speciesAdapter);

        mSpinnerQualityClass = findViewById(R.id.spinnerQualityClass);
        ArrayAdapter qualityClassAdapter = createDefaultSpinnerAdapter(mLogQualityClassList);
        mSpinnerQualityClass.setAdapter(qualityClassAdapter);

        mEtBarCode = findViewById(R.id.etBarCode);

        mCbSpecialPrice = findViewById(R.id.cbSpecialPrice);
        mEtVolumetricPrice = findViewById(R.id.etPrice);

        mEtGrossLength = findViewById(R.id.etGrossLength);
        mEtGrossDiameter = findViewById(R.id.etGrossDiameter);

        mEtNetLength = findViewById(R.id.etNetLength);
        mEtNetDiameter = findViewById(R.id.etNetDiameter);

        mTvGrossVolume = findViewById(R.id.tvGrossVolume);
        mTvNetVolume = findViewById(R.id.tvNetVolume);

        mEtObservations = findViewById(R.id.etObservations);

        mBtnSave = findViewById(R.id.btnSave);
    }

    private void setupOnClickActions() {
        mBtnSave.setOnClickListener(v -> persistAcquisitionItemChanges());
    }

    private void persistAcquisitionItemChanges() {
        if (mExistingAcquisitionItem == null) {
            // inserting new acquisition item
            AcquisitionItem acquisitionItem = createAcquisitionItemMatchingUi();

            mDbHelper.getAcquisitionItemDao().create(acquisitionItem);
            mExistingAcquisitionItem = acquisitionItem;

            PopupUtil.snackbar(mFragmentView, "New acquisition item persisted");
        } else {

        }
    }

    private <T extends View> T findViewById(int id) {
        return mFragmentView.findViewById(id);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        return FragmentUtil.createDefaultSpinnerAdapter(getContext(), modelInstanceList);
    }

    private AcquisitionItem createAcquisitionItemMatchingUi() {
        AcquisitionItem acquisitionItem = new AcquisitionItem();
        syncAcquisitionItemWithUi(acquisitionItem);
        return acquisitionItem;
    }

    private void syncAcquisitionItemWithUi(AcquisitionItem acquisitionItem) {
        acquisitionItem.setTreeSpecies(getSelectedTreeSpecies());
        acquisitionItem.setLogQualityClass(getSelectedQualityClass());

        acquisitionItem.setLogBarCode(mEtBarCode.getText().toString());

        acquisitionItem.setSpecialPrice(mCbSpecialPrice.isChecked());
        acquisitionItem.setPrice(Double.valueOf(mEtVolumetricPrice.getText().toString()));

        acquisitionItem.setGrossLength(Double.valueOf(mEtGrossLength.getText().toString()));
        acquisitionItem.setGrossDiameter(Double.valueOf(mEtGrossDiameter.getText().toString()));

        acquisitionItem.setNetLength(Double.valueOf(mEtNetLength.getText().toString()));
        acquisitionItem.setNetDiameter(Double.valueOf(mEtNetDiameter.getText().toString()));

        acquisitionItem.setGrossVolume(Double.valueOf(mTvGrossVolume.getText().toString()));
        acquisitionItem.setNetVolume(Double.valueOf(mTvNetVolume.getText().toString()));

        acquisitionItem.setObservations(mEtObservations.getText().toString());
    }

    private TreeSpecies getSelectedTreeSpecies() {
        return (TreeSpecies) mSpinnerSpecies.getSelectedItem();
    }

    private LogQualityClass getSelectedQualityClass() {
        return (LogQualityClass) mSpinnerQualityClass.getSelectedItem();
    }


}
