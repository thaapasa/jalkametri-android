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
import java.util.List;

/**
 * Interface for accessing the statistics.
 * 
 * @author Tuukka Haapasalo
 */
public interface Statistics {

    /**
     * Calculates and returns general statistics information.
     */
    GeneralStatistics getGeneralStatistics();
    GeneralStatistics getGeneralStatistics(Date start, Date end);

    /**
     * @return the number of drinks consumed for each day in the given period.
     */
    List<DailyDrinkStatistics> getDailyDrinkAmounts(Date start, Date end);

    Date getFirstDrinkEventTime();

}
