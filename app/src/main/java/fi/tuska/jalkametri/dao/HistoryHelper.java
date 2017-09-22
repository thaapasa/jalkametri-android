package fi.tuska.jalkametri.dao;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.TimeUtil;

public class HistoryHelper {

    /**
     * @return the portions consumed during the week given by the parameter
     * day.
     */
    public static double countWeekPortions(History history, Date day, Context context) {
        Preferences prefs = new PreferencesImpl(context);
        TimeUtil timeUtil = new TimeUtil(context);

        Calendar weekStart = timeUtil.getStartOfWeek(day, prefs);
        Calendar weekEnd = timeUtil.getCalendar(weekStart);
        weekEnd.add(Calendar.DATE, 7);
        return history.countPortions(weekStart.getTime(), weekEnd.getTime());
    }

    public static double countDayPortions(History history, Date day, Context context) {
        Preferences prefs = new PreferencesImpl(context);
        TimeUtil timeUtil = new TimeUtil(context);

        Calendar dayStart = timeUtil.getStartOfDay(day, prefs);
        Calendar dayEnd = timeUtil.getCalendar(dayStart);
        dayEnd.add(Calendar.DATE, 1);
        return history.countPortions(dayStart.getTime(), dayEnd.getTime());
    }

}
