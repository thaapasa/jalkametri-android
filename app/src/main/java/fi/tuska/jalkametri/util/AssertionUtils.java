package fi.tuska.jalkametri.util;

public final class AssertionUtils {

    public static boolean isAssertionsEnabled() {
        try {
            // This should cause an AssertionError to be thrown
            assert false;
        } catch (AssertionError e) {
            // AssertionError thrown
            return true;
        }
        // Not thrown, so assertions are not on
        return false;
    }

}
