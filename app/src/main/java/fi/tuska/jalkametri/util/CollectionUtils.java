package fi.tuska.jalkametri.util;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    /**
     * Creates a string that contains the array members, formatted as strings,
     * and separated by a separator string.
     *
     * @return the string showing the array contents formatted as strings
     */
    public static <T> String implodeArray(T[] array, String separator) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i != 0)
                b.append(separator);
            b.append(array[i].toString());
        }
        return b.toString();
    }

}
