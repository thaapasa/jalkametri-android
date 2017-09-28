package fi.tuska.jalkametri

import android.app.Application
import android.content.res.Configuration
import fi.tuska.jalkametri.dao.Preferences
import fi.tuska.jalkametri.data.PreferencesImpl
import fi.tuska.jalkametri.util.LocalizationUtil
import net.danlew.android.joda.JodaTimeAndroid

/**
 * The jAlkaMetri main application class. Locale-changing code copied from
 *
 * <pre>
 * http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 * </pre>
 */
class JalkametriApplication : Application() {

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        val prefs = PreferencesImpl(this)
        LocalizationUtil.setLocale(prefs.locale, baseContext)
    }

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)

        val prefs = PreferencesImpl(this)
        LocalizationUtil.setLocale(prefs.locale, baseContext)
    }

}
