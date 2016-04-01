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

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;
import android.content.res.Configuration;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.LocalizationUtil;

/**
 * The jAlkaMetri main application class. Locale-changing code copied from
 * 
 * <pre>
 * http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 * </pre>
 * 
 * @author Tuukka Haapasalo
 */
@ReportsCrashes(formKey = PrivateData.STACK_TRACE_DOCS_FORM_KEY, mode = ReportingInteractionMode.TOAST, resToastText = R.string.crash_toast_text)
public class JalkametriApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Preferences prefs = new PreferencesImpl(this);
        LocalizationUtil.setLocale(prefs.getLocale(), getBaseContext());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // The following line is required to initialize ACRA
        if (PrivateData.ACRA_ERROR_REPORTING_ENABLED) {
            ACRA.init(this);
        }

        Preferences prefs = new PreferencesImpl(this);
        LocalizationUtil.setLocale(prefs.getLocale(), getBaseContext());
    }

}
