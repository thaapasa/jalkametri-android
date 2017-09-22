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
