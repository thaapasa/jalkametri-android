package fi.tuska.jalkametri.dao;

import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.Date;
import java.util.List;

public interface History {

    void createDrink(DrinkSelection selection);

    DrinkEvent getDrink(long index);

    void updateEvent(long index, DrinkEvent event);

    boolean deleteEvent(long index);

    List<DrinkEvent> getDrinks(LocalDate day, boolean ascending);

    List<DrinkEvent> getDrinks(Instant fromTime, Instant toTime, boolean ascending);

    List<DrinkEvent> getPreviousDrinks(int limit);

    void clearDay(LocalDate day);

    void clearDrinks(Instant fromTime, Instant toTime);

    void clearAll();

    double countTotalPortions();

    double countPortions(Instant fromTime, Instant endTime);

    void recalculatePortions();

}
