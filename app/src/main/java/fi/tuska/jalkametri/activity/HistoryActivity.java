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
package fi.tuska.jalkametri.activity;

import static fi.tuska.jalkametri.Common.KEY_ORIGINAL;
import static fi.tuska.jalkametri.Common.KEY_RESULT;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.DBActivity;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.HistoryHelper;
import fi.tuska.jalkametri.dao.NamedIcon;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.gui.Confirmation;
import fi.tuska.jalkametri.gui.DrinkDetailsDialog;
import fi.tuska.jalkametri.gui.NamedIconAdapter;
import fi.tuska.jalkametri.util.Converter;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.StringUtil;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * An activity that shows the user's drinking history, day by day.
 *
 * @author Tuukka Haapasalo
 */
public class HistoryActivity extends ListActivity implements GUIActivity, DBActivity {

    private static final String TAG = "HistoryActivity";

    private static final String KEY_SHOW_DAY = "day";
    private static final String KEY_SELECTED_EVENT = "selectedEvent";

    private static final String PORTIONS_FORMAT = "%.1f / %.1f";

    private DateFormat wdayFormat;

    private DBAdapter adapter;
    private Date day;
    private TextView dateText;
    private TextView weekText;
    private TextView portionsText;
    private NamedIconAdapter<DrinkEvent> listAdapter;
    private History history;
    private Preferences prefs;
    private DrinkSelection selectedEvent;
    private String weekPrefix;
    private DateFormat timeFormat;
    private TimeUtil timeUtil;

