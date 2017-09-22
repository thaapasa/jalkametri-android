package fi.tuska.jalkametri.util;

/**
 * Common utilities.
 *
 * @author Tuukka Haapasalo
 */
public final class CommonUtil {

    private CommonUtil() {
        // Prevent instantiation
    }

    /**
     * Check if the given objects are either both null or equal to each
     * other (as per equals()).
     */
    public static <T> boolean nullOrEquals(T o1, T o2) {
        if (o1 == null)
            return o2 == null;
        if (o2 == null)
            return false;
        return o1.equals(o2);
    }

}
