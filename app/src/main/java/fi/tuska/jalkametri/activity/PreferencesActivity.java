/**
 * Copyright 2006-2011 Tuukka Haapasalo
 *
 * This file is part of jAlkaMetri.
 *
 * jAlkaMetri is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with jAlkaMetri (LICENSE.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri.activity;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import fi.tuska.jalkametri.CommonActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.Preferences.Gender;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.gui.DataBackupHandler;
import fi.tuska.jalkametri.util.LocalizationUtil;
import fi.tuska.jalkametri.util.NumberUtil;

/**
 * An activity for editing your preferences.
 *
 * @author Tuukka Haapasalo
 */
public class PreferencesActivity extends JalkametriDBActivity implements GUIActivity {

    private Spinner languageSpinner;
    private Spinner genderSpinner;
    private Spinner weekStartSpinner;
    private Spinner standardDrinkSpinner;
    private EditText weightEdit;
    private EditText drivingLimitEdit;
    // private EditText maxLevelEdit;
    private TimePicker dayChangePicker;

    private CheckBox adsEnabled;
    private TextView adsInfo;
    private Button buyLicenseButton;

    private boolean allowLeave = true;
    private boolean backRequested = false;

    private static final String TAG = "PreferencesActivity";

    /*
     * 25.4.2011: Sounds disabled for now. Same for debug mode.
     */
    // private CheckBox soundsEnabled;
    // private CheckBox debugMode;

    private String[] languageValues;
    private String[] genderValues;
    private String[] standardDrinkValues;

    public PreferencesActivity() {
        super(R.string.title_preferences, R.string.help_prefs);
        setShowDefaultHelpMenu(true);
    }

    /*
     * Standard activity functions --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prefs);
        findMainView();

        Resources res = getResources();
        languageValues = res.getStringArray(R.array.language_values);
        genderValues = res.getStringArray(R.array.gender_values);
        standardDrinkValues = res.getStringArray(R.array.std_drink_weight_values);

        languageSpinner = (Spinner) findViewById(R.id.language_edit);
        genderSpinner = (Spinner) findViewById(R.id.gender_edit);
        weekStartSpinner = (Spinner) findViewById(R.id.week_start_edit);
        standardDrinkSpinner = (Spinner) findViewById(R.id.standard_drink);
        weightEdit = (EditText) findViewById(R.id.weight_edit);
        dayChangePicker = (TimePicker) findViewById(R.id.day_change_edit);
        drivingLimitEdit = (EditText) findViewById(R.id.driving_limit_edit);

        adsEnabled = (CheckBox) findViewById(R.id.ads);
        adsInfo = (TextView) findViewById(R.id.ads_info);
        buyLicenseButton = (Button) findViewById(R.id.buy_license);

        // maxLevelEdit = (EditText) findViewById(R.id.max_level_edit);
        // soundsEnabled = (CheckBox) findViewById(R.id.sounds);
        // debugMode = (CheckBox) findViewById(R.id.debug_mode);

        dayChangePicker.setIs24HourView(true);

        initComponents();
        updateUI();
    }

    @Override
    protected void onPause() {
        savePreferences();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUIFromPreferences();
    }

    @Override
    public void onBackPressed() {
        savePreferences();
        setResult(RESULT_OK);
        if (allowLeave) {
            backRequested = false;
            super.onBackPressed();
        }
        else {
            backRequested = true;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        updateUIFromPreferences();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        savePreferences();
    }

    /*
     * UI initialization etc. --------------------------------------------
     */
    @Override
    public void updateUI() {
        updateAdsEditButtons();
    }

    private void updateAdsEditButtons() {
        Resources res = getResources();
        final boolean hasLicense = prefs.isLicensePurchased();
        if (!hasLicense) {
            adsEnabled.setChecked(true);
        }

        adsEnabled.setEnabled(hasLicense);
        adsInfo.setText(res.getString(hasLicense ? R.string.prefs_ads_info_licensed : R.string.prefs_ads_info_free));
        buyLicenseButton.setVisibility(hasLicense ? View.GONE : View.VISIBLE);
    }

    /*
     * Activity item initialization --------------------------------------------
     */
    private void initComponents() {
        populateSpinners();
    }

