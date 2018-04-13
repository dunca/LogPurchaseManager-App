package dunca.github.io.logpurchasemanager.fragments.interfaces;

import android.support.v4.app.Fragment;

/**
 * A {@link Fragment} subclass that notified when it becomes visible
 */
public abstract class SmartFragment extends Fragment {
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        /*
        when isResumed() == false, onCreateView() has yet to run, thus UI objects are not
        instantiated
        */
        if (!isResumed()) {
            return;
        }

        if (isVisibleToUser) {
            onVisible();
        }
    }

    public abstract void onVisible();
}
