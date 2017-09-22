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

    private static final String ACTION_DRINK = "fi.tuska.jalkametri.ACTION_DRINK";

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
    private void consumeDrink(Context context, DrinkEvent event) {
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

    static PendingIntent createDrinkIntent(DrinkEvent drink, Context context, int index) {
        Intent intent = new Intent(index > 0 ? ACTION_DRINK + "_" + index : ACTION_DRINK);
        intent.putExtra(KEY_DRINK, drink);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

}
