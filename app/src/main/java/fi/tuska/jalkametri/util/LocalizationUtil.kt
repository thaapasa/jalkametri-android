package fi.tuska.jalkametri.util

import android.content.Context
import android.content.res.Resources
import fi.tuska.jalkametri.R
import java.util.Locale

object LocalizationUtil {

    private val TAG = "LocalizationUtil"

    val DEFAULT_LOCALE = Locale.ENGLISH
    val DEFAULT_LOCALE_NAME = R.string.language_en

    // Supported locales listed here (can be used by unit tests, for example)
    val LOCALE_EN = DEFAULT_LOCALE
    val LOCALE_FI = Locale("fi")
    val SUPPORTED_LOCALE_LIST = listOf(
            LocaleEntry(DEFAULT_LOCALE, DEFAULT_LOCALE_NAME),
            LocaleEntry(LOCALE_FI, R.string.language_fi))

    private val SUPPORTED_LOCALES = SUPPORTED_LOCALE_LIST.map { it.locale.language to it }.toMap()

    // Try to get the system default locale
    val defaultLocale: Locale
        get() {
            val def = SUPPORTED_LOCALES[Locale.getDefault().language]
            return def?.locale ?: DEFAULT_LOCALE
        }

    fun getLanguageName(language: String, res: Resources): String {
        val loc = SUPPORTED_LOCALES[language]
        val resCode = loc?.resourceString ?: DEFAULT_LOCALE_NAME
        return res.getString(resCode)
    }

    fun getLocale(language: String): Locale {
        val locale = SUPPORTED_LOCALES[language]
        return locale?.locale ?: defaultLocale
    }

    fun setLocale(locale: Locale, context: Context) {
        // Try to set the system locale to the selected locale
        LogUtil.d(TAG, "Selecting locale %s", locale)

        val res = context.resources
        Locale.setDefault(locale)

        val config = res.configuration
        if (config != null) {
            config.locale = locale
            res.updateConfiguration(config, res.displayMetrics)
        }
    }

    class LocaleEntry(val locale: Locale, val resourceString: Int)

}
