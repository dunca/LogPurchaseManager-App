package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.sql.SQLException;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import io.github.dunca.logpurchasemanager.shared.model.AcquisitionItem;
import io.github.dunca.logpurchasemanager.shared.model.constants.CommonFieldNames;
import dunca.github.io.logpurchasemanager.fragments.events.AcquisitionItemIdEvent;
import dunca.github.io.logpurchasemanager.fragments.interfaces.SmartFragment;

public class AcquisitionItemListFragment extends SmartFragment {
    private View mFragmentView;

    private ViewPager mViewPager;
    private final DatabaseHelper mDbHelper;
    private List<AcquisitionItem> mAcquisitionItemList;

    public AcquisitionItemListFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    public static AcquisitionItemListFragment newInstance() {
        return new AcquisitionItemListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mViewPager = getActivity().findViewById(R.id.fragment_container);

        // inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_acquisition_item_list, null, false);

        if (AcquisitionFragment.sCurrentAcquisitionId == MethodParameterConstants.INVALID_INDEX) {
            return createPlaceholderView(getString(R.string.fragment_acquisition_item_list_no_acquisition_placeholder));
        }

        if (!acquisitionHasItems()) {
            return createPlaceholderView(getString(R.string.fragment_acquisition_item_list_no_items_placeholder));
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

    public void initUi() {
        initAcquisitionItemList();
        initViews();
    }

    private boolean acquisitionHasItems() {
        try {
            return mDbHelper.getAcquisitionItemDao().queryBuilder().where()
                    .eq(CommonFieldNames.ACQUISITION_ID, AcquisitionFragment.sCurrentAcquisitionId)
                    .countOf() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initAcquisitionItemList() {
        try {
            mAcquisitionItemList = mDbHelper.getAcquisitionItemDao().queryBuilder().where()
                    .eq(CommonFieldNames.ACQUISITION_ID, AcquisitionFragment.sCurrentAcquisitionId)
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initViews() {
        TableLayout tblAcquisitionItemList = mFragmentView.findViewById(R.id
                .tblAcquisitionItemList);

        for (AcquisitionItem acquisitionItem : mAcquisitionItemList) {
            TableRow tableRow = (TableRow) getLayoutInflater().inflate(
                    R.layout.fragment_acquisition_item_list_row_item, null, false);

            tableRow.setOnClickListener(view -> switchToAcquisitionItemTab(acquisitionItem.getId()));

            TextView tvSpecies = tableRow.findViewById(R.id.tvSpecies);
            TextView tvQualityClass = tableRow.findViewById(R.id.tvQualityClass);
            TextView tvLogBarCode = tableRow.findViewById(R.id.tvLogBarCode);
            TextView tvNetLength = tableRow.findViewById(R.id.tvNetLength);
            TextView tvNetDiameter = tableRow.findViewById(R.id.tvNetDiameter);
            TextView tvGrossLength = tableRow.findViewById(R.id.tvGrossLength);
            TextView tvGrossDiameter = tableRow.findViewById(R.id.tvGrossDiameter);
            TextView tvNetVolume = tableRow.findViewById(R.id.tvNetVolume);
            TextView tvGrossVolume = tableRow.findViewById(R.id.tvGrossVolume);
            TextView tvVolumetricPrice = tableRow.findViewById(R.id.tvVolumetricPrice);
            TextView tvObservations = tableRow.findViewById(R.id.tvObservations);

            tvSpecies.setText(acquisitionItem.getTreeSpecies().getName());
            tvQualityClass.setText(acquisitionItem.getLogQualityClass().getName());
            tvLogBarCode.setText(acquisitionItem.getLogBarCode());
            tvNetLength.setText(String.valueOf(acquisitionItem.getNetLength()));
            tvNetDiameter.setText(String.valueOf(acquisitionItem.getNetDiameter()));
            tvGrossLength.setText(String.valueOf(acquisitionItem.getGrossLength()));
            tvGrossDiameter.setText(String.valueOf(acquisitionItem.getGrossDiameter()));
            tvNetVolume.setText(String.valueOf(acquisitionItem.getNetVolume()));
            tvGrossVolume.setText(String.valueOf(acquisitionItem.getGrossVolume()));
            tvVolumetricPrice.setText(String.valueOf(acquisitionItem.getPrice()));
            tvObservations.setText(acquisitionItem.getObservations());

            tblAcquisitionItemList.addView(tableRow);
        }

        SparseIntArray maxTextViewWidths = new SparseIntArray();

        for (int rowIndex = 0; rowIndex < tblAcquisitionItemList.getChildCount(); rowIndex++) {
            TableRow tableRow = (TableRow) tblAcquisitionItemList.getChildAt(rowIndex);
            LinearLayout layout = (LinearLayout) tableRow.getChildAt(0);

            for (int textViewIndex = 0; textViewIndex < layout.getChildCount(); textViewIndex++) {
                int lastMaxWidth = maxTextViewWidths.get(textViewIndex, 0);

                TextView currentTextView = (TextView) layout.getChildAt(textViewIndex);
                currentTextView.measure(0, 0);
                int currentWidth = currentTextView.getMeasuredWidth();

                if (lastMaxWidth <= currentWidth) {
                    maxTextViewWidths.put(textViewIndex, currentWidth);
                }
            }
        }

        for (int rowIndex = 0; rowIndex < tblAcquisitionItemList.getChildCount(); rowIndex++) {
            TableRow tableRow = (TableRow) tblAcquisitionItemList.getChildAt(rowIndex);
            LinearLayout layout = (LinearLayout) tableRow.getChildAt(0);

            for (int textViewIndex = 0; textViewIndex < layout.getChildCount(); textViewIndex++) {
                int maxWidth = maxTextViewWidths.get(textViewIndex);

                TextView currentTextView = (TextView) layout.getChildAt(textViewIndex);

                currentTextView.setWidth(maxWidth);
            }
        }
    }

    private void switchToAcquisitionItemTab(int acquisitionItemId) {
        EventBus.getDefault().post(new AcquisitionItemIdEvent(acquisitionItemId));
        mViewPager.setCurrentItem(2);
    }

    private View createPlaceholderView(String message) {
        View placeholderView = getLayoutInflater().inflate(R.layout.placeholder_layout, null,
                false);

        ((TextView) placeholderView.findViewById(R.id.tvContent)).setText(message);

        return placeholderView;
    }
}
