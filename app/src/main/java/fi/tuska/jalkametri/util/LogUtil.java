package fi.tuska.jalkametri.util;

import android.util.Log;
import fi.tuska.jalkametri.PrivateData;

public final class LogUtil {

    private LogUtil() {
        // No instantiation required
    }

    /**
     * @return true if logging is enabled and Log.xxx methods should be
     * called; false otherwise.
     */
    public static final boolean isLoggingEnabled() {
        return PrivateData.LOGGING_ENABLED;
    }

    private static String getLogTag(String tag, String message) {
        return "jAlcoMeter";
    }

    private static String getMessage(String tag, String message) {
        return tag + ": " + message;
    }

    /**
     * Wrapper for Log.d; checks whether logging is enabled before calling
     * Log.d, and supports String formatting.
     */
    public static void d(String tag, String message, Object... params) {
        String msg = String.format(message, params);
        if (PrivateData.LOGGING_ENABLED)
            Log.d(getLogTag(tag, msg), getMessage(tag, msg));
    }

    /**
     * Wrapper for Log.i; checks whether logging is enabled before calling
     * Log.i, and supports String formatting.
     */
    public static void i(String tag, String message, Object... params) {
        String msg = String.format(message, params);
        if (PrivateData.LOGGING_ENABLED)
            Log.i(getLogTag(tag, msg), getMessage(tag, msg));
    }

    /**
     * Wrapper for Log.w; checks whether logging is enabled before calling
     * Log.w, and supports String formatting.
     */
    public static void w(String tag, String message, Object... params) {
        String msg = String.format(message, params);
        if (PrivateData.LOGGING_ENABLED)
            Log.w(getLogTag(tag, msg), getMessage(tag, msg));
    }

    /**
     * Wrapper for Log.e; checks whether logging is enabled before calling
     * Log.e, and supports String formatting.
     */
    public static void e(String tag, String message, Object... params) {
        // Error messages are always logged
        String msg = String.format(message, params);
        Log.e(getLogTag(tag, msg), getMessage(tag, msg));
    }

}
