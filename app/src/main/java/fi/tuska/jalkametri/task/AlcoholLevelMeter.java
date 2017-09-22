package fi.tuska.jalkametri.task;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import fi.tuska.jalkametri.dao.DrinkStatus;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.DrinkStatusCalc;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * Calculates the alcohol level. Loads the required data from the DB.
 *
 * @author Tuukka Haapasalo
 */
public class AlcoholLevelMeter {

    private static final String TAG = "AlcoholLevelMeter";
    private final History history;
    private final Context context;
    private final TimeUtil timeUtil;

    public AlcoholLevelMeter(History history, Context context) {
        this.history = history;
        this.context = context;
        this.timeUtil = new TimeUtil(context);
    }

    public DrinkStatus getDrinkStatus() {
        // Count the status based on two days
        Preferences prefs = new PreferencesImpl(context);
        Date day = timeUtil.getCurrentDrinkingDate(prefs);
        // Start of the time
        Calendar start = timeUtil.getStartOfDay(day, prefs);
        LogUtil.d(TAG, "Current date is %s; started at %s", day, start.getTime());
        Calendar end = timeUtil.getCalendar(start);
        end.add(Calendar.DAY_OF_MONTH, 1);
        start.add(Calendar.DAY_OF_MONTH, -1);

        DrinkStatus status = new DrinkStatusCalc(history, context, start.getTime(), end.getTime());
        return status;
    }

}
