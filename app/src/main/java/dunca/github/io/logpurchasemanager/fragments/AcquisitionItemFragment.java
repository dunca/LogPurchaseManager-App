package dunca.github.io.logpurchasemanager.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
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

import com.j256.ormlite.stmt.QueryBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.sql.SQLException;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.activities.BarCodeScannerActivity;
import dunca.github.io.logpurchasemanager.activities.util.InputValidationUtil;
import dunca.github.io.logpurchasemanager.activities.util.PopupUtil;
import dunca.github.io.logpurchasemanager.activities.util.StringFormatUtil;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionItemIdEvent;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionTotalPriceUpdateRequestEvent;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionTotalVolumeUpdateRequestEvent;
import dunca.github.io.logpurchasemanager.fragments.interfaces.SmartFragment;
import dunca.github.io.logpurchasemanager.fragments.util.FragmentUtil;
import io.github.dunca.logpurchasemanager.shared.model.Acquisition;
import io.github.dunca.logpurchasemanager.shared.model.AcquisitionItem;
import io.github.dunca.logpurchasemanager.shared.model.LogPrice;
import io.github.dunca.logpurchasemanager.shared.model.LogQualityClass;
import io.github.dunca.logpurchasemanager.shared.model.TreeSpecies;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;
import io.github.dunca.logpurchasemanager.shared.model.interfaces.Model;

import static android.app.Activity.RESULT_OK;

public class AcquisitionItemFragment extends SmartFragment {
    private static final int BAR_CODE_RESULT = 1;
    public static final String EXTRA_BAR_CODE = "extra_bar_code";

    private ViewPager mViewPager;
    private View mFragmentView;
    private boolean mReceivedAcquisitionItemId;

    private TextView mTvNoAcquisitionsPlaceholder;
    private View mRootLayout;
    private Spinner mSpinnerSpecies;
    private Spinner mSpinnerQualityClass;

    private TextInputLayout mTilBarCode;
    private TextInputLayout mTilVolumetricPrice;
    private TextInputLayout mTilGrossLength;
    private TextInputLayout mTilGrossDiameter;
    private TextInputLayout mTilNetLength;
    private TextInputLayout mTilNetDiameter;
    private TextInputLayout mTilObservations;

    private EditText mEtBarCode;
    private Button mBtnScanLogBarCode;
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
    private Button mBtnDelete;

    private final DatabaseHelper mDbHelper;

    private List<TreeSpecies> mSpeciesList;
    private List<LogQualityClass> mLogQualityClassList;

    private Acquisition mAcquisition;
    private AcquisitionItem mExistingAcquisitionItem;

    public AcquisitionItemFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    private void initDbLists() {
        try {
            mSpeciesList = mDbHelper.getTreeSpeciesDao().queryBuilder()
                    .orderBy(CommonFieldNames.LIST_PRIORITY, true).query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        mLogQualityClassList = mDbHelper.getLogQualityClassDao().queryForAll();
    }

    public static AcquisitionItemFragment newInstance() {
        return new AcquisitionItemFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewPager = getActivity().findViewById(R.id.fragment_container);

        mFragmentView = inflater.inflate(R.layout.fragment_acquisition_item, container, false);

        initUi();

        return mFragmentView;
    }

    @Override
    public void onVisible() {
        if (AcquisitionFragment.sCurrentAcquisitionId == MethodParameterConstants.INVALID_INDEX) {
            mTvNoAcquisitionsPlaceholder.setVisibility(View.VISIBLE);
            mRootLayout.setVisibility(View.GONE);
            return;
        }

        mAcquisition = mDbHelper.getAcquisitionDao().queryForId(AcquisitionFragment.sCurrentAcquisitionId);

        mTvNoAcquisitionsPlaceholder.setVisibility(View.GONE);
        mRootLayout.setVisibility(View.VISIBLE);

        if (mReceivedAcquisitionItemId) {
            syncUiWithAcquisitionItem();
            mReceivedAcquisitionItemId = false;
        } else {
            mExistingAcquisitionItem = null;
            resetUi();
        }

        updateDeleteButtonState();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == BAR_CODE_RESULT) {
            String scannerBarCode = data.getStringExtra(EXTRA_BAR_CODE);
            mEtBarCode.setText(scannerBarCode);
        }
    }

