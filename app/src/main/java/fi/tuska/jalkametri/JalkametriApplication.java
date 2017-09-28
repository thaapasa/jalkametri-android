package fi.tuska.jalkametri;

import android.app.Application;
import android.content.res.Configuration;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.LocalizationUtil;
import net.danlew.android.joda.JodaTimeAndroid;

/**
 * The jAlkaMetri main application class. Locale-changing code copied from
 *
 * <pre>
 * http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 * </pre>
 *
 * @author Tuukka Haapasalo
 */
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
        JodaTimeAndroid.init(this);

        Preferences prefs = new PreferencesImpl(this);
        LocalizationUtil.setLocale(prefs.getLocale(), getBaseContext());
    }

}
