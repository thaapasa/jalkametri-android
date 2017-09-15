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
package fi.tuska.jalkametri.data;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import fi.tuska.jalkametri.activity.PurchaseReminderActivity;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * This class handles showing the purchase reminder.
 * 
 * @author Tuukka Haapasalo
 */
public class PurchaseReminderHandler {

    private static final Random RANDOM = new Random();
    private static final String TAG = "PurchaseReminder";

    public static int DEFAULT_SHOW_AFTER = 5;
    public static int REPEAT_MIN = 10;
    public static int REPEAT_MAX = 30;

    public static void showReminderIfNecessary(Activity parent) {
        Preferences prefs = new PreferencesImpl(parent);
        if (prefs.isLicensePurchased())
            return;

        boolean mustShow = false;
        int showAfter = prefs.getShowReminderAfter();
        showAfter--;
        if (showAfter < 1) {
            LogUtil.d(TAG, "Reminder counter is zero, showing reminder");
            resetReminderCounter(prefs);
            mustShow = true;
        } else {
            LogUtil.d(TAG, "Decrementing reminder counter, now at %d", showAfter);
            Editor editor = prefs.edit();
            prefs.setShowReminderAfter(editor, showAfter);
            editor.commit();
        }

        if (mustShow) {
            showReminder(parent);
        }
    }

    public static void showReminder(Activity parent) {
        Intent i = new Intent(parent, PurchaseReminderActivity.class);
        parent.startActivity(i);
    }

    public static void resetReminderCounter(Preferences prefs) {
        int steps = RANDOM.nextInt(REPEAT_MAX - REPEAT_MIN + 1) + REPEAT_MIN;
        LogUtil.d(TAG, "Setting new reminder to fire up in %d restarts", steps);

        Editor editor = prefs.edit();
        prefs.setShowReminderAfter(editor, steps);
        editor.commit();
    }

}
