package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dunca.github.io.logpurchasemanager.R;

public class AcquisitionFragment extends Fragment {
    public AcquisitionFragment() {

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
        return inflater.inflate(R.layout.fragment_acquisition, container, false);
    }
}
