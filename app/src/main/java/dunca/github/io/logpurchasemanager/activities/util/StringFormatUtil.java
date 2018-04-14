package dunca.github.io.logpurchasemanager.activities.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

public class StringFormatUtil {
    private StringFormatUtil() {

    }

    /**
     * Rounds the given number to two decimal places
     *
     * @param number the number to round
     * @return a string corresponding to the given number, rounded to two decimal places
     */
    public static String round(double number) {
        number = BigDecimal.valueOf(number).setScale(2, RoundingMode.HALF_UP).doubleValue();
        return String.format(Locale.getDefault(), "%.2f", number);
    }
}