    private void populateSpinners() {
        // Language spinner
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_options,
                android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            languageSpinner.setAdapter(adapter);
        }
        // Gender spinner
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender_options,
                android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            genderSpinner.setAdapter(adapter);
        }
        // Week start day spinner
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.week_start_options,
                android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            weekStartSpinner.setAdapter(adapter);
        }
        // Standard drink spinner
        {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.std_drink_weight_options, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            standardDrinkSpinner.setAdapter(adapter);
        }
    }

    /*
     * Custom actions ----------------------------------------------------------
     */
    public void showAbout(View v) {
        CommonActivities.showAbout(this);
    }

    public void backupData(View v) {
        DataBackupHandler.backupData(this);
    }

    public void restoreData(View v) {
        DataBackupHandler.restoreData(this);
    }

    public void updateUIFromPreferences() {
        // Populate language
        Locale lang = prefs.getLocale();
        for (int i = 0; i < languageValues.length; i++) {
            if (lang.getLanguage().equals(languageValues[i])) {
                languageSpinner.setSelection(i);
                break;
            }
        }

        // Populate weight
        Resources res = getResources();
        double weight = prefs.getWeight();
        weightEdit.setText(NumberUtil.toString(weight, res));

        // Populate driving limit
        double drivingLimit = prefs.getDrivingAlcoholLimit();
        drivingLimitEdit.setText(NumberUtil.toString(drivingLimit, res));

        // Populate maximum alcohol level
        // double maxLevel = prefs.getMaxAlcoholLevel();
        // maxLevelEdit.setText(NumberUtil.toString(maxLevel));

        // Populate day change time
        int hour = prefs.getDayChangeHour();
        int minute = prefs.getDayChangeMinute();
        dayChangePicker.setCurrentHour(hour);
        dayChangePicker.setCurrentMinute(minute);

        // Populate gender
        Gender gender = prefs.getGender();
        for (int i = 0; i < genderValues.length; i++) {
            if (gender.toString().equals(genderValues[i])) {
                genderSpinner.setSelection(i);
                break;
            }
        }

        // Populate standard drink selection
        String drinkSel = prefs.getStandardDrinkCountry();
        for (int i = 0; i < standardDrinkValues.length; i++) {
            if (drinkSel != null && drinkSel.equalsIgnoreCase(standardDrinkValues[i])) {
                standardDrinkSpinner.setSelection(i);
                break;
            }
        }

        // Populate week start
        boolean startMonday = prefs.isWeekStartMonday();
        // Monday is first
        weekStartSpinner.setSelection(startMonday ? 0 : 1);

        // Populate ads enabled
        adsEnabled.setChecked(prefs.isLicensePurchased() ? prefs.isAdsEnabled() : true);

        // Populate sounds enabled
        // soundsEnabled.setChecked(prefs.isSoundsEnabled());

        // Populate debug mode
        // debugMode.setChecked(prefs.isDebugMode());
    }

    private void savePreferences() {
        Editor editor = prefs.edit();
        boolean needRecalculatePortions = false;

        // Set language
        {
            int pos = languageSpinner.getSelectedItemPosition();
            if (pos >= 0 && pos <= languageValues.length) {
                String languageStr = languageValues[pos];
                Locale lang = LocalizationUtil.getLocale(languageStr);
                if (lang != null) {
                    prefs.setLocale(editor, lang);
                }
            }
        }

        // Set weight
        Locale locale = prefs.getLocale();
        double weight = NumberUtil.readDouble(weightEdit.getText().toString(), locale);
        prefs.setWeight(editor, weight);

        // Set driving alcohol limit
        double drivingLimit = NumberUtil.readDouble(drivingLimitEdit.getText().toString(), locale);
        prefs.setDrivingAlcoholLimit(editor, drivingLimit);

        // Set maximum alcohol level
        // double maxLevel =
        // NumberUtil.readDouble(maxLevelEdit.getText().toString());
        // prefs.setMaxAlcoholLevel(editor, maxLevel);

        // Set day change time
        int hour = dayChangePicker.getCurrentHour();
        int minute = dayChangePicker.getCurrentMinute();
        prefs.setDayChangeHour(editor, hour);
        prefs.setDayChangeMinute(editor, minute);

        // Set gender
        {
            int pos = genderSpinner.getSelectedItemPosition();
            if (pos >= 0 && pos <= genderValues.length) {
                String genderStr = genderValues[pos];
                Gender gender = Gender.valueOf(genderStr);
                if (gender != null) {
                    prefs.setGender(editor, gender);
                }
            }
        }

        // Set standard drink selection
        double orgStdWei = prefs.getStandardDrinkAlcoholWeight();
        {
            int pos = standardDrinkSpinner.getSelectedItemPosition();
            if (pos >= 0 && pos <= standardDrinkValues.length) {
                String stdDrinkCountry = standardDrinkValues[pos];
                if (stdDrinkCountry != null) {
                    prefs.setStandardDrinkCountry(editor, stdDrinkCountry);
                }
            }
        }

        // Set week start
        {
            int pos = weekStartSpinner.getSelectedItemPosition();
            if (pos >= 0 && pos <= genderValues.length) {
                prefs.setWeekStartMonday(editor, pos == 0);
            }
        }

        // Set ads enabled
        if (prefs.isLicensePurchased()) {
            // Only allow changing this setting if the license has been bought
            prefs.setAdsEnabled(editor, adsEnabled.isChecked());
        }

        // Set sounds enabled
        // prefs.setSoundsEnabled(editor, soundsEnabled.isChecked());
        prefs.setSoundsEnabled(editor, false);

        // Set debug mode
        // prefs.setDebugMode(editor, debugMode.isChecked());
        prefs.setDebugMode(editor, false);

        // Commit changes
        editor.commit();

        if (prefs.getStandardDrinkAlcoholWeight() != orgStdWei) {
            needRecalculatePortions = true;
        }

        if (needRecalculatePortions) {
            recalculateHistoryPortions();
        }
    }

    private void recalculateHistoryPortions() {
        allowLeave = false;
        // Recalculate the portions in history database
        DrinkActions.recalculateHistoryPortions(adapter, this, new Runnable() {
            @Override
            public void run() {
                allowLeave = true;
                if (backRequested) {
                    PreferencesActivity.super.onBackPressed();
                }
            }
        });
    }


    /*
     * Return from activities --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }

}
