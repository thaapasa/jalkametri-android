package fi.tuska.jalkametri.util

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import fi.tuska.jalkametri.data.PreferencesImpl
import java.util.Locale

/**
 * Locale helper code copied from
 * https://gunhansancar.com/change-language-programmatically-in-android/
 */
object LocaleHelper {

    fun onAttach(context: Context): Context {
        val locale = PreferencesImpl(context).locale
        return setLocale(context, locale)
    }

    fun setLocale(context: Context, locale: Locale): Context =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) updateResources(context, locale)
            else updateResourcesLegacy(context, locale)

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLegacy(context: Context, locale: Locale): Context {
        Locale.setDefault(locale)

        val resources = context.resources

        val configuration = resources.configuration
        configuration.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLayoutDirection(locale)
        }

        resources.updateConfiguration(configuration, resources.displayMetrics)

        return context
    }
}
