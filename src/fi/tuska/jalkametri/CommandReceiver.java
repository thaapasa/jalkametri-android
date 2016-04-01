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
package fi.tuska.jalkametri;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import fi.tuska.jalkametri.dao.DrinkStatus;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.task.AlcoholLevelMeter;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * Receives commands from the jAlkaMetri widget: used to drink a favourite
 * drink by clicking the icon shown on the widget.
 * 
 * @author Tuukka Haapasalo
 */
public class CommandReceiver extends BroadcastReceiver {

    public static final String ACTION_DRINK = "fi.tuska.jalkametri.ACTION_DRINK";

    private static final String KEY_DRINK = "drink";


    /*
     * Standard receiver functions
     * --------------------------------------------
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.startsWith(ACTION_DRINK)) {
            // Drink a drink
            DrinkEvent event = (DrinkEvent) intent.getExtras().get(KEY_DRINK);
            if (event != null) {
                consumeDrink(context, event);
            }
        }
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    public void consumeDrink(Context context, DrinkEvent event) {
        DBAdapter adapter = new DBAdapter(context);
        History history = new HistoryDB(adapter, context);

        // Count original alcohol level
        AlcoholLevelMeter meter = new AlcoholLevelMeter(history, context);
        DrinkStatus status = meter.getDrinkStatus();
        double orgLevel = status.getAlcoholLevel();

        // Update event
        event.setTime(new TimeUtil(context).getCurrentTime());
        // Store event
        history.createDrink(event);
        // Recalculate widgets
        JalkametriWidget.triggerRecalculate(context, adapter);

        // Make toast
        DrinkActivities.makeDrinkToast(context, orgLevel, true);
        adapter.close();
    }

    public static PendingIntent createDrinkIntent(DrinkEvent drink, Context context, int index) {
        Intent intent = new Intent(index > 0 ? ACTION_DRINK + "_" + index : ACTION_DRINK);
        intent.putExtra(KEY_DRINK, drink);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

}
