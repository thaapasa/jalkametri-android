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

import java.util.Date;

import android.content.Context;

public interface GeneralStatistics {

    /**
     * @return the total number of drinks consumed (number of recorded drink
     * events)
     */
    long getTotalDrinks();

    /**
     * @return the total number of portions consumed
     */
    double getTotalPortions();

    /**
     * @return the number of portions drunk as pure alcohol, in liters
     */
    double getTotalPortionsAsPureAlcoholLiters(Context context);

    /**
     * @return the date of the first recorded drinking event
     */
    Date getFirstDay();

    /**
     * @return the number of days from the first recorded drinking event
     */
    long getNumberOfRecordedDays();

    /**
     * @return the number of sober days from the first recorded event
     */
    long getNumberOfSoberDays();

    /**
     * @return the ratio of sober days / all days; in percents
     */
    double getSoberDayPercentage();

    /**
     * @return the number of days when drunk from the first recorded event
     */
    long getNumberOfDrunkDays();

    /**
     * @return the ratio of drunk days / all days; in percents
     */
    double getDrunkDayPercentage();

    /**
     * @return the average number of portions per all days
     */
    double getAvgPortionsAllDays();

    /**
     * @return the average number of portions per days when drunk
     */
    double getAvgPortionsDrunkDays();
    
    /**
     * @return the average number of portions for each week
     */
    double getAvgWeeklyPortions();

}
