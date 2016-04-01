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
package fi.tuska.jalkametri.dao;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.TimeUtil;

public class HistoryHelper {

    /**
     * @return the portions consumed during the week given by the parameter
     * day.
     */
    public static double countWeekPortions(History history, Date day, Context context) {
        Preferences prefs = new PreferencesImpl(context);
        TimeUtil timeUtil = new TimeUtil(context);

        Calendar weekStart = timeUtil.getStartOfWeek(day, prefs);
        Calendar weekEnd = timeUtil.getCalendar(weekStart);
        weekEnd.add(Calendar.DATE, 7);
        return history.countPortions(weekStart.getTime(), weekEnd.getTime());
    }

    public static double countDayPortions(History history, Date day, Context context) {
        Preferences prefs = new PreferencesImpl(context);
        TimeUtil timeUtil = new TimeUtil(context);

        Calendar dayStart = timeUtil.getStartOfDay(day, prefs);
        Calendar dayEnd = timeUtil.getCalendar(dayStart);
        dayEnd.add(Calendar.DATE, 1);
        return history.countPortions(dayStart.getTime(), dayEnd.getTime());
    }

}