    private String iconNameUnitStr;
    private final Converter<NamedIcon, String> iconNameConverter = new Converter<NamedIcon, String>() {
        @Override
        public String convert(NamedIcon src) {
            DrinkEvent drink = (DrinkEvent) src;
            return String.format("%s: %s\n%.1f %s", timeFormat.format(drink.getTime()),
                drink.getName(), drink.getPortions(getContext()), iconNameUnitStr);
        }
    };

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            loadDay(timeUtil.getCalendarFromDatePicker(year, monthOfYear, dayOfMonth).getTime());
        }
    };

    public HistoryActivity() {
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /**
     * Call to prepare an intent for showing the selected category.
     */
    public static void prepareForDay(Intent intent, Date showDay) {
        intent.putExtra(KEY_SHOW_DAY, showDay);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        this.timeUtil = new TimeUtil(getContext());

        Resources res = getResources();
        // Update title to enforce correct language
        setTitle(res.getString(R.string.title_history));

        this.adapter = new DBAdapter(this);
        this.history = new HistoryDB(adapter, this);
        this.prefs = new PreferencesImpl(this);

        this.portionsText = (TextView) findViewById(R.id.portions);
        assert portionsText != null;
        setPortions(0, 0);
        this.dateText = (TextView) findViewById(R.id.browser_title);
        assert dateText != null;
        this.weekText = (TextView) findViewById(R.id.browser_subtitle);
        assert weekText != null;

        weekPrefix = res.getString(R.string.history_week_prefix) + " ";

        this.iconNameUnitStr = res.getString(R.string.history_list_units);
        this.wdayFormat = timeUtil.getDateFormatWDay();
        this.timeFormat = timeUtil.getTimeFormat();
        Bundle extras = getIntent().getExtras();
        Date showDay = (Date) extras.get(KEY_SHOW_DAY);
        initComponents();
        loadDay(showDay);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SHOW_DAY, day);
        outState.putSerializable(KEY_SELECTED_EVENT, selectedEvent);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Date date = (Date) state.get(KEY_SHOW_DAY);
        if (date != null)
            loadDay(date);
        selectedEvent = (DrinkEvent) state.get(KEY_SELECTED_EVENT);
    }

    @Override
    protected void onPause() {
        adapter.close();
        super.onPause();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DrinkEvent event = listAdapter.getItem(position);
        if (event != null) {
            showEventDetails(event);
            return;
        }
        super.onListItemClick(l, v, position, id);
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    @Override
    public void updateUI() {
        List<DrinkEvent> drinks = history.getDrinks(day, false);
        LogUtil.d(TAG, "Loaded %d drinks for %s", drinks.size(), wdayFormat.format(day));
        listAdapter = new NamedIconAdapter<DrinkEvent>(this, drinks, false, iconNameConverter,
            Common.DEFAULT_ICON_RES);
        setListAdapter(listAdapter);
        setPortions(countPortions(drinks), HistoryHelper.countWeekPortions(history, day, this));
    }

    private void initComponents() {
        registerForContextMenu(getListView());
    }

    protected double countPortions(List<DrinkEvent> drinks) {
        double portions = 0;
        for (DrinkEvent event : drinks) {
            portions += event.getPortions(getContext());
        }
        return portions;
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    protected void showEventDetails(DrinkEvent event) {
        this.selectedEvent = event;
        showDialog(Common.DIALOG_SHOW_DRINK_DETAILS);
    }

    private void loadDay(Date date) {
        this.day = date;
        dateText.setText(StringUtil.uppercaseFirstLetter(wdayFormat.format(date)));
        weekText.setText(weekPrefix + String.valueOf(timeUtil.getWeekNumber(day, prefs)));
        LogUtil.d(TAG, "Selected day: %s", day);
        updateUI();
    }

    private void setPortions(double todayPortions, double weekPortions) {
        Resources res = getResources();
        portionsText.setText(res.getString(R.string.history_portions_title) + " "
            + String.format(PORTIONS_FORMAT, todayPortions, weekPortions));
    }

    public void onPreviousClick(View v) {
        // Show previous day
        loadDay(timeUtil.addDays(day, -1));
    }

    public void onNextClick(View v) {
        // Show next day
        loadDay(timeUtil.addDays(day, 1));
    }

    public void onTodayClick(View v) {
        // Show this day
        loadDay(timeUtil.getCurrentDrinkingDate(prefs));
    }

    public void onSelectDayClick(View v) {
        // Select day to show
        LogUtil.d(TAG, "Showing date selection dialog");
        showDialog(Common.DIALOG_SELECT_DATE);
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public DBAdapter getDBAdapter() {
        return adapter;
    }

    /*
     * Dialog handling
     * -------------------------------------------------------------
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case Common.DIALOG_SHOW_DRINK_DETAILS:
            dialog = new DrinkDetailsDialog(this);
            return dialog;
        case Common.DIALOG_SELECT_DATE:
            Calendar cal = timeUtil.getCalendar(day);
            dialog = new DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            return dialog;
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case Common.DIALOG_SHOW_DRINK_DETAILS: {
            DrinkDetailsDialog d = (DrinkDetailsDialog) dialog;
            d.showDrinkSelection(selectedEvent, true);
        }
            break;
        case Common.DIALOG_SELECT_DATE: {
            Calendar cal = timeUtil.getCalendar(day);
            DatePickerDialog d = (DatePickerDialog) dialog;
            d.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        }
            break;
        }
        super.onPrepareDialog(id, dialog);
    }

    /*
     * Context menu handling
     * ----------------------------------------------------------
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        if (info instanceof AdapterContextMenuInfo) {
            AdapterContextMenuInfo mi = (AdapterContextMenuInfo) info;
            int pos = mi.position;
            if (pos != AdapterView.INVALID_POSITION) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.history_drink_actions, menu);
                DrinkSelection sel = listAdapter.getItem(pos);
                menu.setHeaderTitle(sel.getIconText(getResources()));
                return;
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
        int pos = mi.position;
        if (pos != AdapterView.INVALID_POSITION) {
            DrinkEvent selectedEvent = listAdapter.getItem(pos);
            switch (item.getItemId()) {

            case R.id.action_delete:
                // Delete this drink event from the history
                DrinkActions.deleteDrinkEvent(history, selectedEvent, this);
                return true;

            case R.id.action_modify:
                // Modify this drink event from the history
                // Start modifying the drink
                DrinkActivities.startModifyDrinkEvent(this, selectedEvent, true);
                return true;

            case R.id.action_show_info:
                // Show the information for this drink event
                showEventDetails(selectedEvent);
                return true;

            }
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Return from activities
     * --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

            case Common.ACTIVITY_CODE_MODIFY_DRINK_EVENT: {
                // Modify an existing drink
                Bundle extras = data.getExtras();
                DrinkSelection modifications = (DrinkSelection) extras.get(KEY_RESULT);
                long originalID = extras.getLong(KEY_ORIGINAL);
                DrinkActions.updateDrinkEvent(history, originalID, modifications, this);
            }
                break;

            case Common.ACTIVITY_CODE_ADD_DRINK_FOR_DAY: {
                // Add a new drink for the shown day
                DrinkSelection drink = DrinkActivities.getDrinkSelectionFromResult(data);
                DrinkActions.addDrinkForSelectedDay(history, drink, day, this);
            }
                break;

            }
        }
    }

    /*
     * Options menu handling
     * --------------------------------------------------
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.history_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_clear_day:
            // Ask the user if they really wish to delete all drinks
            Confirmation.showConfirmation(this, R.string.confirm_clear_day, new Runnable() {
                @Override
                public void run() {
                    DrinkActions.clearEventsFromDay(history, day, HistoryActivity.this);
                }
            });
            return true;
        case R.id.action_add_drink:
            DrinkActivities.startSelectDrink(this, Common.ACTIVITY_CODE_ADD_DRINK_FOR_DAY);
            return true;
        case R.id.action_help:
            // Show the help screen for this activity
            showHelp(null);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    /** Shows the statistics screen. */
    public void showHelp(View v) {
        HelpActivity.showHelp(R.string.help_history, this);
    }

}
