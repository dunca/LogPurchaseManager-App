package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dunca.github.io.logpurchasemanager.R;
import dunca.github.io.logpurchasemanager.constants.MethodParameterConstants;

public class AcquisitionItemFragment extends Fragment {
    public AcquisitionItemFragment() {

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

        Bundle args = getArguments();
        int acquisitionId = args.getInt(MethodParameterConstants.ACQUISITION_ID_PARAM, 0);

        if (acquisitionId != 0) {
            // the user is trying to add an item to an existing acquisition

        } else {

        }

        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_acquisition_item, container, false);
    }

}
