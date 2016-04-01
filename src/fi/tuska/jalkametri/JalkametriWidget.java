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

import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import fi.tuska.jalkametri.dao.DrinkStatus;
import fi.tuska.jalkametri.dao.Favourites;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.FavouritesDB;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.gui.DrinkIconUtils;
import fi.tuska.jalkametri.task.AlcoholLevelMeter;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * A widget that shows the current blood alcohol content and the first three
 * of your favorite drinks. Clicking on a favorite drink marks that drink with
 * the current time.
 * 
 * @author Tuukka Haapasalo
 */
public class JalkametriWidget extends AppWidgetProvider {

    private static final String TAG = "JalkametriWidget";
    public static final String FORCE_UPDATE = "fi.tuska.jalkametri.JalkametriWidget.FORCE_UPDATE";

    /*
     * Interface for the other jAlkaMetri components to use
     * --------------------------------------------
     */

    /**
     * Triggers an in-program recalculation of the widget.
     */
    public static void triggerRecalculate(Context context, DBAdapter adapter) {
        // parent.sendBroadcast(new Intent(FORCE_RECALCULATE));
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        recalculateWidgetUI(views, context, adapter);
        updateWidgets(views, context);

        // Check that the update alarm is in place
        createUpdateAlarm(context);
    }

    /*
     * Standard widget functions --------------------------------------------
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        createUpdateAlarm(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (FORCE_UPDATE.equals(intent.getAction())) {
            DBAdapter adapter = new DBAdapter(context);
            updateWidgetStatus(context, adapter);
            adapter.close();
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // Update the UI

        DBAdapter adapter = new DBAdapter(context);
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            // Create a RemoveView
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            // Update the UI.
            recalculateWidgetUI(views, context, adapter);

            // Notify the AppWidgetManager to update the widget using
            // the modified remoteview.
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        adapter.close();
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    protected static void updateWidgetStatus(Context context, DBAdapter adapter) {
        LogUtil.d(TAG, "Updating widget status");
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        recalculateStatus(views, context, adapter);
        updateWidgets(views, context);
    }

    /**
     * Call to make the manager actually update the widgets.
     */
    protected static void updateWidgets(RemoteViews views, Context context) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, JalkametriWidget.class);
        int[] ids = mgr.getAppWidgetIds(name);
        for (int id : ids) {
            mgr.updateAppWidget(id, views);
        }
    }

    protected static PendingIntent getJalkametriLaunch(Context context) {
        // Set jAlkaMetri launch
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
        return pIntent;
    }

    public static void recalculateWidgetUI(RemoteViews views, Context context, DBAdapter adapter) {
        LogUtil.d(TAG, "Recalculating jAlkaMetri widget UI");
        setFavouriteIcons(views, context, adapter);
        {
            // Set jAlkaMetri launch
            PendingIntent pIntent = getJalkametriLaunch(context);
            views.setOnClickPendingIntent(R.id.status, pIntent);
        }
        recalculateStatus(views, context, adapter);
    }

    protected static void recalculateStatus(RemoteViews views, Context context, DBAdapter adapter) {
        History history = new HistoryDB(adapter, context);
        AlcoholLevelMeter meter = new AlcoholLevelMeter(history, context);
        DrinkStatus status = meter.getDrinkStatus();
        double level = status.getAlcoholLevel();
        views.setTextViewText(R.id.status, String.format("%.2f", level));
        LogUtil.d(TAG, "Setting alcohol status to %f", level);
    }

    protected static void setFavouriteIcons(RemoteViews views, Context context, DBAdapter adapter) {
        Favourites favourites = new FavouritesDB(adapter, context);
        List<DrinkEvent> favs = favourites.getFavourites(3);
        setIconsAndIntents(R.id.icon1, context, views, favs, 0);
        setIconsAndIntents(R.id.icon2, context, views, favs, 1);
        setIconsAndIntents(R.id.icon3, context, views, favs, 2);
    }

    private static void setIconsAndIntents(int iconRes, Context context, RemoteViews views,
        List<DrinkEvent> favs, int index) {
        int iconId = R.drawable.drink_none;
        if (favs.size() > index) {
            DrinkEvent drink = favs.get(index);
            int newIconId = DrinkIconUtils.getDrinkIconRes(drink.getIcon());
            if (newIconId != 0)
                iconId = newIconId;

            PendingIntent pIntent = CommandReceiver.createDrinkIntent(drink, context, index + 1);
            views.setOnClickPendingIntent(iconRes, pIntent);
        } else {
            PendingIntent pIntent = getJalkametriLaunch(context);
            views.setOnClickPendingIntent(iconRes, pIntent);
        }
        views.setImageViewResource(iconRes, iconId);
    }

    /**
     * Sets up an alarm that will update the widget periodically.
     */
    protected static void createUpdateAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context
            .getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(FORCE_UPDATE);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 1000,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES / 15, pIntent);
    }
}
