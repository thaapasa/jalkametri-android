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

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import fi.tuska.jalkametri.R;

public final class LocalizationUtil {

    private static final String TAG = "LocalizationUtil";
    private static final Map<String, LocaleEntry> SUPPORTED_LOCALES = new TreeMap<String, LocaleEntry>();
    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;
    public static final int DEFAULT_LOCALE_NAME = R.string.language_en;

    // Supported locales listed here (can be used by unit tests, for example)
    public static final Locale LOCALE_EN = DEFAULT_LOCALE;
    public static final Locale LOCALE_FI = new Locale("fi");

    static {
        // English
        SUPPORTED_LOCALES.put(DEFAULT_LOCALE.getLanguage(), new LocaleEntry(DEFAULT_LOCALE,
            DEFAULT_LOCALE_NAME));
        // Finnish
        SUPPORTED_LOCALES.put(LOCALE_FI.getLanguage(), new LocaleEntry(LOCALE_FI,
            R.string.language_fi));
    }

    private LocalizationUtil() {
        // No instantiation required
    }

    public static Collection<String> getSupportedLocales() {
        return SUPPORTED_LOCALES.keySet();
    }

    public static String getLanguageName(String language, Resources res) {
        LocaleEntry loc = SUPPORTED_LOCALES.get(language);
        int resCode = loc != null ? loc.resourceString : DEFAULT_LOCALE_NAME;
        return res.getString(resCode);
    }

    public static Locale getLocale(String language) {
        LocaleEntry locale = SUPPORTED_LOCALES.get(language);
        return locale != null ? locale.locale : getDefaultLocale();
    }

    public static Locale getDefaultLocale() {
        // Try to get the system default locale
        LocaleEntry def = SUPPORTED_LOCALES.get(Locale.getDefault().getLanguage());
        return def != null ? def.locale : DEFAULT_LOCALE;
    }

    public static void setLocale(Locale locale, Context context) {
        // Try to set the system locale to the selected locale
        LogUtil.d(TAG, "Selecting locale %s", locale);

        Resources res = context.getResources();
        Locale.setDefault(locale);

        Configuration config = res.getConfiguration();
        if (config != null) {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
    }

    private static class LocaleEntry {
        private final Locale locale;
        private final int resourceString;

        private LocaleEntry(Locale locale, int resourceString) {
            this.locale = locale;
            this.resourceString = resourceString;
        }

    }

}
