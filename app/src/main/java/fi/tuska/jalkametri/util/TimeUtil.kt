package fi.tuska.jalkametri.util

import android.content.Context
import android.content.res.Resources
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.Preferences
import org.joda.time.DateTimeZone
import java.text.DateFormat
import java.text.FieldPosition
import java.text.NumberFormat
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

class TimeUtil(val res: Resources, val locale: Locale) {

    val currentCalendar: Calendar
        get() = Calendar.getInstance(locale)

    val currentTime: Date
        get() = currentCalendar.time

    val timeZone: DateTimeZone
        get() = DateTimeZone.forID("Europe/Helsinki")

    constructor(res: Resources) : this(res, res.configuration.locale)
    constructor(ctx: Context) : this(ctx.resources)

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
     * @return the calendar instance configured to the given time
     */
    fun getCalendar(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Calendar {
        val cal = currentCalendar
        cal.set(Calendar.YEAR, year)
        // First set day of month to 1 so that the day is not too high if a
        // shorter month is currently selected. I'm not sure if this is
        // required (depends on when the sanity of the date settings is
        // enforced), but this can't be bad.
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.MONTH, month - 1 + Calendar.JANUARY)
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, second)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
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
    fun getTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Date {
        return getCalendar(year, month, day, hour, minute, second).time
    }

    /**
     * @return true if the time specified in cal is after hour:min
     */
    fun isTimeAfter(cal: Calendar, hour: Int, min: Int): Boolean {
        val cHour = cal.get(Calendar.HOUR_OF_DAY)
        val cMin = cal.get(Calendar.MINUTE)
        return cHour > hour || cHour == hour && cMin > min
    }

    /**
     * @return true if the time specified in cal is before hour:min
     */
    fun isTimeBefore(cal: Calendar, hour: Int, min: Int): Boolean {
        val cHour = cal.get(Calendar.HOUR_OF_DAY)
        val cMin = cal.get(Calendar.MINUTE)
        return cHour < hour || cHour == hour && cMin < min
    }

    fun getCurrentDrinkingCalendar(prefs: Preferences): Calendar {
        val cal = currentCalendar

        if (isTimeBefore(cal, prefs.dayChangeHour, prefs.dayChangeMinute)) {
            cal.add(Calendar.DATE, -1)
        }
        return cal
    }

    fun getCurrentDrinkingDate(prefs: Preferences): Date {
        return getCurrentDrinkingCalendar(prefs).time
    }

    fun getCalendar(calendar: Calendar): Calendar {
        val calCopy = currentCalendar
        calCopy.time = calendar.time
        return calCopy
    }

    fun getCalendar(date: Date?): Calendar {
        val cal = currentCalendar
        if (date == null)
            return cal
        cal.time = date
        return cal
    }

    fun getHourDifference(date1: Date, date2: Date): Double {
        return (date2.time - date1.time) / HOUR.toDouble()
    }

