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
package fi.tuska.jalkametri;

import android.content.Intent;
import fi.tuska.jalkametri.activity.AboutActivity;
import fi.tuska.jalkametri.activity.CalculatorActivity;
import fi.tuska.jalkametri.activity.DisclaimerActivity;
import fi.tuska.jalkametri.activity.HistoryActivity;
import fi.tuska.jalkametri.activity.JalkametriActivity;
import fi.tuska.jalkametri.activity.PreferencesActivity;
import fi.tuska.jalkametri.activity.StatisticsActivity;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * A single point-of-entry for starting up common activities.
 * 
 * @author Tuukka Haapasalo
 */
public final class CommonActivities {

    private static final String TAG = "CommonActivities";

    private CommonActivities() {
        // Prevent instantiation
    }

    /** Shows the user preferences screen. */
    public static void showPreferences(JalkametriActivity parent) {
        LogUtil.i(TAG, "Showing preferences");
        Intent i = new Intent(parent, PreferencesActivity.class);
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_SHOW_PREFERENCES);
    }
    
    /** Shows the drinking statistics screen. */
    public static void showStatistics(JalkametriActivity parent) {
        LogUtil.i(TAG, "Showing statistics");
        Intent i = new Intent(parent, StatisticsActivity.class);
        parent.startActivity(i);
    }

    /** Shows a legal disclaimer message. */
    public static void showDisclaimer(JalkametriActivity parent) {
        LogUtil.i(TAG, "Showing legal disclaimer");
        Intent i = new Intent(parent, DisclaimerActivity.class);
        parent.startActivity(i);
    }

    /** Shows the about screen. */
    public static void showAbout(JalkametriActivity parent) {
        LogUtil.i(TAG, "Showing legal disclaimer");
        Intent i = new Intent(parent, AboutActivity.class);
        parent.startActivity(i);
    }

    /** Add a drink. */
    public static void showAddDrink(JalkametriActivity parent) {
        LogUtil.i(TAG, "Adding a drink");
        DrinkActivities.startSelectDrink(parent);
    }

    /** Show the drinking history. */
    public static void showDrinkHistory(JalkametriActivity parent, Preferences prefs) {
        LogUtil.i(TAG, "Showing drink history");
        Intent i = new Intent(parent, HistoryActivity.class);
        HistoryActivity.prepareForDay(i, new TimeUtil(parent).getCurrentDrinkingDate(prefs));
        parent.startActivity(i);
    }

    /** Shows the drink strength calculator. */
    public static void showCalculator(JalkametriActivity parent) {
        LogUtil.i(TAG, "Showing drink strength calculator");
        Intent i = new Intent(parent, CalculatorActivity.class);
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_SHOW_CALCULATOR);
    }

    /** Shows the drink strength calculator. */
    public static void showCalculator(JalkametriActivity parent, DrinkSelection initialSelection) {
        LogUtil.i(TAG, "Showing drink strength calculator");
        Intent i = new Intent(parent, CalculatorActivity.class);
        CalculatorActivity.prepareForDrink(i, initialSelection);
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_SHOW_CALCULATOR);
    }

}
