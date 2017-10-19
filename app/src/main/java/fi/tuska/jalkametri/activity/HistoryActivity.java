package fi.tuska.jalkametri.activity;

import android.app.DatePickerDialog;
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
import fi.tuska.jalkametri.util.AssertionUtils;
import fi.tuska.jalkametri.util.Converter;
import fi.tuska.jalkametri.util.LocaleHelper;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.StringUtil;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import static android.view.Gravity.CENTER_VERTICAL;
import static android.view.Gravity.START;
import static fi.tuska.jalkametri.Common.DEFAULT_ICON_RES;
import static fi.tuska.jalkametri.Common.KEY_ORIGINAL;
import static fi.tuska.jalkametri.Common.KEY_RESULT;

/**
 * An activity that shows the user's drinking history, day by day.
 *
 * @author Tuukka Haapasalo
 */
public class HistoryActivity extends ListActivity implements GUIActivity, DBActivity {

    private static final String TAG = "HistoryActivity";

    private static final String KEY_SHOW_DAY = "day";

    private static final String PORTIONS_FORMAT = "%.1f / %.1f";

    private DateTimeFormatter wdayFormat;

    private DBAdapter adapter;
    private LocalDate day;
    private TextView dateText;
    private TextView weekText;
    private TextView portionsText;
    private NamedIconAdapter<DrinkEvent> listAdapter;
    private History history;
    private Preferences prefs;
    private String weekPrefix;
    private DateTimeFormatter timeFormat;
    private TimeUtil timeUtil;

    private String iconNameUnitStr;
    private final Converter<NamedIcon, String> iconNameConverter = new Converter<NamedIcon, String>() {
        @Override
        public String convert(NamedIcon src) {
            DrinkEvent drink = (DrinkEvent) src;
            return String.format(timeUtil.getLocale(), "%s: %s\n%.1f %s", timeFormat.print(drink.getTime()),
                    drink.getName(), drink.getPortions(getContext()), iconNameUnitStr);
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
    public static void prepareForDay(Intent intent, LocalDate showDay) {
        intent.putExtra(KEY_SHOW_DAY, showDay);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.INSTANCE.onAttach(newBase));
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        this.timeUtil = new TimeUtil(getContext());

        Resources res = getResources();
        // Update title to enforce correct language
        setTitle(res.getString(R.string.title_history));

        this.adapter = new DBAdapter(this);
        this.history = new HistoryDB(adapter, this);
        this.prefs = new PreferencesImpl(this);

        this.portionsText = (TextView) findViewById(R.id.portions);
        AssertionUtils.INSTANCE.expect(portionsText != null);
        setPortions(0, 0);
        this.dateText = (TextView) findViewById(R.id.browser_title);
        AssertionUtils.INSTANCE.expect(dateText != null);
        this.weekText = (TextView) findViewById(R.id.browser_subtitle);
        AssertionUtils.INSTANCE.expect(weekText != null);

        weekPrefix = res.getString(R.string.history_week_prefix) + " ";

        this.iconNameUnitStr = res.getString(R.string.history_list_units);
        this.wdayFormat = timeUtil.getDateFormatWDay();
        this.timeFormat = timeUtil.getTimeFormat();
        Bundle extras = getIntent().getExtras();
        LocalDate showDay = (LocalDate) extras.get(KEY_SHOW_DAY);
        initComponents();
        loadDay(showDay);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SHOW_DAY, day);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        LocalDate date = (LocalDate) state.get(KEY_SHOW_DAY);
        if (date != null)
            loadDay(date);
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
        LogUtil.INSTANCE.d(TAG, "Loaded %d drinks for %s", drinks.size(), wdayFormat.print(day));
        listAdapter = new NamedIconAdapter<>(this, drinks, DEFAULT_ICON_RES,
                false, START | CENTER_VERTICAL, iconNameConverter);
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
        JalkametriActivity.Companion.showCustomDialog(this,
                DrinkDetailsDialog.Companion.createDialog(event, true));
    }

    private void loadDay(LocalDate date) {
        this.day = date;
        dateText.setText(StringUtil.INSTANCE.uppercaseFirstLetter(wdayFormat.print(date)));
        weekText.setText(weekPrefix + day.getWeekOfWeekyear());
        LogUtil.INSTANCE.d(TAG, "Selected day: %s", day);
        updateUI();
    }

    private void setPortions(double todayPortions, double weekPortions) {
        Resources res = getResources();
        portionsText.setText(res.getString(R.string.history_portions_title) + " "
                + String.format(timeUtil.getLocale(), PORTIONS_FORMAT, todayPortions, weekPortions));
    }

    public void onPreviousClick(View v) {
        // Show previous day
        loadDay(day.minusDays(1));
    }

    public void onNextClick(View v) {
        // Show next day
        loadDay(day.plusDays(1));
    }

    public void onTodayClick(View v) {
        // Show this day
        loadDay(timeUtil.getCurrentDrinkingDate(prefs));
    }

    public void onSelectDayClick(View v) {
        // Select day to show
        LogUtil.INSTANCE.d(TAG, "Showing date selection dialog");
        // Show a date picker dialog
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                loadDay(new LocalDate(year, monthOfYear + 1, dayOfMonth));
            }
        }, day.getYear(), day.getMonthOfYear() - 1, day.getDayOfMonth()).show();
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

    /**
     * Shows the statistics screen.
     */
    public void showHelp(View v) {
        HelpActivity.showHelp(R.string.help_history, this);
    }

}
