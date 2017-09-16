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
package fi.tuska.jalkametri.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import fi.tuska.jalkametri.CommonActivities;
import fi.tuska.jalkametri.PrivateData;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.BillingUtil;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * Abstract base class for jAlkaMetri activities; contains common
 * functionality.
 *
 * @author Tuukka Haapasalo
 */
public abstract class JalkametriActivity extends Activity implements GUIActivity {

    public static final int NO_HELP_TEXT = 0;

    private View mainView;
    private final int titleResourceId;
    private final int helpTextId;
    protected Preferences prefs;
    private boolean showDefaultHelpMenu = false;

    protected TimeUtil timeUtil;

    private boolean firstRun;

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    protected JalkametriActivity(int titleResourceId, int helpTextId) {
        super();
        this.titleResourceId = titleResourceId;
        this.helpTextId = helpTextId;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.timeUtil = new TimeUtil(this);

        this.prefs = new PreferencesImpl(this);

        Resources res = getResources();
        // Update title to enforce correct language
        setTitle(res.getString(titleResourceId));

        this.firstRun = true;
    }

    protected void findMainView() {
        this.mainView = findViewById(R.id.main_view);
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    protected final void invalidateView() {
        if (mainView != null)
            mainView.invalidate();
    }

    protected void setShowDefaultHelpMenu(boolean state) {
        this.showDefaultHelpMenu = state;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    /**
     * Returns true on the first run, false afterwards. The value is reset at
     * onCreate(), so if this is called from onStart(), it will return true at
     * the first onStart() invocation, and false after that.
     */
    protected boolean isFirstRunAfterCreate() {
        boolean res = firstRun;
        firstRun = false;
        return res;
    }

    /**
     * Tries to suppress the soft keyboard from popping up automatically for
     * the given view.
     */
    protected void tryToHideSoftKeyboard(View view) {
        InputMethodManager service = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (service != null) {
            service.hideSoftInputFromInputMethod(view.getWindowToken(),
                InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean s = super.onCreateOptionsMenu(menu);
        if (showDefaultHelpMenu && helpTextId != NO_HELP_TEXT) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_only_help, menu);
            return true;
        }
        return s;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.action_help) {
            // Show the help screen for this activity
            showHelp(null);
            return true;
        }

        return false;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.action_devel_clear_license:
            // Debugging action: clear license information
            if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
                BillingUtil.removeLicenseInfo(prefs);
                updateUI();
                Toast.makeText(this, "Licence cleared", Toast.LENGTH_SHORT).show();
                return true;
            }

        case R.id.action_devel_set_license:
            // Debugging action: set license information
            if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
                BillingUtil.setLicensePurchased(prefs);
                updateUI();
                Toast.makeText(this, "Licence set", Toast.LENGTH_SHORT).show();
                return true;
            }

        case R.id.action_devel_clear_def_library_created:
            // Debugging action: clear default drink library creation flag
            if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
                Editor editor = prefs.edit();
                prefs.setDrinkLibraryInitialized(editor, false);
                editor.commit();
                Toast.makeText(this, "Drink library flag cleared", Toast.LENGTH_SHORT).show();
                return true;
            }

        case R.id.action_devel_clear_disclaimer_read:
            // Debugging action: set license information
            if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
                Editor editor = prefs.edit();
                prefs.setDisclaimerRead(editor, false);
                editor.commit();
                Toast.makeText(this, "Disclaimer read flag reset", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Common activity bindings --------------------------------------------
     */

    /**
     * Show the drink calculator.
     */
    public void showDrinkCalculator(View v) {
        CommonActivities.showCalculator(this);
    }

    /** Show the drinking history. */
    public void showDrinkHistory(View v) {
        CommonActivities.showDrinkHistory(this, prefs);
    }

    /** Shows the user preferences screen. */
    public void showPreferences(View v) {
        CommonActivities.showPreferences(this);
    }

    /** Shows the statistics screen. */
    public void showStatistics(View v) {
        CommonActivities.showStatistics(this);
    }

    /** Shows the statistics screen. */
    public void showHelp(View v) {
        if (helpTextId != NO_HELP_TEXT) {
            HelpActivity.showHelp(helpTextId, this);
        }
    }

}
