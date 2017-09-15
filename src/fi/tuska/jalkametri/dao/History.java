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

import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;

public interface History {

    /**
     * C: create
     */
    void createDrink(DrinkSelection selection);

    /**
     * R: read
     */
    DrinkEvent getDrink(long index);

    /**
     * U: update
     */
    void updateEvent(long index, DrinkEvent event);

    /**
     * D: delete
     */
    boolean deleteEvent(long index);

    List<DrinkEvent> getDrinks(Date day, boolean ascending);

    List<DrinkEvent> getDrinks(Date fromTime, Date toTime, boolean ascending);

    List<DrinkEvent> getPreviousDrinks(int limit);

    void clearDay(Date day);

    void clearDrinks(Date fromTime, Date toTime);

    void clearAll();
    
    double countTotalPortions();

    double countPortions(Date fromTime, Date endTime);
    
    void recalculatePortions();
    
}
