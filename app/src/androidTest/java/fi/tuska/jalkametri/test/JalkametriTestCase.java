package fi.tuska.jalkametri.test;

import java.util.Date;
import java.util.Locale;

import android.content.SharedPreferences.Editor;
import android.test.AndroidTestCase;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.LocalizationUtil;
import fi.tuska.jalkametri.util.TimeUtil;

public abstract class JalkametriTestCase extends AndroidTestCase {

    protected final String tag;
    protected Preferences prefs;

    protected TimeUtil timeUtil;

    protected JalkametriTestCase() {
        tag = this.getClass().getSimpleName();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        timeUtil = new TimeUtil(getContext());
        prefs = new PreferencesImpl(getContext());
        LocalizationUtil.setLocale(LocalizationUtil.DEFAULT_LOCALE, getContext());
    }

    protected void switchLocale(Locale locale) {
        LocalizationUtil.setLocale(locale, getContext());
    }

    protected Locale getCurrentLocale() {
        return getContext().getResources().getConfiguration().locale;
    }

    protected void assertSameTime(Date expected, Date actual) {
        assertEquals("Time not same; expected " + expected + "; actual " + actual, 0,
            actual.compareTo(expected));
    }

    protected void assertCloseEnough(double expected, double actual) {
        assertCloseEnough(expected, actual, 0.0001d);
    }

    protected void assertCloseEnough(double expected, double actual, double tolerance) {
        double diff = Math.abs(actual - expected);
        if (diff > tolerance) {
            fail("Not close enough; expected " + expected + "; actual " + actual);
        }
    }

    protected void setDayChangeTime(int hour, int min) {
        Editor editor = prefs.edit();
        prefs.setDayChangeHour(editor, hour);
        prefs.setDayChangeMinute(editor, min);
        editor.commit();
    }

}
