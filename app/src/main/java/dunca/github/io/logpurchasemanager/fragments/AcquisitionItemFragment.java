package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import dunca.github.io.logpurchasemanager.data.model.LogDiameterClass;
import dunca.github.io.logpurchasemanager.data.model.LogPrice;
import dunca.github.io.logpurchasemanager.data.model.LogQualityClass;
import dunca.github.io.logpurchasemanager.data.model.TreeSpecies;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;
import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;
import dunca.github.io.logpurchasemanager.fragments.interfaces.SmartFragment;
import dunca.github.io.logpurchasemanager.fragments.util.FragmentUtil;

public class AcquisitionItemFragment extends SmartFragment {
    private static final String NO_ACQUISITION_MESSAGE = "Create an acquisition first...";

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

    // set to a dummy LogDiameterClass, since the feature is unused
    private final LogDiameterClass mDummyLogDiameterClass = new LogDiameterClass("", "", 0, 0);

    public AcquisitionItemFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    public static AcquisitionItemFragment newInstance(int acquisitionItemId) {
        AcquisitionItemFragment fragment = new AcquisitionItemFragment();

        Bundle args = new Bundle();

        args.putInt(MethodParameterConstants.ACQUISITION_ITEM_ID_PARAM, acquisitionItemId);

        fragment.setArguments(args);

        return fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentView = inflater.inflate(R.layout.fragment_acquisition_item, container, false);

        if (AcquisitionFragment.sCurrentAcquisitionId == MethodParameterConstants.INVALID_INDEX) {
            View placeholderView = inflater.inflate(R.layout.placeholder_layout, container, false);
            ((TextView) placeholderView.findViewById(R.id.tvContent)).setText(NO_ACQUISITION_MESSAGE);

            return placeholderView;
        }

        initUi();

        return mFragmentView;
    }

