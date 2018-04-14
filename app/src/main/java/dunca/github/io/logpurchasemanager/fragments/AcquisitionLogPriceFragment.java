package dunca.github.io.logpurchasemanager.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dunca.github.io.logpurchasemanager.R;

public class AcquisitionLogPriceFragment extends Fragment {
    public AcquisitionLogPriceFragment() {

    }

    public static AcquisitionLogPriceFragment newInstance() {
        AcquisitionLogPriceFragment fragment = new AcquisitionLogPriceFragment();

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
        return inflater.inflate(R.layout.fragment_acquisition_log_price, container, false);
    }
}
