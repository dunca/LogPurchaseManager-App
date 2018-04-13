package dunca.github.io.logpurchasemanager.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dunca.github.io.logpurchasemanager.R;

/**
 * A fragment that's that shows a non dismissible {@link AlertDialog} with one button,
 * which once pressed, starts a given {@link Activity}
 */
public class DummyRedirectingFragment extends Fragment {
    /**
     * The message to display in the {@link AlertDialog}
     */
    private static final String PARAM_MESSAGE = "param_message";

    /**
     * The name of the {@link Activity} class that is to to be started
     */
    private static final String PARAM_ACTIVITY_CLASS_NAME = "param_activity_class_name";

    private String mMessage;
    private Class mActivityClass;

    public DummyRedirectingFragment() {

    }

    public static DummyRedirectingFragment newInstance(String mMessage, String className) {
        DummyRedirectingFragment fragment = new DummyRedirectingFragment();

        Bundle args = new Bundle();

        args.putString(PARAM_MESSAGE, mMessage);
        args.putString(PARAM_ACTIVITY_CLASS_NAME, className);

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMessage = getArguments().getString(PARAM_MESSAGE);

        try {
            mActivityClass = Class.forName(getArguments().getString(PARAM_ACTIVITY_CLASS_NAME));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dummy_redirecting, container, false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        /*
        when isResumed() == false, onCreateView() has yet to run, thus UI objects are not
        instantiated, thus, there's nothing to update
        */
        if (!isResumed()) {
            return;
        }

        if (isVisibleToUser) {
            displayAlert();
        }
    }

    /**
     * Displays a non dismissible {@link AlertDialog} with on button, which, once pressed,
     * starts the {@link Activity} described by {@link #mActivityClass}
     */
    private void displayAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        alertDialogBuilder.setMessage(mMessage).setCancelable(false)
                .setPositiveButton("OK", ((alertDialog, buttonId) -> startActivityClass()));

        alertDialogBuilder.create().show();
    }

    private void startActivityClass() {
        Intent intent = new Intent(getContext(), mActivityClass);
        startActivity(intent);
    }
}
