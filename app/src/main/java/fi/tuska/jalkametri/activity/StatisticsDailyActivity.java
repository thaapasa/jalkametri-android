package fi.tuska.jalkametri.activity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.data.DailyStatisticsGraph;
import fi.tuska.jalkametri.gui.GraphView;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.StringUtil;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * Shows statistics about your drinking habits. The truth is often harsh;
 * ignorance bliss.
 *
 * @author Tuukka Haapasalo
 */
public class StatisticsDailyActivity extends AbstractStatisticsActivity {

    public static final String TAG = "StatisticsDailyActivity";

    public static final String DAY_STORE_KEY = "daily_statistics_day";
    public static final String TYPE = "daily_statistics_type";

    private TextView dateTitle;
    private TextView dateSubtitle;
    private DateFormat dateTitleFormat;

    private Date day;
    private Date start;
    private Date end;

    private Type type;

    private List<DailyDrinkStatistics> dailyStats;
    private GraphView graphView;

    public enum Type {
        Yearly, Monthly, Weekly
    };

    public StatisticsDailyActivity() {
        super();
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        type = (Type) intent.getSerializableExtra(TYPE);

        setContentView(R.layout.statistics_daily);

        graphView = (GraphView) findViewById(R.id.graph);
        dateTitle = (TextView) findViewById(R.id.browser_title);
        dateSubtitle = (TextView) findViewById(R.id.browser_subtitle);
        dateSubtitle.setVisibility(View.GONE);

        dateTitleFormat = timeUtil.getMonthCorrectedDateFormat(getDateTitlePattern(this, type));

        loadDay(timeUtil.getCurrentTime());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Date d = (Date) savedInstanceState.getSerializable(DAY_STORE_KEY);
        if (d != null)
            loadDay(d);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DAY_STORE_KEY, day);
    }

    protected static String getTitle(Context context, Type type, Date date) {
        DateFormat f = new TimeUtil(context).getMonthCorrectedDateFormat(getDateTitlePattern(
            context, type));
        return StringUtil.uppercaseFirstLetter(f.format(date));
    }

    protected static String getDateTitlePattern(Context context, Type type) {
        Resources res = context.getResources();
        switch (type) {
        case Yearly:
            return res.getString(R.string.stats_yearly_title_pattern);
        case Monthly:
            return res.getString(R.string.stats_monthly_title_pattern);
        case Weekly:
            return res.getString(R.string.stats_weekly_title_pattern);
        }
        LogUtil.w(TAG, "Unknown type: %s", type);
        return "???";
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */

    @Override
    public void updateUI() {
        super.updateUI();
        dateTitle.setText(StringUtil.uppercaseFirstLetter(dateTitleFormat.format(day)));
    }

    public void showGraph(View v) {
        GraphActivity.showGraph(this, start, end, type);
    }

    public void onPreviousClick(View v) {
        gotoNextOrPrevious(-1);
    }

    public void onNextClick(View v) {
        gotoNextOrPrevious(1);
    }

    private void gotoNextOrPrevious(int multiplier) {
        // Go to next year/month/week
        Calendar cal = timeUtil.getCalendar(day);
        cal.setLenient(true);
        switch (type) {
        case Weekly:
            cal.add(Calendar.DAY_OF_MONTH, multiplier * 7);
            break;
        case Monthly:
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.add(Calendar.MONTH, multiplier * 1);
            break;
        case Yearly:
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.add(Calendar.YEAR, multiplier * 1);
            break;
        }
        loadDay(cal.getTime());
    }

    public void onTodayClick(View v) {
        // Show this day
        loadDay(timeUtil.getCurrentDrinkingDate(prefs));
    }

    public void onSelectDayClick(View v) {
        // Select day to show
        LogUtil.d(TAG, "Showing date selection dialog");
        Calendar cal = timeUtil.getCalendar(day);
        // Show a date picker dialog
        new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                loadDay(timeUtil.getCalendarFromDatePicker(year, monthOfYear, dayOfMonth)
                    .getTime());
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            .show();
    }

    private void loadDay(Date date) {
        calculateDates(date);

        LogUtil.d(TAG, "Loading daily statistics between %s and %s", start, end);
        setStatistics(statistics.getGeneralStatistics(start, end));
        dailyStats = statistics.getDailyDrinkAmounts(start, end);
        graphView.clear();
        graphView.addGraph(DailyStatisticsGraph.getGraph(type, dailyStats, prefs, this));

        updateUI();
    }

    private void calculateDates(Date date) {
        this.day = timeUtil.clearTimeValues(date);

        Calendar cal = timeUtil.getCalendar(day);
        switch (type) {
        case Yearly:
            cal.set(Calendar.MONTH, Calendar.JANUARY);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            break;
        case Monthly:
            cal.set(Calendar.DAY_OF_MONTH, 1);
            break;
        case Weekly:
            cal = timeUtil.getStartOfWeek(day, prefs);
            timeUtil.clearTimeValues(cal);
            break;
        }
        start = cal.getTime();

        switch (type) {
        case Yearly:
            cal.add(Calendar.YEAR, 1);
            break;
        case Monthly:
            cal.add(Calendar.MONTH, 1);
            break;
        case Weekly:
            cal.add(Calendar.DAY_OF_MONTH, 7);
            break;
        }
        // Back up to the previous day
        cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, -1);

        end = cal.getTime();
    }

}
