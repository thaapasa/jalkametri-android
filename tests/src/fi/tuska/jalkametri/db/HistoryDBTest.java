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
package fi.tuska.jalkametri.db;

import java.util.Date;
import java.util.List;

import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.DrinkSupport;
import fi.tuska.jalkametri.test.JalkametriDBTestCase;

public class HistoryDBTest extends JalkametriDBTestCase {

    private History history;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        history = new HistoryDB(adapter, getContext());
        setDayChangeTime(6, 0);
    }

    private void createSomeDrinks() {
        Drink beer = DrinkSupport.getBeer();
        DrinkSize pint = DrinkSupport.getPint();
        history.createDrink(new DrinkSelection(beer, pint, timeUtil.getTime(2011, 5, 14, 01, 30,
            0)));
        history.createDrink(new DrinkSelection(beer, pint, timeUtil
            .getTime(2011, 5, 13, 19, 0, 0)));
        history.createDrink(new DrinkSelection(beer, pint, timeUtil
            .getTime(2011, 5, 13, 18, 0, 0)));
        history.createDrink(new DrinkSelection(beer, pint, timeUtil
            .getTime(2011, 5, 14, 10, 0, 0)));
    }

    public void testHistoryAdd() {
        history.clearAll();
        List<DrinkEvent> drinks = history.getPreviousDrinks(10);
        assertNotNull(drinks);
        assertTrue(drinks.isEmpty());

        // Sanity check
        assertEquals(6, prefs.getDayChangeHour());
        assertEquals(0, prefs.getDayChangeMinute());

        createSomeDrinks();

        drinks = history.getDrinks(timeUtil.getTime(2011, 5, 13, 0, 0, 0), true);
        assertEquals(3, drinks.size());
        assertSameTime(timeUtil.getTime(2011, 5, 13, 18, 0, 0), drinks.get(0).getTime());
        assertSameTime(timeUtil.getTime(2011, 5, 13, 19, 0, 0), drinks.get(1).getTime());
        assertSameTime(timeUtil.getTime(2011, 5, 14, 1, 30, 0), drinks.get(2).getTime());

        drinks = history.getDrinks(timeUtil.getTime(2011, 5, 13, 0, 0, 0), false);
        assertEquals(3, drinks.size());
        assertSameTime(timeUtil.getTime(2011, 5, 14, 1, 30, 0), drinks.get(0).getTime());
        assertSameTime(timeUtil.getTime(2011, 5, 13, 19, 0, 0), drinks.get(1).getTime());
        assertSameTime(timeUtil.getTime(2011, 5, 13, 18, 0, 0), drinks.get(2).getTime());

        try {
            setDayChangeTime(0, 0);

            drinks = history.getDrinks(timeUtil.getTime(2011, 5, 13, 0, 0, 0), true);
            assertEquals(2, drinks.size());
            assertSameTime(timeUtil.getTime(2011, 5, 13, 18, 0, 0), drinks.get(0).getTime());
            assertSameTime(timeUtil.getTime(2011, 5, 13, 19, 0, 0), drinks.get(1).getTime());
        } finally {
            setDayChangeTime(6, 0);
        }
    }

    public void testAddDrinkForSelectedDay() {
        history.clearAll();
        createSomeDrinks();

        Date day = timeUtil.getTime(2011, 5, 13, 0, 0, 0);
        List<DrinkEvent> drinks = history.getDrinks(day, true);
        assertEquals(3, drinks.size());

        // Test: Add drink @ 16.5. to selected day 13.5.; time is during
        // normal hours
        DrinkSelection beer = DrinkSupport.getBeerSelection(timeUtil.getTime(2011, 5, 16, 15, 0,
            0));
        DrinkActions.addDrinkForSelectedDay(history, beer, day, dummyParentActivity);

        // Check that drink has been added and has correct time (will be the
        // first drink on this day)
        drinks = history.getDrinks(day, true);
        assertEquals(4, drinks.size());
        assertSameTime(timeUtil.getTime(2011, 5, 13, 15, 0, 0), drinks.get(0).getTime());

        // Test: Add drink @ 16.5. to selected day 13.5.; time is during
        // morning hours
        beer = DrinkSupport.getBeerSelection(timeUtil.getTime(2011, 5, 16, 2, 0, 0));
        DrinkActions.addDrinkForSelectedDay(history, beer, day, dummyParentActivity);

        // Check that drink has been added and has correct time (will be the
        // last drink on this day)
        drinks = history.getDrinks(day, true);
        assertEquals(5, drinks.size());
        // The drinks should have been placed on the next day's morning
        assertSameTime(timeUtil.getTime(2011, 5, 14, 2, 0, 0), drinks.get(4).getTime());
    }

}
