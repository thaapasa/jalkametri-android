package fi.tuska.jalkametri.dao;

import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.List;

public interface Statistics {

    GeneralStatistics getGeneralStatistics();

    GeneralStatistics getGeneralStatistics(LocalDate start, LocalDate end);

    List<DailyDrinkStatistics> getDailyDrinkAmounts(LocalDate start, LocalDate end);

    Instant getFirstDrinkEventTime();

}
