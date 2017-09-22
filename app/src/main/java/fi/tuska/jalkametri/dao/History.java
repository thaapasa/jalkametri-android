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
