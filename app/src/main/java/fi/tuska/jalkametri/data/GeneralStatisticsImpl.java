package fi.tuska.jalkametri.data;

import android.content.Context;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.GeneralStatistics;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.Days;
import org.joda.time.LocalDate;

public class GeneralStatisticsImpl implements GeneralStatistics {

    private static final LocalDate MIN_START = new LocalDate(2000, 1, 1);

    private long totalDrinks = 0;
    private double totalPortions = 0;
    private long allDays = 0;
    private long drunkDays = 0;
    private LocalDate lastDay;
    private LocalDate firstDay;

    public GeneralStatisticsImpl(LocalDate start, LocalDate end, LocalDate firstRecordedDay, Context context) {
        TimeUtil timeUtil = new TimeUtil(context);
        LocalDate cur = new LocalDate(timeUtil.getTimeZone());
        firstDay = start != null ? start : MIN_START;
        lastDay = end != null ? end : cur;
        if (firstRecordedDay.isAfter(firstDay))
            firstDay = firstRecordedDay;
        if (lastDay.isAfter(cur))
            lastDay = cur;

        allDays = Days.daysBetween(firstDay, lastDay).getDays() + 1;
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
    public LocalDate getFirstDay() {
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
