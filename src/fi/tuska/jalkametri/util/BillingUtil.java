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
