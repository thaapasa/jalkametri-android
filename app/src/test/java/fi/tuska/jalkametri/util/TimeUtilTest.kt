package fi.tuska.jalkametri.util

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDateTime
import org.junit.Before
import org.junit.Test
import java.util.Locale

class TimeUtilTest {

    private val FI = Locale("fi", "FI")
    private val EN = Locale.ENGLISH

    private val timeUtilEN = TimeUtil(MockResources.en(), EN)
    private val timeUtilFI = TimeUtil(MockResources.fi(), FI)
    private val timeUtil = timeUtilEN

    fun getTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): DateTime
            = LocalDateTime(year, month, day, hour, minute, second).toDateTime(timeUtil.timeZone)

    @Before
    fun before() {
        LogUtil.logger = NoopLogger
    }

    @Test
    fun testGetTime() {
        val time = getTime(2011, 5, 16, 10, 35, 47).toDateTime()
        assertEquals(2011, time.year)
        assertEquals(DateTimeConstants.MAY, time.monthOfYear)
        assertEquals(5, time.monthOfYear)
        assertEquals(16, time.dayOfMonth)
        assertEquals(10, time.hourOfDay)
        assertEquals(35, time.minuteOfHour)
        assertEquals(47, time.secondOfMinute)
    }

    @Test
    fun testDateFormatFull() {
        val date = getTime(2011, 5, 16, 10, 0, 0)

        timeUtil.dateFormatFull.let { formatter ->
            assertEquals("Mon 5/16/2011 10:00", formatter.print(date))
        }

        timeUtilFI.dateFormatFull.let { formatter ->
            assertEquals("ma 16.5.2011 10:00", formatter.print(date))
        }
    }

    @Test
    fun testTimeFormat() {
        val date = getTime(2011, 5, 16, 10, 0, 0)

        timeUtil.timeFormat.let { formatter ->
            assertEquals("10:00", formatter.print(date))
        }

        timeUtilFI.timeFormat.let { formatter ->
            assertEquals("10:00", formatter.print(date))
        }
    }

    @Test
    fun testDateFormatWDay() {
        val date = getTime(2011, 5, 16, 10, 0, 0)
        assertEquals("Mon 5/16", timeUtilEN.dateFormatWDay.print(date))
        assertEquals("ma 16.5.", timeUtilFI.dateFormatWDay.print(date))
    }

    @Test
    fun testIsLeapYear() {
        timeUtil.apply {
            assertTrue(isLeapYear(1996))
            assertTrue(isLeapYear(1992))
            assertTrue(isLeapYear(2012))
            assertTrue(isLeapYear(2000))
            assertTrue(isLeapYear(2400))

            assertFalse(isLeapYear(1998))
            assertFalse(isLeapYear(2001))
            assertFalse(isLeapYear(1997))
            assertFalse(isLeapYear(1900))
            assertFalse(isLeapYear(1800))
        }
    }

    @Test
    fun testGetDaysInMonth() {
        timeUtil.apply {
            assertEquals(31, getDaysInMonth(1, 2001))
            assertEquals(31, getDaysInMonth(12, 2001))
            assertEquals(31, getDaysInMonth(1, 2000))
            assertEquals(30, getDaysInMonth(6, 2001))
            assertEquals(31, getDaysInMonth(7, 1999))
            assertEquals(31, getDaysInMonth(8, 1999))

            assertEquals(28, getDaysInMonth(2, 1999))
            assertEquals(29, getDaysInMonth(2, 1996))
            assertEquals(29, getDaysInMonth(2, 2000))
            assertEquals(28, getDaysInMonth(2, 2100))

            assertEquals(31, getDaysInMonth(1, 1993))
            assertEquals(28, getDaysInMonth(2, 1993))
            assertEquals(31, getDaysInMonth(3, 1993))
            assertEquals(30, getDaysInMonth(4, 1993))
            assertEquals(31, getDaysInMonth(5, 1993))
            assertEquals(30, getDaysInMonth(6, 1993))
            assertEquals(31, getDaysInMonth(7, 1993))
            assertEquals(31, getDaysInMonth(8, 1993))
            assertEquals(30, getDaysInMonth(9, 1993))
            assertEquals(31, getDaysInMonth(10, 1993))
            assertEquals(30, getDaysInMonth(11, 1993))
            assertEquals(31, getDaysInMonth(12, 1993))

            assertEquals(0, getDaysInMonth(0, 1993))
            assertEquals(0, getDaysInMonth(13, 1993))
        }
    }

}
