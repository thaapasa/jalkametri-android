package fi.tuska.jalkametri.util;

import android.content.Context;
import android.content.res.Resources;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.activity.GUIActivity;
import fi.tuska.jalkametri.activity.JalkametriActivity;
import fi.tuska.jalkametri.dao.Preferences;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class TimeUtil {

    public static final long SECOND = 1000;
    public static final long MINUTE = 60 * SECOND;
    public static final long HOUR = 60 * MINUTE;
    public static final long DAY = 24 * HOUR;
    public static final long WEEK = 7 * DAY;

    private static final String TAG = "TimeUtil";

    private final Locale locale;
    private final Resources res;

    // These will be created when needed
    private NumberFormat numberFormat;
    private DateFormat dateFormatWDay;
    private DateFormat dateFormatFull;
    private DateFormat timeFormat;
    private DateFormat sqlDateFormat;

    public TimeUtil(Resources res, Locale locale) {
        this.res = res;
        this.locale = locale;
    }

    public TimeUtil(Resources res) {
        this.res = res;
        this.locale = res.getConfiguration().locale;
    }

    public TimeUtil(Context ctx) {
        this(ctx.getResources());
    }

    public Calendar getCurrentCalendar() {
        return Calendar.getInstance(locale);
    }

    public Date getCurrentTime() {
        return getCurrentCalendar().getTime();
    }

    /**
     * Returns the time instant specified by the given (user-friendly) day
     * specification (i.e., month is 1-based, 1=January; and hour is 0-23).
     * Sets the milliseconds to zero.
     *
     * @param year   the year as it is normally used (1900=1900, 2011=2011)
     * @param month  the month as it is normally used (1=January, ...,
     *               12=December)
     * @param day    the day of month (1=1st, 2=2nd, ...)
     * @param hour   the hour of day (from 0 to 23
     * @param minute the minute
     * @param second the second
     * @return the calendar instance configured to the given time
     */
    public Calendar getCalendar(int year, int month, int day, int hour, int minute, int second) {
        Calendar cal = getCurrentCalendar();
        cal.set(Calendar.YEAR, year);
        // First set day of month to 1 so that the day is not too high if a
        // shorter month is currently selected. I'm not sure if this is
        // required (depends on when the sanity of the date settings is
        // enforced), but this can't be bad.
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, month - 1 + Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * Returns the time instant specified by the given (user-friendly) day
     * specification (i.e., month is 1-based, 1=January; and hour is 0-23).
     * Sets the milliseconds to zero.
     *
     * @param year   the year as it is normally used (1900=1900, 2011=2011)
     * @param month  the month as it is normally used (1=January, ...,
     *               12=December)
     * @param day    the day of month (1=1st, 2=2nd, ...)
     * @param hour   the hour of day (from 0 to 23
     * @param minute the minute
     * @param second the second
     * @return the requested time instance
     */
    public Date getTime(int year, int month, int day, int hour, int minute, int second) {
        return getCalendar(year, month, day, hour, minute, second).getTime();
    }

    /**
     * @return true if the time specified in cal is after hour:min
     */
    public boolean isTimeAfter(Calendar cal, int hour, int min) {
        int cHour = cal.get(Calendar.HOUR_OF_DAY);
        int cMin = cal.get(Calendar.MINUTE);
        return cHour > hour || (cHour == hour && cMin > min);
    }

    /**
     * @return true if the time specified in cal is before hour:min
     */
    public boolean isTimeBefore(Calendar cal, int hour, int min) {
        int cHour = cal.get(Calendar.HOUR_OF_DAY);
        int cMin = cal.get(Calendar.MINUTE);
        return cHour < hour || (cHour == hour && cMin < min);
    }

    public Calendar getCurrentDrinkingCalendar(Preferences prefs) {
        Calendar cal = getCurrentCalendar();

        if (isTimeBefore(cal, prefs.getDayChangeHour(), prefs.getDayChangeMinute())) {
            cal.add(Calendar.DATE, -1);
        }
        return cal;
    }

    public Date getCurrentDrinkingDate(Preferences prefs) {
        return getCurrentDrinkingCalendar(prefs).getTime();
    }

    public Calendar getCalendar(Calendar calendar) {
        Calendar calCopy = getCurrentCalendar();
        calCopy.setTime(calendar.getTime());
        return calCopy;
    }

    public Calendar getCalendar(Date date) {
        Calendar cal = getCurrentCalendar();
        if (date == null)
            return cal;
        cal.setTime(date);
        return cal;
    }

    public DateTimeZone getTimeZone() {
        return DateTimeZone.forID("Europe/Helsinki");
    }

    public double getHourDifference(Date date1, Date date2) {
        return (date2.getTime() - date1.getTime()) / (double) HOUR;
    }

    public int getWeekNumber(Date cur, Preferences prefs) {
        Calendar cal = getCalendar(cur);
        // Android calendar always seems to start weeks on Sunday
        if (!prefs.isWeekStartMonday()) {
            // !monday == sunday, so the week is given correctly
            return cal.get(Calendar.WEEK_OF_YEAR);
        }
        // Check if the current date is Sunday
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            // Return the week number of yesterday (Saturday)
            cal.add(Calendar.DAY_OF_MONTH, -1);
            assert cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
            // Return the week number of yesterday
            return cal.get(Calendar.WEEK_OF_YEAR);
        }
        // Otherwise, week number is okay
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public Date addDays(Date cur, int days) {
        Calendar cal = getCalendar(cur);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    public Date add(Date cur, int what, int amount) {
        Calendar cal = getCalendar(cur);
        cal.add(what, amount);
        return cal.getTime();
    }

    public Calendar getCalendarFromDatePicker(int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = getCurrentCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        LogUtil.d(TAG, "Selected date: %s", cal);
        return cal;
    }

    public Calendar getStartOfToday(int dayChangeHour, int dayChangeMinute) {
        Calendar start = getCurrentCalendar();
        start.set(Calendar.HOUR_OF_DAY, 12);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        start.set(Calendar.HOUR_OF_DAY, dayChangeHour);
        start.set(Calendar.MINUTE, dayChangeMinute);

        Calendar now = getCurrentCalendar();
        while (start.after(now)) {
            start.add(Calendar.DAY_OF_MONTH, -1);
        }
        Calendar end = getCalendar(now);
        end.add(Calendar.DAY_OF_MONTH, 1);
        while (end.before(now)) {
            start.add(Calendar.DAY_OF_MONTH, 1);
            end.add(Calendar.DAY_OF_MONTH, 1);
        }

        return start;
    }

    public Calendar getStartOfDay(Date day, Preferences prefs) {
        Calendar start = getCalendar(day);

        start.set(Calendar.HOUR_OF_DAY, prefs.getDayChangeHour());
        start.set(Calendar.MINUTE, prefs.getDayChangeMinute());
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        return start;
    }

    public Date clearTimeValues(Date date) {
        Calendar cal = getCalendar(date);
        clearTimeValues(cal);
        return cal.getTime();
    }

    public void clearTimeValues(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    public Calendar getStartOfWeek(Date day, Preferences prefs) {
        // Get the start of today (day)
        Calendar cur = getStartOfDay(day, prefs);

        int firstDayOfWeek = prefs.isWeekStartMonday() ? Calendar.MONDAY : Calendar.SUNDAY;

        // Find the correct week starting date
        while (cur.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            cur.add(Calendar.DATE, -1);
        }
        // cur is now at the first day of week
        return cur;
    }

    public String toSQLDate(final Date date) {
        String asSQL = getSQLDateFormat().format(date);
        LogUtil.d(TAG, "Sql date is %s; parsed from %s", asSQL, date);
        return asSQL;
    }

    public Date fromSQLDate(final String dateString) {
        try {
            return getSQLDateFormat().parse(dateString);
        } catch (ParseException e) {
            LogUtil.w(TAG, "Invalid SQL date string: %s", dateString);
            return null;
        }
    }

    public DateFormat getSQLDateFormat() {
        if (sqlDateFormat == null) {
            sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        }
        return sqlDateFormat;
    }

    public Date getTimeAfterHours(final double hours) {
        return new Date(getCurrentTime().getTime() + (long) (hours * HOUR));
    }

    public DateFormat getDateFormatWDay() {
        if (dateFormatWDay == null) {
            String formatStr = res.getString(R.string.day_showday_format);
            dateFormatWDay = new SimpleDateFormat(formatStr, locale);
        }
        return dateFormatWDay;
    }

    public DateFormat getTimeFormat() {
        if (timeFormat == null) {
            String formatStr = res.getString(R.string.time_format);
            timeFormat = new SimpleDateFormat(formatStr, res.getConfiguration().locale);
        }
        return timeFormat;
    }

    public DateFormat getDateFormatFull() {
        if (dateFormatFull == null) {
            String formatStr = res.getString(R.string.day_full_format);
            dateFormatFull = new SimpleDateFormat(formatStr, res.getConfiguration().locale);
        }
        return dateFormatFull;
    }

    /**
     * @return the number formatter used for reading the numbers in
     * jAlkaMetri.
     */

    public NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance(locale);
            numberFormat.setMaximumFractionDigits(2);
        }
        assert numberFormat != null;
        return numberFormat;
    }

    public int getDaysInMonth(int month, int year) {
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            default:
                LogUtil.w(TAG, "Unknown month: %d/%d", month, year);
                return 0;
        }
    }

    public boolean isLeapYear(int year) {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }

    public DateFormat getMonthCorrectedDateFormat(final String pattern) {
        return new DateFormat() {

            private static final long serialVersionUID = 6206106978792376480L;

            private DateFormat formatter;
            private DateFormat[] formatters;

            /**
             * Constructor
             */ {
                formatter = new SimpleDateFormat(pattern);
                formatters = new DateFormat[12];
                for (int i = 0; i < 12; i++) {
                    String pat = pattern.replaceAll("MMMM?'", "'" + getMonthName(i));
                    pat = pat.replaceAll("'MMMM?", getMonthName(i) + "'");
                    pat = pat.replaceAll("'MMMM", "'" + getMonthName(i) + "'");
                    formatters[i] = new SimpleDateFormat(pat);
                }
            }

            private String getMonthName(int month) {
                switch (month) {
                    case Calendar.JANUARY:
                        return res.getString(R.string.month_january);
                    case Calendar.FEBRUARY:
                        return res.getString(R.string.month_february);
                    case Calendar.MARCH:
                        return res.getString(R.string.month_march);
                    case Calendar.APRIL:
                        return res.getString(R.string.month_april);
                    case Calendar.MAY:
                        return res.getString(R.string.month_may);
                    case Calendar.JUNE:
                        return res.getString(R.string.month_june);
                    case Calendar.JULY:
                        return res.getString(R.string.month_july);
                    case Calendar.AUGUST:
                        return res.getString(R.string.month_august);
                    case Calendar.SEPTEMBER:
                        return res.getString(R.string.month_september);
                    case Calendar.OCTOBER:
                        return res.getString(R.string.month_october);
                    case Calendar.NOVEMBER:
                        return res.getString(R.string.month_november);
                    case Calendar.DECEMBER:
                        return res.getString(R.string.month_december);
                }
                LogUtil.w(TAG, "Unknown month: %d", month);
                return "";
            }

            @Override
            public StringBuffer format(Date date, StringBuffer buffer, FieldPosition field) {
                Calendar cal = TimeUtil.this.getCalendar(date);
                return formatters[cal.get(Calendar.MONTH)].format(date, buffer, field);
            }

            @Override
            public Date parse(String string, ParsePosition position) {
                return formatter.parse(string, position);
            }

        };
    }

}
