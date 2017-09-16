/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
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
