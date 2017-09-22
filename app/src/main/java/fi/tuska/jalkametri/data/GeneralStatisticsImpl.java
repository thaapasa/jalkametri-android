package fi.tuska.jalkametri.data;

import java.util.Date;

import android.content.Context;

import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.GeneralStatistics;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.util.FAQCalendar;
import fi.tuska.jalkametri.util.TimeUtil;

public class GeneralStatisticsImpl implements GeneralStatistics {

    private long totalDrinks = 0;
    private double totalPortions = 0;
    private long allDays = 0;
    private long drunkDays = 0;
    private Date lastDay;
    private Date firstDay;

    public GeneralStatisticsImpl(Date start, Date end, Date firstRecordedDay, Context context) {
        TimeUtil timeUtil = new TimeUtil(context);
        Date cur = timeUtil.getCurrentTime();
        firstDay = start != null ? start : new Date(0);
        lastDay = end != null ? end : cur;
        if (firstRecordedDay.getTime() > firstDay.getTime())
            firstDay = firstRecordedDay;
        if (lastDay.getTime() > cur.getTime())
            lastDay = cur;

        FAQCalendar fcal = new FAQCalendar(firstDay, context);
        allDays = fcal.diffDayPeriods(timeUtil.getCalendar(lastDay)) + 1;
    }

    public void setTotalDrinks(long count) {
        totalDrinks = count;
    }

    public void setTotalPortions(double count) {
        totalPortions = count;
    }

    public void setDrunkDays(long days) {
        drunkDays = days;
    }

    @Override
    public long getTotalDrinks() {
        return totalDrinks;
    }

    @Override
    public double getTotalPortions() {
        return totalPortions;
    }

    @Override
    public double getTotalPortionsAsPureAlcoholLiters(Context context) {
        Preferences prefs = new PreferencesImpl(context);
        return totalPortions * prefs.getStandardDrinkAlcoholWeight() / Common.ALCOHOL_LITER_WEIGHT;
    }

    @Override
    public Date getFirstDay() {
        return firstDay;
    }

    @Override
    public long getNumberOfRecordedDays() {
        return Math.max(allDays, drunkDays);
    }

    @Override
    public long getNumberOfSoberDays() {
        return getNumberOfRecordedDays() - drunkDays;
    }

    @Override
    public long getNumberOfDrunkDays() {
        return drunkDays;
    }

    @Override
    public double getAvgPortionsAllDays() {
        return getNumberOfRecordedDays() > 0 ? totalPortions / getNumberOfRecordedDays() : 0;
    }

    @Override
    public double getAvgPortionsDrunkDays() {
        return drunkDays > 0 ? totalPortions / drunkDays : 0;
    }

    @Override
    public double getSoberDayPercentage() {
        return getNumberOfRecordedDays() > 0 ? (double) getNumberOfSoberDays() * 100d
            / getNumberOfRecordedDays() : 0;
    }

    @Override
    public double getDrunkDayPercentage() {
        return getNumberOfRecordedDays() > 0 ? (double) drunkDays * 100d
            / getNumberOfRecordedDays() : 0;
    }

    @Override
    public double getAvgWeeklyPortions() {
        double weeks = allDays / 7d;
        return totalPortions / weeks;
    }

}