    fun getWeekNumber(cur: Date, prefs: Preferences): Int {
        val cal = getCalendar(cur)
        // Android calendar always seems to start weeks on Sunday
        if (!prefs.isWeekStartMonday) {
            // !monday == sunday, so the week is given correctly
            return cal.get(Calendar.WEEK_OF_YEAR)
        }
        // Check if the current date is Sunday
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // Return the week number of yesterday (Saturday)
            cal.add(Calendar.DAY_OF_MONTH, -1)
            assert(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY)
            // Return the week number of yesterday
            return cal.get(Calendar.WEEK_OF_YEAR)
        }
        // Otherwise, week number is okay
        return cal.get(Calendar.WEEK_OF_YEAR)
    }

    fun addDays(cur: Date, days: Int): Date {
        val cal = getCalendar(cur)
        cal.add(Calendar.DAY_OF_MONTH, days)
        return cal.time
    }

    fun add(cur: Date, what: Int, amount: Int): Date {
        val cal = getCalendar(cur)
        cal.add(what, amount)
        return cal.time
    }

    fun getCalendarFromDatePicker(year: Int, monthOfYear: Int, dayOfMonth: Int): Calendar {
        val cal = currentCalendar
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, monthOfYear)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        LogUtil.d(TAG, "Selected date: %s", cal)
        return cal
    }

    fun getStartOfToday(dayChangeHour: Int, dayChangeMinute: Int): Calendar {
        val start = currentCalendar
        start.set(Calendar.HOUR_OF_DAY, 12)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        start.set(Calendar.HOUR_OF_DAY, dayChangeHour)
        start.set(Calendar.MINUTE, dayChangeMinute)

        val now = currentCalendar
        while (start.after(now)) {
            start.add(Calendar.DAY_OF_MONTH, -1)
        }
        val end = getCalendar(now)
        end.add(Calendar.DAY_OF_MONTH, 1)
        while (end.before(now)) {
            start.add(Calendar.DAY_OF_MONTH, 1)
            end.add(Calendar.DAY_OF_MONTH, 1)
        }

        return start
    }

    fun getStartOfDay(day: Date, prefs: Preferences): Calendar {
        val start = getCalendar(day)

        start.set(Calendar.HOUR_OF_DAY, prefs.dayChangeHour)
        start.set(Calendar.MINUTE, prefs.dayChangeMinute)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        return start
    }

    fun clearTimeValues(date: Date): Date {
        val cal = getCalendar(date)
        clearTimeValues(cal)
        return cal.time
    }

    fun clearTimeValues(cal: Calendar) {
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
    }

    fun getStartOfWeek(day: Date, prefs: Preferences): Calendar {
        // Get the start of today (day)
        val cur = getStartOfDay(day, prefs)

        val firstDayOfWeek = if (prefs.isWeekStartMonday) Calendar.MONDAY else Calendar.SUNDAY

        // Find the correct week starting date
        while (cur.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            cur.add(Calendar.DATE, -1)
        }
        // cur is now at the first day of week
        return cur
    }

    fun toSQLDate(date: Date): String {
        return sqlDateFormat.format(date).apply {
            LogUtil.d(TAG, "Sql date is %s; parsed from %s", this, date)
        }
    }

    fun fromSQLDate(dateString: String): Date? {
        return try {
            sqlDateFormat.parse(dateString)
        } catch (e: ParseException) {
            LogUtil.w(TAG, "Invalid SQL date string: %s", dateString)
            null
        }
    }

    val sqlDateFormat: DateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", locale)

    fun getTimeAfterHours(hours: Double): Date {
        return Date(currentTime.time + (hours * HOUR).toLong())
    }

    val dateFormatWDay: DateFormat
        get() = SimpleDateFormat(res.getString(R.string.day_showday_format), locale)

    val timeFormat: DateFormat
        get() = SimpleDateFormat(res.getString(R.string.time_format), locale)

    val dateFormatFull: DateFormat
        get() = SimpleDateFormat(res.getString(R.string.day_full_format), locale)

    /**
     * @return the number formatter used for reading the numbers in
     * jAlkaMetri.
     */

    fun getNumberFormat(): NumberFormat = NumberFormat.getInstance(locale).apply {
        maximumFractionDigits = 2
    }

    fun getDaysInMonth(month: Int, year: Int): Int = when (month) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        2 -> if (isLeapYear(year)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 0.apply {
            LogUtil.w(TAG, "Unknown month: %d/%d", month, year)
        }
    }

    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    fun getMonthCorrectedDateFormat(pattern: String): DateFormat {
        return object : DateFormat() {

            private val formatter: DateFormat = SimpleDateFormat(pattern, locale)
            private val formatters: List<DateFormat> = (0..11).map {
                val pat = pattern.replace("MMMM?'".toRegex(), "'" + getMonthName(it))
                        .replace("'MMMM?".toRegex(), getMonthName(it) + "'")
                        .replace("'MMMM".toRegex(), "'" + getMonthName(it) + "'")
                SimpleDateFormat(pat, locale)
            }

            private fun getMonthName(month: Int): String {
                return when (month) {
                    Calendar.JANUARY -> res.getString(R.string.month_january)
                    Calendar.FEBRUARY -> res.getString(R.string.month_february)
                    Calendar.MARCH -> res.getString(R.string.month_march)
                    Calendar.APRIL -> res.getString(R.string.month_april)
                    Calendar.MAY -> res.getString(R.string.month_may)
                    Calendar.JUNE -> res.getString(R.string.month_june)
                    Calendar.JULY -> res.getString(R.string.month_july)
                    Calendar.AUGUST -> res.getString(R.string.month_august)
                    Calendar.SEPTEMBER -> res.getString(R.string.month_september)
                    Calendar.OCTOBER -> res.getString(R.string.month_october)
                    Calendar.NOVEMBER -> res.getString(R.string.month_november)
                    Calendar.DECEMBER -> res.getString(R.string.month_december)
                    else -> "".apply {
                        LogUtil.w(TAG, "Unknown month: %d", month)
                    }
                }
            }

            override fun format(date: Date, buffer: StringBuffer, field: FieldPosition): StringBuffer {
                val cal = this@TimeUtil.getCalendar(date)
                return formatters[cal.get(Calendar.MONTH)].format(date, buffer, field)
            }

            override fun parse(string: String, position: ParsePosition): Date {
                return formatter.parse(string, position)
            }

        }
    }

    companion object {

        val SECOND: Long = 1000
        val MINUTE = 60 * SECOND
        val HOUR = 60 * MINUTE
        val DAY = 24 * HOUR
        val WEEK = 7 * DAY

        private val TAG = "TimeUtil"
    }

}
