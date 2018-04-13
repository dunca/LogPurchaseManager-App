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
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
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

    public static AcquisitionItemFragment newInstance(int acquisitionId) {
        AcquisitionItemFragment fragment = new AcquisitionItemFragment();

        Bundle args = new Bundle();

        if (acquisitionId != MethodParameterConstants.NO_ELEMENT_INDEX) {
            args.putInt(MethodParameterConstants.ACQUISITION_ID_PARAM, acquisitionId);
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

        if (acquisitionId != 0) {
            // the user is trying to add an item to an existing acquisition

        } else {

        }

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
        // TODO
    }

    private <T extends View> T findViewById(int id) {
        return mFragmentView.findViewById(id);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        return FragmentUtil.createDefaultSpinnerAdapter(getContext(), modelInstanceList);
    }
}
