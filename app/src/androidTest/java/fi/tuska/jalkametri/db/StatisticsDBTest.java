package fi.tuska.jalkametri.db;

import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.Statistics;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.DrinkSupport;
import fi.tuska.jalkametri.test.JalkametriDBTestCase;
import org.joda.time.LocalDate;

import java.util.List;

public class StatisticsDBTest extends JalkametriDBTestCase {

    private History history;
    private Statistics statistics;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        history = new HistoryDB(db, getContext());
        statistics = new StatisticsDB(db, getPrefs(), getContext());
        setDayChangeTime(6, 0);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        setDayChangeTime(6, 0);
    }

    public void testTimeGroupColumnSpec() {
        // Create an overridden instance of StatisticsDB so that we are able
        // to access its protected methods
        new StatisticsDB(db, getPrefs(), getContext()) {
            public void checkFormat() {
                setDayChangeTime(5, 8);
                assertEquals("DISTINCT(TRIM(SUBSTR(DATETIME(time, '-05:08'), 0, 11))) AS thedate",
                        getTimeGroupColumnSpec());
                setDayChangeTime(6, 0);
                assertEquals("DISTINCT(TRIM(SUBSTR(DATETIME(time, '-06:00'), 0, 11))) AS thedate",
                        getTimeGroupColumnSpec());
                setDayChangeTime(10, 15);
                assertEquals("DISTINCT(TRIM(SUBSTR(DATETIME(time, '-10:15'), 0, 11))) AS thedate",
                        getTimeGroupColumnSpec());
            }
        }.checkFormat();
    }

    public void testGetDailyDrinkAmounts() {
        HistoryDB history = new HistoryDB(db, getContext());

        history.clearAll();
        createSomeDrinks();

        setDayChangeTime(0, 0);

        List<DailyDrinkStatistics> amounts = statistics.getDailyDrinkAmounts(
                getTime(2011, 5, 13, 15, 0, 0).toLocalDate(),
                getTime(2011, 5, 13, 13, 0, 0).toLocalDate());
        assertNotNull(amounts);
        assertEquals(1, amounts.size());
        DailyDrinkStatistics st = amounts.get(0);
        assertSameTime(getTime(2011, 5, 13, 0, 0, 0).toLocalDate(), st.getDay());
        assertEquals(2, st.getNumberOfDrinks());
        assertCloseEnough(3.4402, st.getPortions());

        // Include the other day

        amounts = statistics.getDailyDrinkAmounts(getTime(2011, 4, 20, 15, 0, 0).toLocalDate(),
                getTime(2011, 5, 15, 13, 0, 0).toLocalDate());
        assertNotNull(amounts);
        assertEquals(2, amounts.size());
        // Check 13.5.
        st = amounts.get(0);
        assertSameTime(getTime(2011, 5, 13, 0, 0, 0).toLocalDate(), st.getDay());
        assertEquals(2, st.getNumberOfDrinks());
        assertCloseEnough(3.4402, st.getPortions());
        // Check 14.5.
        st = amounts.get(1);
        assertSameTime(getTime(2011, 5, 14, 0, 0, 0).toLocalDate(), st.getDay());
        assertEquals(2, st.getNumberOfDrinks());
        assertCloseEnough(3.4402, st.getPortions());

        setDayChangeTime(6, 0);
        // One of the drinks of 14.5. should now be listed as a drink of 13.5.

        amounts = statistics.getDailyDrinkAmounts(new LocalDate(2011, 5, 13),
                new LocalDate(2011, 5, 13));
        assertNotNull(amounts);
        assertEquals(1, amounts.size());
        st = amounts.get(0);
        assertSameTime(new LocalDate(2011, 5, 13), st.getDay());
        assertEquals(3, st.getNumberOfDrinks());
        assertCloseEnough(5.1603, st.getPortions());

    }

    private void createSomeDrinks() {
        Drink beer = DrinkSupport.getBeer();
        DrinkSize pint = DrinkSupport.getPint();
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 14, 01, 30,
                0).toInstant()));
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 13, 19, 0,
                0).toInstant()));
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 13, 18, 0,
                0).toInstant()));
        history.createDrink(new DrinkSelection(beer, pint, getTime(2011, 5, 14, 10, 0,
                0).toInstant()));

    }

}
