package fi.tuska.jalkametri.dao;

import android.content.Context;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import static org.joda.time.Duration.standardDays;

public class HistoryHelper {

    /**
     * @return the portions consumed during the week given by the parameter
     * day.
     */
    public static double countWeekPortions(History history, LocalDate day, Context context) {
        Preferences prefs = new PreferencesImpl(context);
        TimeUtil timeUtil = new TimeUtil(context);

        Instant weekStart = timeUtil.getStartOfDrinkDay(timeUtil.getStartOfWeek(day, prefs), prefs);
        Instant weekEnd = weekStart.plus(standardDays(7));
        return history.countPortions(weekStart, weekEnd);
    }

    public static double countDayPortions(History history, LocalDate day, Context context) {
        Preferences prefs = new PreferencesImpl(context);
        TimeUtil timeUtil = new TimeUtil(context);

        Instant dayStart = timeUtil.getStartOfDrinkDay(day, prefs);
        Instant dayEnd = dayStart.plus(standardDays(1));
        return history.countPortions(dayStart, dayEnd);
    }

}
