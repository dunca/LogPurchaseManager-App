package dunca.github.io.logpurchasemanager.activities.util;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;

public class StringValidationUtil {
    private StringValidationUtil() {

    }

    public static boolean isNotEmpty(TextInputLayout textInputLayout) {
        Editable editable = textInputLayout.getEditText().getText();

        if (TextUtils.isEmpty(editable)) {
            textInputLayout.setError("*");
            return false;
        }

        textInputLayout.setError(null);
        return true;
    }
}