    @Override
    public void onVisible() {
        if (AcquisitionFragment.sCurrentAcquisitionId == MethodParameterConstants.INVALID_INDEX) {
            return;
        }

        /*
        re-attaching the fragment recreates the view, thus we'll be able to render it properly
        since we now have an acquisition id
        */
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    private void initUi() {
        initDbLists();

        initViews();
        setupOnClickActions();

        mAcquisition = mDbHelper.getAcquisitionDao().queryForId(AcquisitionFragment.sCurrentAcquisitionId);

        int acquisitionItemId = getArguments().getInt(MethodParameterConstants.ACQUISITION_ITEM_ID_PARAM);

        if (acquisitionItemId != MethodParameterConstants.INVALID_INDEX) {
            mExistingAcquisitionItem = mDbHelper.getAcquisitionItemDao().queryForId(acquisitionItemId);

            syncUiWithAcquisitionItem();
        }
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
        updateUiPriceFormState();

        mEtGrossLength = findViewById(R.id.etGrossLength);
        mEtGrossLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUiGrossVolume();
            }
        });

        mEtGrossDiameter = findViewById(R.id.etGrossDiameter);
        mEtGrossDiameter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUiGrossVolume();
            }
        });

        mEtNetLength = findViewById(R.id.etNetLength);
        mEtNetLength.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUiNetVolume();
            }
        });

        mEtNetDiameter = findViewById(R.id.etNetDiameter);
        mEtNetDiameter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateUiNetVolume();
            }
        });

        mTvGrossVolume = findViewById(R.id.tvGrossVolume);
        mTvNetVolume = findViewById(R.id.tvNetVolume);

        mEtObservations = findViewById(R.id.etObservations);

        mBtnSave = findViewById(R.id.btnSave);
    }

    private void setupOnClickActions() {
        mBtnSave.setOnClickListener(v -> persistAcquisitionItemChanges());

        mCbSpecialPrice.setOnCheckedChangeListener((view, state) -> updateUiPriceFormState());
    }

    private void persistAcquisitionItemChanges() {
        if (mExistingAcquisitionItem == null) {
            // inserting new acquisition item
            AcquisitionItem acquisitionItem = createAcquisitionItemMatchingUi();

            mDbHelper.getAcquisitionItemDao().create(acquisitionItem);
            mExistingAcquisitionItem = acquisitionItem;

            if (!mExistingAcquisitionItem.isSpecialPrice()) {
                LogPrice logPrice = new LogPrice(mExistingAcquisitionItem.getAcquisition(),
                        mExistingAcquisitionItem.getAcquirer(),
                        mExistingAcquisitionItem.getTreeSpecies(),
                        mExistingAcquisitionItem.getLogQualityClass(),
                        mDummyLogDiameterClass, 0, 1, false);

                mDbHelper.getLogPriceDao().create(logPrice);

                PopupUtil.snackbar(mFragmentView, "New log price persisted");
            }

            PopupUtil.snackbar(mFragmentView, "New acquisition item persisted");
        } else {
            syncAcquisitionItemWithUi(mExistingAcquisitionItem);

            mDbHelper.getAcquisitionItemDao().update(mExistingAcquisitionItem);

            // TODO what if isSpecialPrice changes???

            PopupUtil.snackbar(mFragmentView, "Updated existing acquisition item");
        }
    }

    private AcquisitionItem createAcquisitionItemMatchingUi() {
        AcquisitionItem acquisitionItem = new AcquisitionItem();

        acquisitionItem.setAcquisition(mAcquisition);
        acquisitionItem.setAcquirer(mAcquisition.getAcquirer());
        acquisitionItem.setLogDiameterClass(mDummyLogDiameterClass);

        syncAcquisitionItemWithUi(acquisitionItem);
        return acquisitionItem;
    }

    private void syncAcquisitionItemWithUi(AcquisitionItem acquisitionItem) {
        acquisitionItem.setTreeSpecies(getSelectedTreeSpecies());
        acquisitionItem.setLogQualityClass(getSelectedQualityClass());

        acquisitionItem.setLogBarCode(mEtBarCode.getText().toString());

        acquisitionItem.setSpecialPrice(mCbSpecialPrice.isChecked());

        double price;

        try {
            price = Double.valueOf(mEtVolumetricPrice.getText().toString());
        } catch (NumberFormatException e) {
            price = 0;
        }
        acquisitionItem.setPrice(price);

        acquisitionItem.setGrossLength(Double.valueOf(mEtGrossLength.getText().toString()));
        acquisitionItem.setGrossDiameter(Double.valueOf(mEtGrossDiameter.getText().toString()));

        acquisitionItem.setNetLength(Double.valueOf(mEtNetLength.getText().toString()));
        acquisitionItem.setNetDiameter(Double.valueOf(mEtNetDiameter.getText().toString()));

        acquisitionItem.setGrossVolume(Double.valueOf(mTvGrossVolume.getText().toString()));
        acquisitionItem.setNetVolume(Double.valueOf(mTvNetVolume.getText().toString()));

        acquisitionItem.setObservations(mEtObservations.getText().toString());
    }

    private void syncUiWithAcquisitionItem() {
        mSpinnerSpecies.setSelection(mSpeciesList.indexOf(mExistingAcquisitionItem.getTreeSpecies()));
        mSpinnerQualityClass.setSelection(mLogQualityClassList.indexOf(mExistingAcquisitionItem.getLogQualityClass()));

        mEtBarCode.setText(mExistingAcquisitionItem.getLogBarCode());

        mCbSpecialPrice.setChecked(mExistingAcquisitionItem.isSpecialPrice());

        mEtVolumetricPrice.setText(String.valueOf(mExistingAcquisitionItem.getPrice()));

        mEtGrossLength.setText(String.valueOf(mExistingAcquisitionItem.getGrossLength()));
        mEtGrossDiameter.setText(String.valueOf(mExistingAcquisitionItem.getGrossDiameter()));

        mEtNetLength.setText(String.valueOf(mExistingAcquisitionItem.getNetLength()));
        mEtNetDiameter.setText(String.valueOf(mExistingAcquisitionItem.getNetDiameter()));

        mTvGrossVolume.setText(String.valueOf(mExistingAcquisitionItem.getGrossVolume()));
        mTvNetVolume.setText(String.valueOf(mExistingAcquisitionItem.getNetVolume()));

        mEtObservations.setText(mExistingAcquisitionItem.getObservations());
    }

    private TreeSpecies getSelectedTreeSpecies() {
        return (TreeSpecies) mSpinnerSpecies.getSelectedItem();
    }

    private LogQualityClass getSelectedQualityClass() {
        return (LogQualityClass) mSpinnerQualityClass.getSelectedItem();
    }

    private void updateUiGrossVolume() {
        mTvGrossVolume.setText(String.valueOf(calculateGrossVolume()));
    }

    private void updateUiNetVolume() {
        mTvNetVolume.setText(String.valueOf(calculateNetVolume()));
    }

    private double calculateGrossVolume() {
        double grossLength;
        double grossDiameter;

        try {
            grossLength = Double.valueOf(mEtGrossLength.getText().toString());
            grossDiameter = Double.valueOf(mEtGrossDiameter.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }

        return calculateVolume(grossLength, grossDiameter);
    }

    private double calculateNetVolume() {
        double netLength;
        double netDiameter;

        try {
            netLength = Double.valueOf(mEtNetLength.getText().toString());
            netDiameter = Double.valueOf(mEtNetDiameter.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }

        return calculateVolume(netLength, netDiameter);
    }

    private void updateUiPriceFormState() {
        mEtVolumetricPrice.setEnabled(mCbSpecialPrice.isChecked());
    }

    /**
     * Calculates the log volume using: pi*(diameter*10^2/2)^2*length
     *
     * @param length   the log's length
     * @param diameter the log's diameter
     * @return the log's volume
     */
    private double calculateVolume(double length, double diameter) {
        return Math.PI * Math.pow(diameter * Math.pow(10, 2) / 2, 2) * length;
    }

    private <T extends View> T findViewById(int id) {
        return mFragmentView.findViewById(id);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        return FragmentUtil.createDefaultSpinnerAdapter(getContext(), modelInstanceList);
    }
}
