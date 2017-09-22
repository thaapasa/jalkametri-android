package fi.tuska.jalkametri.util;

/**
 * Utility functions for handling strings.
 *
 * @author Tuukka Haapasalo
 */
public final class StringUtil {

    private StringUtil() {
        // No instantiation required
    }

    /**
     * Converts the first letter in the string to upper case; e.g.,
     * "toodledyDoo" becomes "ToodledyDoo".
     *
     * @param str the string
     * @return the string with upper-cased first letter
     */
    public static String uppercaseFirstLetter(String str) {
        if (str == null)
            return null;
        if (str.length() <= 1)
            return str.toUpperCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
