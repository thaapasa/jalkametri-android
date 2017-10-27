package fi.tuska.jalkametri.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import java.text.NumberFormat
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

    fun getHourDifference(from: Instant, to: Instant): Double = (to.millis - from.millis).toDouble() / HOUR_MS

    fun getStartOfDrinkDay(day: LocalDate, prefs: Preferences): Instant =
            day.toDateTime(prefs.dayChangeTime, timeZone).toInstant()

    fun getStartOfWeek(day: LocalDate?, prefs: Preferences): LocalDate {
        var cur = day ?: LocalDate.now()
        val firstDayOfWeek = if (prefs.isWeekStartMonday) DateTimeConstants.MONDAY else DateTimeConstants.SUNDAY
        while (cur.dayOfWeek != firstDayOfWeek) {
            cur = cur.minusDays(1)
        }
        return cur
    }

    fun toSQLDate(date: Instant): String = sqlDateFormat.print(date).apply {
        LogUtil.d(TAG, "Sql date is %s; parsed from %s", this, date)
    }

    fun fromSQLDate(dateString: String): LocalDate? = try {
        LocalDate.parse(dateString, sqlDateFormat)
    } catch (e: Exception) {
        LogUtil.w(TAG, "Invalid SQL date string: %s", dateString)
        null
    }

    fun getTimeAfterHours(hours: Double): Instant = Instant.now().plus(Duration.millis((hours * HOUR_MS).toLong()))

    private val sqlDateFormat: DateTimeFormatter = timeFormatter("yyyy-MM-dd")

    val dateFormatWDay: DateTimeFormatter
        get() = timeFormatter(res.getString(R.string.day_showday_format))

    val timeFormat: DateTimeFormatter
        get() = timeFormatter(res.getString(R.string.time_format))

    val dateFormat: DateTimeFormatter
        get() = timeFormatter(res.getString(R.string.day_format))

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

    fun pickDate(context: Context, startDate: LocalDate, callback: (LocalDate) -> Unit) {
        DatePickerDialog(context,
                DatePickerDialog.OnDateSetListener { _, y, m, d -> callback(LocalDate(y, m + 1, d)) },
                startDate.year, startDate.monthOfYear - 1, startDate.dayOfMonth).show()
    }

    fun pickTime(context: Context, startTime: LocalTime, callback: (LocalTime) -> Unit) {
        TimePickerDialog(context,
                TimePickerDialog.OnTimeSetListener { _, h, m -> callback(LocalTime(h, m)) },
                startTime.hourOfDay, startTime.minuteOfHour, true).show()
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
