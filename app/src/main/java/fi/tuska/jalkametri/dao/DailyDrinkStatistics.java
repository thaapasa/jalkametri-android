package fi.tuska.jalkametri.dao;

import fi.tuska.jalkametri.gui.GraphView.Point;
import org.joda.time.LocalDate;

public interface DailyDrinkStatistics extends Point {

    LocalDate getDay();

    double getPortions();

    int getNumberOfDrinks();

}
