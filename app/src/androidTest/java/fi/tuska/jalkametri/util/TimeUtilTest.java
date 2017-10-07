package fi.tuska.jalkametri.util;

import fi.tuska.jalkametri.test.JalkametriTestCase;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;

/**
 * Unit tests for the time utility functions.
 *
 * @author Tuukka Haapasalo
 */
public class TimeUtilTest extends JalkametriTestCase {

    public void testGetTime() {
        DateTime time = getTime(2011, 5, 16, 10, 35, 47).toDateTime();
        assertEquals(2011, time.getYear());
        assertEquals(Calendar.MAY, time.getMonthOfYear());
        assertEquals(16, time.getDayOfMonth());
        assertEquals(10, time.getHourOfDay());
        assertEquals(35, time.getMinuteOfHour());
        assertEquals(47, time.getSecondOfMinute());
    }

    public void testDateFormatFull() {
        final DateTime date = getTime(2011, 5, 16, 10, 0, 0);

        DateTimeFormatter formatter = getTimeUtil().getDateFormatFull();
        assertEquals("Mon 5/16/2011 10:00", formatter.print(date));

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_FI());
        setTimeUtil(new TimeUtil(getContext()));
        formatter = getTimeUtil().getDateFormatFull();
        assertEquals("ma 16.5.2011 10:00", formatter.print(date));
    }

    public void testTimeFormat() {
        final DateTime date = getTime(2011, 5, 16, 10, 0, 0);

        DateTimeFormatter formatter = getTimeUtil().getTimeFormat();
        assertEquals("10:00", formatter.print(date));

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_FI());
        formatter = getTimeUtil().getTimeFormat();
        assertEquals("10:00", formatter.print(date));
    }

    public void testDateFormatWDay() {
        final DateTime date = getTime(2011, 5, 16, 10, 0, 0);

        DateTimeFormatter formatter = null;

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_EN());
        setTimeUtil(new TimeUtil(getContext()));
        formatter = getTimeUtil().getDateFormatWDay();
        assertEquals("Mon 5/16", formatter.print(date));

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_FI());
        setTimeUtil(new TimeUtil(getContext()));
        formatter = getTimeUtil().getDateFormatWDay();
        assertEquals("ma 16.5.", formatter.print(date));

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_EN());
        setTimeUtil(new TimeUtil(getContext()));
        formatter = getTimeUtil().getDateFormatWDay();
        assertEquals("Mon 5/16", formatter.print(date));
    }

    public void testIsLeapYear() {
        assertTrue(getTimeUtil().isLeapYear(1996));
        assertTrue(getTimeUtil().isLeapYear(1992));
        assertTrue(getTimeUtil().isLeapYear(2012));
        assertTrue(getTimeUtil().isLeapYear(2000));
        assertTrue(getTimeUtil().isLeapYear(2400));

        assertFalse(getTimeUtil().isLeapYear(1998));
        assertFalse(getTimeUtil().isLeapYear(2001));
        assertFalse(getTimeUtil().isLeapYear(1997));
        assertFalse(getTimeUtil().isLeapYear(1900));
        assertFalse(getTimeUtil().isLeapYear(1800));
    }

    public void testGetDaysInMonth() {
        assertEquals(31, getTimeUtil().getDaysInMonth(1, 2001));
        assertEquals(31, getTimeUtil().getDaysInMonth(12, 2001));
        assertEquals(31, getTimeUtil().getDaysInMonth(1, 2000));
        assertEquals(30, getTimeUtil().getDaysInMonth(6, 2001));
        assertEquals(31, getTimeUtil().getDaysInMonth(7, 1999));
        assertEquals(31, getTimeUtil().getDaysInMonth(8, 1999));

        assertEquals(28, getTimeUtil().getDaysInMonth(2, 1999));
        assertEquals(29, getTimeUtil().getDaysInMonth(2, 1996));
        assertEquals(29, getTimeUtil().getDaysInMonth(2, 2000));
        assertEquals(28, getTimeUtil().getDaysInMonth(2, 2100));

        assertEquals(31, getTimeUtil().getDaysInMonth(1, 1993));
        assertEquals(28, getTimeUtil().getDaysInMonth(2, 1993));
        assertEquals(31, getTimeUtil().getDaysInMonth(3, 1993));
        assertEquals(30, getTimeUtil().getDaysInMonth(4, 1993));
        assertEquals(31, getTimeUtil().getDaysInMonth(5, 1993));
        assertEquals(30, getTimeUtil().getDaysInMonth(6, 1993));
        assertEquals(31, getTimeUtil().getDaysInMonth(7, 1993));
        assertEquals(31, getTimeUtil().getDaysInMonth(8, 1993));
        assertEquals(30, getTimeUtil().getDaysInMonth(9, 1993));
        assertEquals(31, getTimeUtil().getDaysInMonth(10, 1993));
        assertEquals(30, getTimeUtil().getDaysInMonth(11, 1993));
        assertEquals(31, getTimeUtil().getDaysInMonth(12, 1993));

        assertEquals(0, getTimeUtil().getDaysInMonth(0, 1993));
        assertEquals(0, getTimeUtil().getDaysInMonth(13, 1993));
    }

}
