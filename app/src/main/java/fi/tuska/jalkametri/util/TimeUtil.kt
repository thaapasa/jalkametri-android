package fi.tuska.jalkametri.util

import android.content.Context
import android.content.res.Resources
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.Preferences
import org.joda.time.DateTimeConstants
import org.joda.time.DateTimeZone
import org.joda.time.Duration
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import org.joda.time.Period
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.NumberFormat
import java.text.ParseException
import java.util.Locale

class TimeUtil(val res: Resources, val locale: Locale) {

    constructor(res: Resources) : this(res, res.configuration.locale)
    constructor(ctx: Context) : this(ctx.resources)

    val timeZone: DateTimeZone = DateTimeZone.getDefault()

    fun timeFormatter(pattern: String): DateTimeFormatter =
            DateTimeFormat.forPattern(pattern).withLocale(locale).withZone(timeZone)

    fun dayChangeTime(prefs: Preferences): LocalTime =
            LocalTime(prefs.dayChangeHour, prefs.dayChangeMinute)

    fun getCurrentDrinkingDate(prefs: Preferences): LocalDate {
        val time = Instant.now()
        val dt = time.toDateTime(timeZone)

        return dt.toLocalDate().let {
            if (dt.toLocalTime().isBefore(dayChangeTime(prefs))) it.minusDays(1)
            else it
        }
    }

    fun getHourDifference(from: Instant, to: Instant): Double =
            Period(to, from).millis.toDouble() / HOUR_MS

    fun getStartOfDrinkDay(day: LocalDate, prefs: Preferences): Instant =
            day.toDateTime(prefs.dayChangeTime, timeZone).toInstant()

    fun getStartOfWeek(day: LocalDate, prefs: Preferences): LocalDate {
        var cur = day
        val firstDayOfWeek = if (prefs.isWeekStartMonday) DateTimeConstants.MONDAY else DateTimeConstants.SUNDAY
        while (cur.dayOfWeek != firstDayOfWeek) {
            cur = cur.minusDays(1)
        }
        return cur
    }

    fun toSQLDate(date: Instant): String = sqlDateFormat.print(date).apply {
        LogUtil.d(TAG, "Sql date is %s; parsed from %s", this, date)
    }

    fun fromSQLDate(dateString: String): Instant? = try {
        Instant.parse(dateString, sqlDateFormat)
    } catch (e: ParseException) {
        LogUtil.w(TAG, "Invalid SQL date string: %s", dateString)
        null
    }

    fun getTimeAfterHours(hours: Double): Instant = Instant.now().plus(Duration.millis((hours * HOUR_MS).toLong()))

    val sqlDateFormat: DateTimeFormatter
        get() = timeFormatter("yyyy-MM-dd")

    val dateFormatWDay: DateTimeFormatter
        get() = timeFormatter(res.getString(R.string.day_showday_format))

    val timeFormat: DateTimeFormatter
        get() = timeFormatter(res.getString(R.string.time_format))

    val dateFormatFull: DateTimeFormatter
        get() = timeFormatter(res.getString(R.string.day_full_format))

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

    fun getMonthCorrectedDateFormat(pattern: String): DateTimeFormatter {
        return DateTimeFormat.forPattern(pattern).withLocale(locale).withZone(timeZone)
        /*object : DateFormat() {

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

        }*/
    }

    companion object {

        val SECOND_MS: Long = 1000
        val MINUTE_MS = 60 * SECOND_MS
        val HOUR_MS = 60 * MINUTE_MS
        val DAY_MS = 24 * HOUR_MS
        val WEEK_MS = 7 * DAY_MS

        private val TAG = "TimeUtil"
    }

}
