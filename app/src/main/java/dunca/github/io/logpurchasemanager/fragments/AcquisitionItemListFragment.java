package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.List;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;
import dunca.github.io.logpurchasemanager.data.dao.DatabaseHelper;
import dunca.github.io.logpurchasemanager.data.model.AcquisitionItem;
import dunca.github.io.logpurchasemanager.data.model.constants.CommonFieldNames;

public class AcquisitionItemListFragment extends Fragment {
    private View mFragmentView;

    private final DatabaseHelper mDbHelper;
    private List<AcquisitionItem> mAcquisitionItemList;
    private TableLayout mTblAcquisitionItemList;

    public AcquisitionItemListFragment() {
        mDbHelper = DatabaseHelper.getLatestInstance();
    }

    public static AcquisitionItemListFragment newInstance(int acquisitionId) {
        AcquisitionItemListFragment fragment = new AcquisitionItemListFragment();

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
        // inflate the layout for this fragment
        mFragmentView = inflater.inflate(R.layout.fragment_acquisition_item_list, container, false);

        Bundle args = getArguments();

        int acquisitionId = args.getInt(MethodParameterConstants.ACQUISITION_ID_PARAM, 0);


        if (acquisitionId == 0) {
            throw new RuntimeException("Acquisition id is required");
        }

        try {
            mAcquisitionItemList = mDbHelper.getAcquisitionItemDao().queryBuilder()
                    .where()
                    .eq(CommonFieldNames.ACQUISITION_ID, acquisitionId)
                    .query();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        initViews();

        return mFragmentView;
    }

    private void initViews() {
        mTblAcquisitionItemList = mFragmentView.findViewById(R.id
                .tblAcquisitionItemList);

        for (AcquisitionItem acquisitionItem : mAcquisitionItemList) {
            TableRow tableRow = (TableRow) getActivity().getLayoutInflater().inflate(
                    R.layout.fragment_acquisition_item_list_row_item, null, false);

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

            mTblAcquisitionItemList.addView(tableRow);
        }
    }
}
