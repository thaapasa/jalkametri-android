package fi.tuska.jalkametri

import android.app.Application
import android.content.res.Configuration
import fi.tuska.jalkametri.dao.Preferences
import fi.tuska.jalkametri.data.PreferencesImpl
import fi.tuska.jalkametri.util.LocalizationUtil
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

    val timeUtil: TimeUtil
        get() = currentTimeUtil!!

    val prefs: Preferences
        get() = currentPrefs!!

    private var currentTimeUtil: TimeUtil? = null
    private var currentPrefs: Preferences? = null

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
        LocalizationUtil.setLocale(locale, baseContext)

        currentTimeUtil = TimeUtil(resources, locale)
        currentPrefs = p
    }

}
