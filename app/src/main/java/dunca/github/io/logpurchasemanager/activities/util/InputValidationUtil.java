package dunca.github.io.logpurchasemanager.activities.util;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class InputValidationUtil {
    private InputValidationUtil() {

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

    public static boolean areNotEmpty(TextInputLayout... textInputLayouts) {
        List<Boolean> validityList = new ArrayList<>();

        for (TextInputLayout textInputLayout : textInputLayouts) {
            validityList.add(isNotEmpty(textInputLayout));
        }

        for (boolean validity : validityList) {
            if (!validity) {
                return false;
            }
        }

        return true;
    }
}
