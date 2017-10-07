package fi.tuska.jalkametri.test

import android.test.AndroidTestCase
import fi.tuska.jalkametri.dao.Preferences
import fi.tuska.jalkametri.data.PreferencesImpl
import fi.tuska.jalkametri.util.LocalizationUtil
import fi.tuska.jalkametri.util.TimeUtil
import junit.framework.Assert
import org.joda.time.DateTime
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import java.util.Locale

abstract class JalkametriTestCase protected constructor() : AndroidTestCase() {

    protected val tag: String = this.javaClass.simpleName
    protected lateinit var prefs: Preferences
    protected lateinit var timeUtil: TimeUtil

    @Throws(Exception::class)
    override fun setUp() {
        super.setUp()
        timeUtil = TimeUtil(context)
        prefs = PreferencesImpl(context)
        LocalizationUtil.setLocale(LocalizationUtil.DEFAULT_LOCALE, context)
    }

    protected fun switchLocale(locale: Locale) {
        LocalizationUtil.setLocale(locale, context)
    }

    protected val currentLocale: Locale
        get() = context.resources.configuration.locale

    protected fun assertSameTime(expected: Instant, actual: Instant) {
        Assert.assertEquals("Time not same; expected $expected; actual $actual", 0,
                actual.compareTo(expected))
    }

    protected fun assertSameTime(expected: LocalDate, actual: LocalDate) {
        Assert.assertEquals("Time not same; expected $expected; actual $actual", 0,
                actual.compareTo(expected))
    }

    @JvmOverloads protected fun assertCloseEnough(expected: Double, actual: Double, tolerance: Double = 0.0001) {
        val diff = Math.abs(actual - expected)
        if (diff > tolerance) {
            Assert.fail("Not close enough; expected $expected; actual $actual")
        }
    }

    protected fun setDayChangeTime(hour: Int, min: Int) {
        val editor = prefs.edit()
        prefs.setDayChangeHour(editor, hour)
        prefs.setDayChangeMinute(editor, min)
        editor.commit()
    }


    /**
     * Returns the time instant specified by the given (user-friendly) day
     * specification (i.e., month is 1-based, 1=January; and hour is 0-23).
     * Sets the milliseconds to zero.
     *
     * @param year   the year as it is normally used (1900=1900, 2011=2011)
     * @param month  the month as it is normally used (1=January, ...,
     * 12=December)
     * @param day    the day of month (1=1st, 2=2nd, ...)
     * @param hour   the hour of day (from 0 to 23
     * @param minute the minute
     * @param second the second
     * @return the requested time instance
     */
    fun getTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DateTime {
        return LocalDateTime(year, month, day, hour, minute, second).toDateTime(timeUtil.timeZone)
    }
}
