package fi.tuska.jalkametri.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import android.content.res.Resources;

public final class NumberUtil {

    private NumberUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convenience method for parsing a double; defaults to 0 if the number
     * could not be parsed.
     */
    public static double readDouble(String value, Locale locale) {
        try {
            // Use the English number formatter for reading the number
            NumberFormat f = NumberFormat.getNumberInstance(Locale.ENGLISH);
            // Change the Finnish separator characters to English ones
            String valStr = value.replace(',', '.');
            // Should now accept both Finnish and English decimal separator
            // chars
            return f.parse(valStr).doubleValue();
        } catch (ParseException e) {
            return 0d;
        }
    }

    public static String toString(int value, Resources res) {
        NumberFormat f = new TimeUtil(res).getNumberFormat();
        return f.format(value);
    }

    public static String toString(double value, Resources res) {
        NumberFormat f = new TimeUtil(res).getNumberFormat();
        return f.format(value);
    }

}
