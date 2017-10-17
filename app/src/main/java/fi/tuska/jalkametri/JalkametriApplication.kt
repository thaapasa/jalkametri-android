package fi.tuska.jalkametri

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import fi.tuska.jalkametri.dao.Preferences
import fi.tuska.jalkametri.data.PreferencesImpl
import fi.tuska.jalkametri.util.LocaleHelper
import fi.tuska.jalkametri.util.TimeUtil
import net.danlew.android.joda.JodaTimeAndroid

/**
 * The jAlkaMetri main application class. Locale-changing code copied from
 *
 * <pre>
 * http://stackoverflow.com/questions/2264874/android-changing-locale-within-the-app-itself
 * </pre>
 */
class JalkametriApplication : Application() {

    lateinit var timeUtil: TimeUtil

    lateinit var prefs: Preferences

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        reset()
    }

    override fun onCreate() {
        super.onCreate()
        JodaTimeAndroid.init(this)
        reset()
    }

    private fun reset() {
        val p = PreferencesImpl(this)
        val locale = p.locale
        LocaleHelper.setLocale(baseContext, locale)

        timeUtil = TimeUtil(resources, locale)
        prefs = p
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }

}
