package fi.tuska.jalkametri.dao;

import android.content.SharedPreferences.Editor;
import fi.tuska.jalkametri.data.PurchaseReminderHandler;
import org.joda.time.LocalTime;

import java.util.Locale;

public interface Preferences {

    enum Gender {
        Male, Female
    }

    ;

    String PREF_WEIGHT = "pref_weight";
    String PREF_GENDER = "pref_gender";
    String PREF_DAY_CHANGE_HOUR = "pref_day_change_hour";
    String PREF_DAY_CHANGE_MINUTE = "pref_day_change_minute";
    String PREF_DRIVING_ALCOHOL_LIMIT = "pref_driving_alcohol_limit";
    String PREF_MAX_ALCOHOL_LEVEL = "pref_max_alcohol_level";
    String PREF_DRINK_LIB_INIT = "pref_drink_lib_init";
    String PREF_SOUNDS_ENABLED = "pref_sounds";
    String PREF_DEBUG_MODE = "pref_debug_mode";
    String PREF_WEEK_START_MONDAY = "pref_week_start_monday";
    String PREF_DISCLAIMER_READ = "pref_disclaimer_read";
    String PREF_LOCALE = "pref_locale";
    String PREF_LICENSE_PURCHASED = "license_purchased";
    String PREF_ADS_ENABLED = "ads_enabled";
    String PREF_SHOW_REMINDER_AFTER = "show_reminder_after";
    String PREF_STANDARD_DRINK_COUNTRY = "standard_drink_country";

    double DEFAULT_WEIGHT = 70f;
    Gender DEFAULT_GENDER = Gender.Male;
    String DEFAULT_TIME = "06:00";
    double DEFAULT_DRIVING_ALCOHOL_LIMIT = 0.5f;
    int DEFAULT_DAY_CHANGE_HOUR = 6;
    int DEFAULT_DAY_CHANGE_MINUTE = 0;
    double DEFAULT_MAX_ALHOCOL_LEVEL = 2.0f;
    boolean DEFAULT_DEBUG_MODE = false;
    boolean DEFAULT_SOUNDS = true;
    boolean DEFAULT_WEEK_START_MONDAY = true;
    boolean DEFAULT_DISCLAIMER_READ = false;
    boolean DEFAULT_LICENSE_PURCHASED = false;
    boolean DEFAULT_ADS_ENABLED = true;
    int DEFAULT_SHOW_REMINDER_AFTER = PurchaseReminderHandler.DEFAULT_SHOW_AFTER;
    String DEFAULT_STANDARD_DRINK_COUNTRY = "fi";

    /**
     * @return the alcohol level limit for driving a car, in promilles. In Finland, this is 0.5.
     */
    double getDrivingAlcoholLimit();

    void setDrivingAlcoholLimit(Editor editor, double limit);

    /**
     * @return the maximum alcohol level shown in the alcohol meter.
     */
    double getMaxAlcoholLevel();

    void setMaxAlcoholLevel(Editor editor, double level);

    /**
     * @return the user's weight.
     */
    double getWeight();

    void setWeight(Editor editor, double weight);

    /**
     * @return the user's gender.
     */
    Gender getGender();

    void setGender(Editor editor, Gender gender);

    int getDayChangeHour();

    void setDayChangeHour(Editor editor, int hour);

    int getDayChangeMinute();

    void setDayChangeMinute(Editor editor, int minute);

    LocalTime getDayChangeTime();

    boolean isDrinkLibraryInitialized();

    void setDrinkLibraryInitialized(Editor editor, boolean state);

    boolean isDebugMode();

    void setDebugMode(Editor editor, boolean state);

    boolean isSoundsEnabled();

    void setSoundsEnabled(Editor editor, boolean state);

    boolean isWeekStartMonday();

    void setWeekStartMonday(Editor editor, boolean state);

    boolean isDisclaimerRead();

    void setDisclaimerRead(Editor editor, boolean state);

    boolean isLicensePurchased();

    void setLicensePurchased(Editor editor, boolean state);

    boolean isAdsEnabled();

    void setAdsEnabled(Editor editor, boolean state);

    Locale getLocale();

    void setLocale(Editor editor, Locale locale);

    int getShowReminderAfter();

    void setShowReminderAfter(Editor editor, int steps);

    String getStandardDrinkCountry();

    void setStandardDrinkCountry(Editor editor, String countryCode);

    double getStandardDrinkAlcoholWeight();

    Editor edit();

}
