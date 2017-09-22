package fi.tuska.jalkametri.util;

import android.content.SharedPreferences.Editor;
import fi.tuska.jalkametri.dao.Preferences;

public final class BillingUtil {

    private BillingUtil() {
        // No instantiation required
    }

    /**
     * Marks the license as being bought. Call when the license purchasing
     * succeeds.
     *
     * @param prefs the preferences
     */
    public static void setLicensePurchased(Preferences prefs) {
        Editor editor = prefs.edit();
        prefs.setLicensePurchased(editor, true);
        prefs.setAdsEnabled(editor, false);
        editor.commit();
    }

    /**
     * Removes the license information. Should really be used only for
     * testing...
     *
     * @param prefs the preferences
     */
    public static void removeLicenseInfo(Preferences prefs) {
        Editor editor = prefs.edit();
        prefs.setLicensePurchased(editor, false);
        prefs.setAdsEnabled(editor, true);
        editor.commit();
    }

}
