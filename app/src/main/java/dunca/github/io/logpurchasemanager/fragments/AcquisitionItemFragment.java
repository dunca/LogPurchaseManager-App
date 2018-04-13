package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dunca.github.io.logpurchasemanager.R;

public class AcquisitionItemFragment extends Fragment {
    public AcquisitionItemFragment() {

    }

    public static AcquisitionItemFragment newInstance() {
        AcquisitionItemFragment fragment = new AcquisitionItemFragment();
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
        return inflater.inflate(R.layout.fragment_acquisition_item, container, false);
    }

}
