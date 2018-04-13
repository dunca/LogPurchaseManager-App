package dunca.github.io.logpurchasemanager.fragments.util;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import dunca.github.io.logpurchasemanager.data.model.interfaces.Model;

/**
 * Helper class with methods helpful to fragment classes
 */
public final class FragmentUtil {
    private FragmentUtil() {

    }

    public static <T extends Model> ArrayAdapter<T> createDefaultSpinnerAdapter(Context context,
                                                                                List<T> modelInstanceList) {
        ArrayAdapter<T> arrayAdapter = new ArrayAdapter<T>(context,
                android.R.layout.simple_spinner_item, modelInstanceList);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return arrayAdapter;
    }
}
