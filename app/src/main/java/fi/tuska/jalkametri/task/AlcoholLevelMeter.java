/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
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
