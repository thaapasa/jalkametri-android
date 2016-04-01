/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import fi.tuska.jalkametri.test.JalkametriTestCase;

/**
 * Unit tests for the time utility functions.
 * 
 * @author Tuukka Haapasalo
 */
public class TimeUtilTest extends JalkametriTestCase {

    public void testGetTime() {
        Date time = timeUtil.getTime(2011, 5, 16, 10, 35, 47);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        assertEquals(2011, cal.get(Calendar.YEAR));
        assertEquals(Calendar.MAY, cal.get(Calendar.MONTH));
        assertEquals(16, cal.get(Calendar.DAY_OF_MONTH));
        assertEquals(10, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(35, cal.get(Calendar.MINUTE));
        assertEquals(47, cal.get(Calendar.SECOND));
    }

    public void testDateFormatFull() {
        final Date date = timeUtil.getTime(2011, 5, 16, 10, 0, 0);

        DateFormat formatter = timeUtil.getDateFormatFull();
        assertEquals("Mon 5/16/2011 10:00", formatter.format(date));

        switchLocale(LocalizationUtil.LOCALE_FI);
        timeUtil = new TimeUtil(getContext());
        formatter = timeUtil.getDateFormatFull();
        assertEquals("ma 16.5.2011 10:00", formatter.format(date));
    }

    public void testTimeFormat() {
        final Date date = timeUtil.getTime(2011, 5, 16, 10, 0, 0);

        DateFormat formatter = timeUtil.getTimeFormat();
        assertEquals("10:00", formatter.format(date));

        switchLocale(LocalizationUtil.LOCALE_FI);
        formatter = timeUtil.getTimeFormat();
        assertEquals("10:00", formatter.format(date));
    }

    public void testDateFormatWDay() {
        final Date date = timeUtil.getTime(2011, 5, 16, 10, 0, 0);

        DateFormat formatter = null;

        switchLocale(LocalizationUtil.LOCALE_EN);
        timeUtil = new TimeUtil(getContext());
        formatter = timeUtil.getDateFormatWDay();
        assertEquals("Mon 5/16", formatter.format(date));

        switchLocale(LocalizationUtil.LOCALE_FI);
        timeUtil = new TimeUtil(getContext());
        formatter = timeUtil.getDateFormatWDay();
        assertEquals("ma 16.5.", formatter.format(date));

        switchLocale(LocalizationUtil.LOCALE_EN);
        timeUtil = new TimeUtil(getContext());
        formatter = timeUtil.getDateFormatWDay();
        assertEquals("Mon 5/16", formatter.format(date));
    }

    public void testTimeBefore() {
        Calendar cal = timeUtil.getCalendar(2011, 5, 16, 3, 30, 0);
        assertFalse(timeUtil.isTimeBefore(cal, 0, 0));
        assertFalse(timeUtil.isTimeBefore(cal, 0, 59));
        assertFalse(timeUtil.isTimeBefore(cal, 2, 0));
        assertFalse(timeUtil.isTimeBefore(cal, 2, 45));
        assertFalse(timeUtil.isTimeBefore(cal, 3, 0));
        assertFalse(timeUtil.isTimeBefore(cal, 3, 29));
        assertFalse(timeUtil.isTimeBefore(cal, 3, 30));
        assertTrue(timeUtil.isTimeBefore(cal, 3, 31));
        assertTrue(timeUtil.isTimeBefore(cal, 3, 35));
        assertTrue(timeUtil.isTimeBefore(cal, 4, 0));
        assertTrue(timeUtil.isTimeBefore(cal, 5, 0));
        assertTrue(timeUtil.isTimeBefore(cal, 24, 0));
    }

    public void testTimeAfter() {
        Calendar cal = timeUtil.getCalendar(2011, 5, 16, 3, 30, 0);
        assertTrue(timeUtil.isTimeAfter(cal, 0, 0));
        assertTrue(timeUtil.isTimeAfter(cal, 0, 59));
        assertTrue(timeUtil.isTimeAfter(cal, 2, 0));
        assertTrue(timeUtil.isTimeAfter(cal, 2, 45));
        assertTrue(timeUtil.isTimeAfter(cal, 3, 0));
        assertTrue(timeUtil.isTimeAfter(cal, 3, 29));
        assertFalse(timeUtil.isTimeAfter(cal, 3, 30));
        assertFalse(timeUtil.isTimeAfter(cal, 3, 35));
        assertFalse(timeUtil.isTimeAfter(cal, 4, 0));
        assertFalse(timeUtil.isTimeAfter(cal, 5, 0));
        assertFalse(timeUtil.isTimeAfter(cal, 24, 0));
    }

    public void testIsLeapYear() {
        assertTrue(timeUtil.isLeapYear(1996));
        assertTrue(timeUtil.isLeapYear(1992));
        assertTrue(timeUtil.isLeapYear(2012));
        assertTrue(timeUtil.isLeapYear(2000));
        assertTrue(timeUtil.isLeapYear(2400));

        assertFalse(timeUtil.isLeapYear(1998));
        assertFalse(timeUtil.isLeapYear(2001));
        assertFalse(timeUtil.isLeapYear(1997));
        assertFalse(timeUtil.isLeapYear(1900));
        assertFalse(timeUtil.isLeapYear(1800));
    }

    public void testGetDaysInMonth() {
        assertEquals(31, timeUtil.getDaysInMonth(1, 2001));
        assertEquals(31, timeUtil.getDaysInMonth(12, 2001));
        assertEquals(31, timeUtil.getDaysInMonth(1, 2000));
        assertEquals(30, timeUtil.getDaysInMonth(6, 2001));
        assertEquals(31, timeUtil.getDaysInMonth(7, 1999));
        assertEquals(31, timeUtil.getDaysInMonth(8, 1999));

        assertEquals(28, timeUtil.getDaysInMonth(2, 1999));
        assertEquals(29, timeUtil.getDaysInMonth(2, 1996));
        assertEquals(29, timeUtil.getDaysInMonth(2, 2000));
        assertEquals(28, timeUtil.getDaysInMonth(2, 2100));

        assertEquals(31, timeUtil.getDaysInMonth(1, 1993));
        assertEquals(28, timeUtil.getDaysInMonth(2, 1993));
        assertEquals(31, timeUtil.getDaysInMonth(3, 1993));
        assertEquals(30, timeUtil.getDaysInMonth(4, 1993));
        assertEquals(31, timeUtil.getDaysInMonth(5, 1993));
        assertEquals(30, timeUtil.getDaysInMonth(6, 1993));
        assertEquals(31, timeUtil.getDaysInMonth(7, 1993));
        assertEquals(31, timeUtil.getDaysInMonth(8, 1993));
        assertEquals(30, timeUtil.getDaysInMonth(9, 1993));
        assertEquals(31, timeUtil.getDaysInMonth(10, 1993));
        assertEquals(30, timeUtil.getDaysInMonth(11, 1993));
        assertEquals(31, timeUtil.getDaysInMonth(12, 1993));

        assertEquals(0, timeUtil.getDaysInMonth(0, 1993));
        assertEquals(0, timeUtil.getDaysInMonth(13, 1993));
    }

}
