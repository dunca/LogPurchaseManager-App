package dunca.github.io.logpurchasemanager.activities.util;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Helper class with methods that deal with popups like {@link Snackbar}
 */
public final class PopupUtil {
    private PopupUtil() {

    }

    /**
     * Shows a {@link Snackbar} on the given {@link View} with the message that corresponds to the
     * given resource id
     *
     * @param view             the {@link View} on which the {@link Snackbar} is to be shown
     * @param stringResourceId the resource id of the string to be used as a message
     */
    public static void snackbar(View view, int stringResourceId) {
        Snackbar.make(view, stringResourceId, Snackbar.LENGTH_INDEFINITE).show();
    }

    /**
     * Shows a {@link Snackbar} on the given {@link View} with the given message
     *
     * @param view    the {@link View} on which the {@link Snackbar} is to be shown
     * @param message the {@link Snackbar}'s message
     */
    public static void snackbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE).show();
    }
}
