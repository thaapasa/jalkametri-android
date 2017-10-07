package fi.tuska.jalkametri.db;

import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.DrinkSupport;
import fi.tuska.jalkametri.test.JalkametriDBTestCase;
import org.joda.time.DateTime;

import java.util.List;

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
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 14, 1, 30,
                0).toInstant()));
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 13, 19, 0,
                0).toInstant()));
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 13, 18, 0,
                0).toInstant()));
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 14, 10, 0,
                0).toInstant()));
    }

    public void testHistoryAdd() {
        history.clearAll();
        List<DrinkEvent> drinks = history.getPreviousDrinks(10);
        assertNotNull(drinks);
        assertTrue(drinks.isEmpty());

        // Sanity check
        assertEquals(6, getPrefs().getDayChangeHour());
        assertEquals(0, getPrefs().getDayChangeMinute());

        createSomeDrinks();

        drinks = history.getDrinks(getTime(2011, 5, 13, 0, 0, 0).toLocalDate(), true);
        assertEquals(3, drinks.size());
        assertSameTime(getTime(2011, 5, 13, 18, 0, 0).toInstant(), drinks.get(0).getTime());
        assertSameTime(getTime(2011, 5, 13, 19, 0, 0).toInstant(), drinks.get(1).getTime());
        assertSameTime(getTime(2011, 5, 14, 1, 30, 0).toInstant(), drinks.get(2).getTime());

        drinks = history.getDrinks(getTime(2011, 5, 13, 0, 0, 0).toLocalDate(), false);
        assertEquals(3, drinks.size());
        assertSameTime(getTime(2011, 5, 14, 1, 30, 0).toInstant(), drinks.get(0).getTime());
        assertSameTime(getTime(2011, 5, 13, 19, 0, 0).toInstant(), drinks.get(1).getTime());
        assertSameTime(getTime(2011, 5, 13, 18, 0, 0).toInstant(), drinks.get(2).getTime());

        try {
            setDayChangeTime(0, 0);

            drinks = history.getDrinks(getTime(2011, 5, 13, 0, 0, 0).toLocalDate(), true);
            assertEquals(2, drinks.size());
            assertSameTime(getTime(2011, 5, 13, 18, 0, 0).toInstant(), drinks.get(0).getTime());
            assertSameTime(getTime(2011, 5, 13, 19, 0, 0).toInstant(), drinks.get(1).getTime());
        } finally {
            setDayChangeTime(6, 0);
        }
    }

    public void testAddDrinkForSelectedDay() {
        history.clearAll();
        createSomeDrinks();

        DateTime day = getTime(2011, 5, 13, 0, 0, 0);
        List<DrinkEvent> drinks = history.getDrinks(day.toLocalDate(), true);
        assertEquals(3, drinks.size());

        // Test: Add drink @ 16.5. to selected day 13.5.; time is during
        // normal hours
        DrinkSelection beer = DrinkSupport.getBeerSelection(getTime(2011, 5, 16, 15, 0,
                0).toInstant());
        DrinkActions.addDrinkForSelectedDay(history, beer, day.toLocalDate(), dummyParentActivity);

        // Check that drink has been added and has correct time (will be the
        // first drink on this day)
        drinks = history.getDrinks(day.toLocalDate(), true);
        assertEquals(4, drinks.size());
        assertSameTime(getTime(2011, 5, 13, 15, 0, 0).toInstant(), drinks.get(0).getTime());

        // Test: Add drink @ 16.5. to selected day 13.5.; time is during
        // morning hours
        beer = DrinkSupport.getBeerSelection(getTime(2011, 5, 16, 2, 0, 0).toInstant());
        DrinkActions.addDrinkForSelectedDay(history, beer, day.toLocalDate(), dummyParentActivity);

        // Check that drink has been added and has correct time (will be the
        // last drink on this day)
        drinks = history.getDrinks(day.toLocalDate(), true);
        assertEquals(5, drinks.size());
        // The drinks should have been placed on the next day's morning
        assertSameTime(getTime(2011, 5, 14, 2, 0, 0).toInstant(), drinks.get(4).getTime());
    }

}
