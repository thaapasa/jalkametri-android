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
package fi.tuska.jalkametri.data;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.gui.GraphView.Point;
import fi.tuska.jalkametri.util.TimeUtil;

public class DailyDrinkStatisticsImpl implements DailyDrinkStatistics, Point {

    private static final long serialVersionUID = 261921892737912321L;

    /** Time shift to noon. */
    private static final double POINT_OFFSET_MS = 1000 * 60 * 60 * 12d;

    private Date day;
    private double portions;
    private int nDrinks;
    private final TimeUtil timeUtil;

    public DailyDrinkStatisticsImpl(Date day, double portions, int nDrinks, Context context) {
        this.day = day;
        this.portions = portions;
        this.nDrinks = nDrinks;
        this.timeUtil = new TimeUtil(context);
    }

    public DailyDrinkStatisticsImpl(String dateSQLString, double portions, int nDrinks, Context context) {
        this.timeUtil = new TimeUtil(context);
        this.day = timeUtil.fromSQLDate(dateSQLString);
        this.portions = portions;
        this.nDrinks = nDrinks;
    }

    /**
     * The time value of this date is set to 0:00:00.
     */
    @Override
    public Date getDay() {
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
        Calendar cal = timeUtil.getCalendar(day);
        cal.set(Calendar.HOUR_OF_DAY, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    @Override
    public double getValue() {
        return portions;
    }

}
