package fi.tuska.jalkametri.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.util.LocalizationUtil;
import fi.tuska.jalkametri.util.LogUtil;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Stores the user preferences in Android shared preferences.
 *
 * @author Tuukka Haapasalo
 */
public class PreferencesImpl implements Preferences {

    private static final String TAG = "Preferences";

    private final SharedPreferences prefs;

    private static final Map<String, Double> STD_DRINK_WEIGHTS = new HashMap<String, Double>();

    static {
        STD_DRINK_WEIGHTS.put("au", 10d);
        STD_DRINK_WEIGHTS.put("at", 6d);
        STD_DRINK_WEIGHTS.put("ca", 13.5d);
        STD_DRINK_WEIGHTS.put("dk", 12d);
        STD_DRINK_WEIGHTS.put("fi", 12d);
        STD_DRINK_WEIGHTS.put("fr", 12d);
        STD_DRINK_WEIGHTS.put("hu", 17d);
        STD_DRINK_WEIGHTS.put("is", 8d);
        STD_DRINK_WEIGHTS.put("ie", 10d);
        STD_DRINK_WEIGHTS.put("it", 10d);
        STD_DRINK_WEIGHTS.put("jp", 19.75d);
        STD_DRINK_WEIGHTS.put("nl", 9.9d);
        STD_DRINK_WEIGHTS.put("nz", 10d);
        STD_DRINK_WEIGHTS.put("pl", 10d);
        STD_DRINK_WEIGHTS.put("pt", 14d);
        STD_DRINK_WEIGHTS.put("es", 10d);
        STD_DRINK_WEIGHTS.put("gb", 7.9d);
        STD_DRINK_WEIGHTS.put("us", 14d);
    }

    public PreferencesImpl(Activity parent) {
        Context context = parent.getApplicationContext();
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public PreferencesImpl(Context context) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public double getDrivingAlcoholLimit() {
        // All doubles must be stored internally as floats, so there are a lot
        // of conversions in this class...
        return prefs.getFloat(PREF_DRIVING_ALCOHOL_LIMIT, (float) DEFAULT_DRIVING_ALCOHOL_LIMIT);
    }

    @Override
    public void setDrivingAlcoholLimit(Editor editor, double limit) {
        editor.putFloat(PREF_DRIVING_ALCOHOL_LIMIT, (float) limit);
    }

    @Override
    public double getMaxAlcoholLevel() {
        return prefs.getFloat(PREF_MAX_ALCOHOL_LEVEL, (float) DEFAULT_MAX_ALHOCOL_LEVEL);
    }

    @Override
    public void setMaxAlcoholLevel(Editor editor, double level) {
        editor.putFloat(PREF_MAX_ALCOHOL_LEVEL, (float) level);
    }

    @Override
    public Gender getGender() {
        String genderStr = prefs.getString(PREF_GENDER, DEFAULT_GENDER.toString());
        Gender gender = Gender.valueOf(genderStr);
        return gender != null ? gender : DEFAULT_GENDER;
    }

    @Override
    public void setGender(Editor editor, Gender gender) {
        editor.putString(PREF_GENDER, gender.toString());
    }

    @Override
    public double getWeight() {
        return prefs.getFloat(PREF_WEIGHT, (float) DEFAULT_WEIGHT);
    }

    @Override
    public void setWeight(Editor editor, double weight) {
        editor.putFloat(PREF_WEIGHT, (float) weight);
    }

    @Override
    public int getDayChangeHour() {
        return prefs.getInt(PREF_DAY_CHANGE_HOUR, DEFAULT_DAY_CHANGE_HOUR);
    }

    @Override
    public void setDayChangeHour(Editor editor, int hour) {
        editor.putInt(PREF_DAY_CHANGE_HOUR, hour);
    }

    @Override
    public int getDayChangeMinute() {
        return prefs.getInt(PREF_DAY_CHANGE_MINUTE, DEFAULT_DAY_CHANGE_MINUTE);
    }

    @Override
    public void setDayChangeMinute(Editor editor, int minute) {
        editor.putInt(PREF_DAY_CHANGE_MINUTE, minute);
    }

    @Override
    public LocalTime getDayChangeTime() {
        return new LocalTime(getDayChangeHour(), getDayChangeMinute());
    }

    @Override
    public Editor edit() {
        return prefs.edit();
    }

    @Override
    public boolean isDrinkLibraryInitialized() {
        return prefs.getBoolean(PREF_DRINK_LIB_INIT, false);
    }

    @Override
    public void setDrinkLibraryInitialized(Editor editor, boolean state) {
        editor.putBoolean(PREF_DRINK_LIB_INIT, state);
    }

    @Override
    public boolean isWeekStartMonday() {
        return prefs.getBoolean(PREF_WEEK_START_MONDAY, DEFAULT_WEEK_START_MONDAY);
    }

    @Override
    public void setWeekStartMonday(Editor editor, boolean state) {
        editor.putBoolean(PREF_WEEK_START_MONDAY, state);
    }

    @Override
    public boolean isDisclaimerRead() {
        return prefs.getBoolean(PREF_DISCLAIMER_READ, DEFAULT_DISCLAIMER_READ);
    }

    @Override
    public void setDisclaimerRead(Editor editor, boolean state) {
        editor.putBoolean(PREF_DISCLAIMER_READ, state);
    }

    @Override
    public Locale getLocale() {
        String localeCode = prefs.getString(PREF_LOCALE, LocalizationUtil.INSTANCE.getDefaultLocale().getLanguage());
        return LocalizationUtil.INSTANCE.getLocale(localeCode);
    }

    @Override
    public void setLocale(Editor editor, Locale locale) {
        editor.putString(PREF_LOCALE, locale.getLanguage());
    }

    @Override
    public boolean isLicensePurchased() {
        return prefs.getBoolean(PREF_LICENSE_PURCHASED, DEFAULT_LICENSE_PURCHASED);
    }

    @Override
    public void setLicensePurchased(Editor editor, boolean state) {
        editor.putBoolean(PREF_LICENSE_PURCHASED, state);
    }

    @Override
    public double getStandardDrinkAlcoholWeight() {
        String country = getStandardDrinkCountry();
        Double val = STD_DRINK_WEIGHTS.get(country);
        if (val == null) {
            val = STD_DRINK_WEIGHTS.get(DEFAULT_STANDARD_DRINK_COUNTRY);
            LogUtil.INSTANCE.e(TAG, "No standard drink weight found for country %s; using default value (%.2d)", country, val);
        }
        return val;
    }

    @Override
    public String getStandardDrinkCountry() {
        return prefs.getString(PREF_STANDARD_DRINK_COUNTRY, DEFAULT_STANDARD_DRINK_COUNTRY);
    }

    @Override
    public void setStandardDrinkCountry(Editor editor, String countryCode) {
        editor.putString(PREF_STANDARD_DRINK_COUNTRY, countryCode);
    }

}
