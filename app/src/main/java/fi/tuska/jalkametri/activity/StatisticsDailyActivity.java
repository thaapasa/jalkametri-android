package fi.tuska.jalkametri.activity;

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
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

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
    private DateTimeFormatter dateTitleFormat;

    private LocalDate day;
    private LocalDate start;
    private LocalDate end;

    private Type type;

    private List<DailyDrinkStatistics> dailyStats;
    private GraphView graphView;

    public enum Type {
        Yearly, Monthly, Weekly
    }

    ;

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

        setContentView(R.layout.activity_statistics_daily);

        graphView = (GraphView) findViewById(R.id.graph);
        dateTitle = (TextView) findViewById(R.id.browser_title);
        dateSubtitle = (TextView) findViewById(R.id.browser_subtitle);
        dateSubtitle.setVisibility(View.GONE);

        dateTitleFormat = getTimeUtil().getMonthCorrectedDateFormat(getDateTitlePattern(this, type));

        loadDay(new LocalDate(getTimeUtil().getTimeZone()));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LocalDate d = (LocalDate) savedInstanceState.getSerializable(DAY_STORE_KEY);
        if (d != null)
            loadDay(d);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(DAY_STORE_KEY, day);
    }

    protected static String getTitle(Context context, Type type, LocalDate date) {
        DateTimeFormatter f = new TimeUtil(context).getMonthCorrectedDateFormat(getDateTitlePattern(
                context, type));
        return StringUtil.INSTANCE.uppercaseFirstLetter(f.print(date));
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
        LogUtil.INSTANCE.w(TAG, "Unknown type: %s", type);
        return "???";
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */

    @Override
    public void updateUI() {
        super.updateUI();
        dateTitle.setText(StringUtil.INSTANCE.uppercaseFirstLetter(dateTitleFormat.print(day)));
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
        LocalDate d = this.day;
        // Go to next year/month/week
        switch (type) {
            case Weekly:
                d = d.plusDays(7 * multiplier);
                break;
            case Monthly:
                d = d.withDayOfMonth(1).plusMonths(multiplier);
                break;
            case Yearly:
                d = d.withDayOfMonth(1).withMonthOfYear(1).plusYears(multiplier);
                break;
        }
        loadDay(d);
    }

    public void onTodayClick(View v) {
        // Show this day
        loadDay(getTimeUtil().getCurrentDrinkingDate(getPrefs()));
    }

    public void onSelectDayClick(View v) {
        // Select day to show
        LogUtil.INSTANCE.d(TAG, "Showing date selection dialog");
        // Show a date picker dialog
        new DatePickerDialog(this, new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                loadDay(new LocalDate(year, monthOfYear, dayOfMonth));
            }
        }, day.getYear(), day.getMonthOfYear(), day.getDayOfMonth()).show();
    }

    private void loadDay(LocalDate date) {
        calculateDates(date);

        LogUtil.INSTANCE.d(TAG, "Loading daily statistics between %s and %s", start, end);
        setStatistics(statistics.getGeneralStatistics(start, end));
        dailyStats = statistics.getDailyDrinkAmounts(start, end);
        graphView.clear();
        graphView.addGraph(DailyStatisticsGraph.getGraph(type, dailyStats, getPrefs(), this));

        updateUI();
    }

    private void calculateDates(LocalDate date) {
        this.day = date;

        switch (type) {
            case Yearly:
                start = day.withDayOfMonth(1).withMonthOfYear(1);
                break;
            case Monthly:
                start = day.withDayOfMonth(1);
                break;
            case Weekly:
                start = getTimeUtil().getStartOfWeek(day, getPrefs());
                break;
        }

        switch (type) {
            case Yearly:
                end = start.plusYears(1);
                break;
            case Monthly:
                end = start.plusMonths(1);
                break;
            case Weekly:
                end = start.plusDays(7);
                break;
        }
        // Back up to the previous day
        end = end.minusDays(1);
    }

}
