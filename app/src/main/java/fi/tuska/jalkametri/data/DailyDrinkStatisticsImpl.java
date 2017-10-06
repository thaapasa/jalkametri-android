package fi.tuska.jalkametri.data;

import android.content.Context;
import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.gui.GraphView.Point;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class DailyDrinkStatisticsImpl implements DailyDrinkStatistics, Point {

    private LocalDate day;
    private double portions;
    private int nDrinks;
    private final TimeUtil timeUtil;
    private static final LocalTime positionTime = new LocalTime(12, 0, 0);

    public DailyDrinkStatisticsImpl(LocalDate day, double portions, int nDrinks, Context context) {
        this.day = day;
        this.portions = portions;
        this.nDrinks = nDrinks;
        this.timeUtil = new TimeUtil(context);
    }

    public DailyDrinkStatisticsImpl(String dateSQLString, double portions, int nDrinks, Context context) {
        this.timeUtil = new TimeUtil(context);
        this.day = timeUtil.fromSQLDate(dateSQLString).toDateTime(timeUtil.getTimeZone()).toLocalDate();
        this.portions = portions;
        this.nDrinks = nDrinks;
    }

    @Override
    public LocalDate getDay() {
        return day;
    }

    @Override
    public double getPortions() {
        return portions;
    }

    @Override
    public int getNumberOfDrinks() {
        return nDrinks;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f (%d)", day, portions, nDrinks);
    }

    @Override
    public double getPosition() {
        return day.toDateTime(positionTime, timeUtil.getTimeZone()).getMillis();
    }

    @Override
    public double getValue() {
        return portions;
    }

}
