package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.LogPrice;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;
import dunca.github.io.logpurchasemanager.fragments.interfaces.SmartFragment;

public class AcquisitionLogPriceListFragment extends SmartFragment {
    private View mFragmentView;

    private final DatabaseHelper mDbHelper;
    private List<LogPrice> mLogPriceList;

    public AcquisitionLogPriceListFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    public static AcquisitionLogPriceListFragment newInstance() {
        return new AcquisitionLogPriceListFragment();
    }

    private void initUi() {
        initPriceList();
        initViews();
    }

    private void initViews() {
        TableLayout tblAcquisitionLogPriceList = mFragmentView.findViewById(R.id
                .tblAcquisitionLogPriceList);

        if (mLogPriceList.isEmpty()) {
            return;
        }

        for (LogPrice logPrice : mLogPriceList) {
            TableRow tableRow = (TableRow) getLayoutInflater().inflate(
                    R.layout.fragment_acquisition_log_price_list_row_item, null, false);

            tableRow.setOnClickListener((v) -> System.out.println("hello"));

            TextView tvSpecies = tableRow.findViewById(R.id.tvSpecies);
            TextView tvQualityClass = tableRow.findViewById(R.id.tvQualityClass);
            TextView tvQuantity = tableRow.findViewById(R.id.tvQuantity);
            TextView tvPrice = tableRow.findViewById(R.id.tvPrice);

            tvSpecies.setText(logPrice.getTreeSpecies().getName());
            tvQualityClass.setText(logPrice.getLogQualityClass().getName());
            tvQuantity.setText(String.valueOf(logPrice.getQuantity()));
            tvPrice.setText(String.valueOf(logPrice.getPrice()));

            tblAcquisitionLogPriceList.addView(tableRow);
        }

        SparseIntArray maxTextViewWidths = new SparseIntArray();

        for (int rowIndex = 0; rowIndex < tblAcquisitionLogPriceList.getChildCount(); rowIndex++) {
            TableRow tableRow = (TableRow) tblAcquisitionLogPriceList.getChildAt(rowIndex);
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

        for (int rowIndex = 0; rowIndex < tblAcquisitionLogPriceList.getChildCount(); rowIndex++) {
            TableRow tableRow = (TableRow) tblAcquisitionLogPriceList.getChildAt(rowIndex);
            LinearLayout layout = (LinearLayout) tableRow.getChildAt(0);

            for (int textViewIndex = 0; textViewIndex < layout.getChildCount(); textViewIndex++) {
                int maxWidth = maxTextViewWidths.get(textViewIndex);

                TextView currentTextView = (TextView) layout.getChildAt(textViewIndex);

                currentTextView.setWidth(maxWidth);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_acquisition_log_price_list,
                container, false);

        if (AcquisitionFragment.sCurrentAcquisitionId == MethodParameterConstants.INVALID_INDEX) {
            return createPlaceholderView("Persist the acquisition first...");
        }

        if (!acquisitionHasPriceList()) {
            return createPlaceholderView("This acquisition has no price list...");
        }

        initUi();

        return mFragmentView;
    }

    private boolean acquisitionHasPriceList() {
        try {
            return mDbHelper.getLogPriceDao().queryBuilder()
                    .where()
                    .eq(CommonFieldNames.ACQUISITION_ID, AcquisitionFragment.sCurrentAcquisitionId)
                    .countOf() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initPriceList() {
        try {
            mLogPriceList = mDbHelper.getLogPriceDao().queryBuilder()
                    .where()
                    .eq(CommonFieldNames.ACQUISITION_ID, AcquisitionFragment.sCurrentAcquisitionId)
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private View createPlaceholderView(String message) {
        View placeholderView = getLayoutInflater().inflate(R.layout.placeholder_layout, null,
                false);

        ((TextView) placeholderView.findViewById(R.id.tvContent)).setText(message);

        return placeholderView;
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
}
