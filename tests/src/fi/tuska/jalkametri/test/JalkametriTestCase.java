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
