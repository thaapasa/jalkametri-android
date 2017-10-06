package fi.tuska.jalkametri.dao;

import android.content.Context;
import org.joda.time.LocalDate;

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
    LocalDate getFirstDay();

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