    private void updateDeleteButtonState() {
        mBtnDelete.setEnabled(mExistingAcquisitionItem != null);
    }

    private void resetUi() {
        mSpinnerSpecies.setSelection(0);
        mSpinnerQualityClass.setSelection(0);

        mEtBarCode.setText("");
        mCbSpecialPrice.setChecked(false);
        mEtVolumetricPrice.setText("");
        updateUiPriceFormState();

        mEtGrossLength.setText("");
        mEtGrossDiameter.setText("");
        mEtNetLength.setText("");
        mEtNetDiameter.setText("");
        mTvGrossVolume.setText(R.string.zero_float);
        mTvNetVolume.setText(R.string.zero_float);
        mEtObservations.setText("");
    }

    @Subscribe
    public void onAcquisitionItemIdEvent(AcquisitionItemIdEvent event) {
        int acquisitionItemId = event.getAcquisitionItemId();
        initExistingAcquisitionItem(acquisitionItemId);
        mReceivedAcquisitionItemId = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    private void initUi() {
        initDbLists();

        initViews();
        setupOnClickActions();
    }

    private void initExistingAcquisitionItem(int acquisitionItemId) {
        mExistingAcquisitionItem = mDbHelper.getAcquisitionItemDao().queryForId(acquisitionItemId);
    }

    private void initViews() {
        mTvNoAcquisitionsPlaceholder = findViewById(R.id.tvNoAcquisitionPlaceholder);
        mRootLayout = findViewById(R.id.rootLayout);

        mSpinnerSpecies = findViewById(R.id.spinnerSpecies);
        ArrayAdapter speciesAdapter = createDefaultSpinnerAdapter(mSpeciesList);
        mSpinnerSpecies.setAdapter(speciesAdapter);

        mSpinnerQualityClass = findViewById(R.id.spinnerQualityClass);
        ArrayAdapter qualityClassAdapter = createDefaultSpinnerAdapter(mLogQualityClassList);
        mSpinnerQualityClass.setAdapter(qualityClassAdapter);

        mTilBarCode = findViewById(R.id.tilBarCode);
        mEtBarCode = findViewById(R.id.etBarCode);

        mBtnScanLogBarCode = findViewById(R.id.btnScanLogBarCode);

        mCbSpecialPrice = findViewById(R.id.cbSpecialPrice);

        mTilVolumetricPrice = findViewById(R.id.tilVolumetricPrice);
        mEtVolumetricPrice = findViewById(R.id.etVolumetricPrice);
        updateUiPriceFormState();

        mTilGrossLength = findViewById(R.id.tilGrossLength);
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

        mTilGrossDiameter = findViewById(R.id.tilGrossDiameter);
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

        mTilNetLength = findViewById(R.id.tilNetLength);
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

        mTilNetDiameter = findViewById(R.id.tilNetDiameter);
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

        mTilObservations = findViewById(R.id.tilObservations);
        mEtObservations = findViewById(R.id.etObservations);

        mBtnSave = findViewById(R.id.btnSave);
        mBtnDelete = findViewById(R.id.btnDelete);
    }

    private void setupOnClickActions() {
        mBtnScanLogBarCode.setOnClickListener(v -> startBarCodeScanningActivity());

        mBtnSave.setOnClickListener(v -> persistAcquisitionItemChanges());
        mBtnDelete.setOnClickListener(v -> deleteCurrentAcquisitionItem());

        mCbSpecialPrice.setOnCheckedChangeListener((view, state) -> updateUiPriceFormState());
    }

    private void startBarCodeScanningActivity() {
        Intent intent = new Intent(getContext(), BarCodeScannerActivity.class);
        startActivityForResult(intent, BAR_CODE_RESULT);
    }

    private void deleteCurrentAcquisitionItem() {
        if (!mExistingAcquisitionItem.isSpecialPrice()) {
            decrementLogPriceQuantity();
        }

        mDbHelper.getAcquisitionItemDao().deleteById(mExistingAcquisitionItem.getId());

        // switch to the acquisition item list tab
        mViewPager.setCurrentItem(1);

        postAcquisitionUpdateEvents();
    }

    private void persistAcquisitionItemChanges() {
        if (!inputFormsAreValid()) {
            return;
        }

        if (mExistingAcquisitionItem == null) {
            // inserting new acquisition item
            AcquisitionItem acquisitionItem = createAcquisitionItemMatchingUi();

            mDbHelper.getAcquisitionItemDao().create(acquisitionItem);
            acquisitionItem.setAppAllocatedId(acquisitionItem.getId());
            mDbHelper.getAcquisitionItemDao().update(acquisitionItem);

            mExistingAcquisitionItem = acquisitionItem;

            if (!acquisitionItem.isSpecialPrice()) {
                createLogPriceIfNecessary();
            }

            PopupUtil.snackbar(mFragmentView, getString(R.string.fragment_acquisition_item_new_acquisition_item_persisted_msg));

            updateDeleteButtonState();
        } else {
            boolean hadSpecialPrice = mExistingAcquisitionItem.isSpecialPrice();

            syncAcquisitionItemWithUi(mExistingAcquisitionItem);

            // the user switched from special price, for which the price is entered as is, to a
            // not so special price, for which LogPrice entry is required
            if (hadSpecialPrice && !mExistingAcquisitionItem.isSpecialPrice()) {
                createLogPriceIfNecessary();
            } else if (!hadSpecialPrice && mExistingAcquisitionItem.isSpecialPrice()) {
                decrementLogPriceQuantity();
            }

            mExistingAcquisitionItem.setSynced(false);
            mDbHelper.getAcquisitionItemDao().update(mExistingAcquisitionItem);

            PopupUtil.snackbar(mFragmentView, getString(R.string.fragment_acquisition_item_updated_existing_acquisition_item_msg));
        }

        postAcquisitionUpdateEvents();
    }

    /**
     * Creates a new {@link LogPrice} entry with the underlying {@link TreeSpecies} and
     * {@link LogQualityClass} combination, or updates the {@link LogPrice#quantity} on the
     * existing one
     */
    private void createLogPriceIfNecessary() {
        List<LogPrice> logPriceList;

        try {
            logPriceList = getLogPriceQueryBuilder().query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (logPriceList.size() > 1) {
            throw new RuntimeException("Programming error");
        }

        if (logPriceList.size() == 1) {
            // a log price with these specs exists, update the quantity

            LogPrice logPrice = logPriceList.get(0);
            logPrice.setQuantity(logPrice.getQuantity() + 1);
            logPrice.setSynced(false);
            mDbHelper.getLogPriceDao().update(logPrice);

            PopupUtil.snackbar(mFragmentView, getString(R.string.fragment_acquisition_item_updated_existing_log_price_msg));
            return;
        }

        LogPrice logPrice = new LogPrice(mExistingAcquisitionItem.getAcquisition(),
                mExistingAcquisitionItem.getAcquirer(),
                mExistingAcquisitionItem.getTreeSpecies(),
                mExistingAcquisitionItem.getLogQualityClass(), mExistingAcquisitionItem.getPrice(), 1, false);

        mDbHelper.getLogPriceDao().create(logPrice);
        logPrice.setAppAllocatedId(logPrice.getId());
        mDbHelper.getLogPriceDao().update(logPrice);

        PopupUtil.snackbar(mFragmentView, getString(R.string.fragment_acquisition_item_new_log_price_persisted_msg));
    }

    private void decrementLogPriceQuantity() {
        LogPrice logPrice;

        try {
            logPrice = getLogPriceQueryBuilder().queryForFirst();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (logPrice.getQuantity() == 1) {
            mDbHelper.getLogPriceDao().deleteById(logPrice.getId());
            return;
        }

        logPrice.setQuantity(logPrice.getQuantity() - 1);
        logPrice.setSynced(false);
        mDbHelper.getLogPriceDao().update(logPrice);
    }

    /**
     * Gets a {@link QueryBuilder} instance that is set to query for {@link LogPrice} instances
     * associated with the current {@link Acquisition} instance ({@link #mAcquisition}) and the
     * current {@link AcquisitionItem} instance ({@link #mExistingAcquisitionItem})
     *
     * @return as above
     */
    private QueryBuilder<LogPrice, Integer> getLogPriceQueryBuilder() {
        QueryBuilder<LogPrice, Integer> queryBuilder = mDbHelper.getLogPriceDao().queryBuilder();

        try {
            queryBuilder.where()
                    .eq(CommonFieldNames.ACQUISITION_ID, mAcquisition.getId())
                    .and()
                    .eq(CommonFieldNames.TREE_SPECIES_ID, mExistingAcquisitionItem.getTreeSpecies().getId())
                    .and()
                    .eq(CommonFieldNames.LOG_QUALITY_CLASS_ID, mExistingAcquisitionItem.getLogQualityClass().getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return queryBuilder;
    }

    private void postAcquisitionUpdateEvents() {
        // the volume should be first, as the total price is calculated based on it
        EventBus.getDefault().post(new AcquisitionTotalVolumeUpdateRequestEvent());
        EventBus.getDefault().post(new AcquisitionTotalPriceUpdateRequestEvent());
    }

    private boolean inputFormsAreValid() {
        return InputValidationUtil.areNotEmpty(mTilBarCode, mTilGrossLength, mTilGrossDiameter,
                mTilNetLength, mTilNetDiameter, mTilVolumetricPrice, mTilObservations);
    }

    private AcquisitionItem createAcquisitionItemMatchingUi() {
        AcquisitionItem acquisitionItem = new AcquisitionItem();

        acquisitionItem.setAcquisition(mAcquisition);
        acquisitionItem.setAcquirer(mAcquisition.getAcquirer());

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
            throw new RuntimeException("Programming error", e);
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

        mEtVolumetricPrice.setText(StringFormatUtil.round(mExistingAcquisitionItem.getPrice()));

        mEtGrossLength.setText(StringFormatUtil.round(mExistingAcquisitionItem.getGrossLength()));
        mEtGrossDiameter.setText(StringFormatUtil.round(mExistingAcquisitionItem.getGrossDiameter()));

        mEtNetLength.setText(StringFormatUtil.round(mExistingAcquisitionItem.getNetLength()));
        mEtNetDiameter.setText(StringFormatUtil.round(mExistingAcquisitionItem.getNetDiameter()));

        mTvGrossVolume.setText(StringFormatUtil.round(mExistingAcquisitionItem.getGrossVolume()));
        mTvNetVolume.setText(StringFormatUtil.round(mExistingAcquisitionItem.getNetVolume()));

        mEtObservations.setText(mExistingAcquisitionItem.getObservations());
    }

    private TreeSpecies getSelectedTreeSpecies() {
        return (TreeSpecies) mSpinnerSpecies.getSelectedItem();
    }

    private LogQualityClass getSelectedQualityClass() {
        return (LogQualityClass) mSpinnerQualityClass.getSelectedItem();
    }

    private void updateUiGrossVolume() {
        mTvGrossVolume.setText(StringFormatUtil.round(calculateGrossVolume()));
    }

    private void updateUiNetVolume() {
        mTvNetVolume.setText(StringFormatUtil.round(calculateNetVolume()));
    }

    private void updateUiPriceFormState() {
        mEtVolumetricPrice.setEnabled(mCbSpecialPrice.isChecked());
    }

    private double calculateGrossVolume() {
        return calculateVolume(mEtGrossLength, mEtGrossDiameter);
    }

    private double calculateNetVolume() {
        return calculateVolume(mEtNetLength, mEtNetDiameter);
    }

    private double calculateVolume(EditText etLength, EditText etDiameter) {
        double length;
        double diameter;

        try {
            length = Double.valueOf(etLength.getText().toString());
            diameter = Double.valueOf(etDiameter.getText().toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("Programming error", e);
        }

        return calculateVolume(length, diameter);
    }

    /**
     * Calculates the log volume using: pi*(diameter*10^-2/2)^2*length
     *
     * @param length   the log's length
     * @param diameter the log's diameter
     * @return the log's volume
     */
    private double calculateVolume(double length, double diameter) {
        double meterDiameter = diameter * Math.pow(10, -2);
        return Math.PI * Math.pow(meterDiameter / 2, 2) * length;
    }

    private <T extends View> T findViewById(int id) {
        return mFragmentView.findViewById(id);
    }

    private <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(List<T> modelInstanceList) {
        return FragmentUtil.createDefaultSpinnerAdapter(getContext(), modelInstanceList);
    }
}
