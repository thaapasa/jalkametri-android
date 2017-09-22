package fi.tuska.jalkametri.activity;

import java.text.DateFormat;
import java.util.Date;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.GeneralStatistics;
import fi.tuska.jalkametri.dao.Statistics;
import fi.tuska.jalkametri.db.StatisticsDB;
import fi.tuska.jalkametri.util.NumberUtil;

public abstract class AbstractStatisticsActivity extends JalkametriDBActivity {

    public static final String TAG = StatisticsActivity.TAG;

    // Drink amount stats
    private TextView totalDrinks;
    private TextView totalPortions;
    private TextView totalAlcohol;

    // Date stats
    private TextView firstDay;
    private TextView allDays;
    private TextView soberDays;
    private TextView drunkDays;

    // Average portions per days
    private TextView avgPortionsAllDays;
    private TextView avgPortionsDrunkDays;
    private TextView avgWeeklyPortions;

    protected Statistics statistics;
    protected GeneralStatistics generalStats;

    protected DateFormat dateFormat;

    public AbstractStatisticsActivity() {
        super(R.string.title_statistics, R.string.help_statistics);
        setShowDefaultHelpMenu(true);
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

        this.statistics = new StatisticsDB(getDBAdapter(), prefs, this);
        this.generalStats = null;
        this.dateFormat = timeUtil.getDateFormatFull();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        populateViews();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        populateViews();
    }

    private void populateViews() {
        totalDrinks = (TextView) findViewById(R.id.total_drinks);
        totalPortions = (TextView) findViewById(R.id.total_portions);
        totalAlcohol = (TextView) findViewById(R.id.total_alcohol);

        firstDay = (TextView) findViewById(R.id.first_day);
        allDays = (TextView) findViewById(R.id.all_days);
        soberDays = (TextView) findViewById(R.id.sober_days);
        drunkDays = (TextView) findViewById(R.id.drunk_days);

        avgPortionsAllDays = (TextView) findViewById(R.id.avg_portions_all_days);
        avgPortionsDrunkDays = (TextView) findViewById(R.id.avg_portions_drunk_days);
        avgWeeklyPortions = (TextView) findViewById(R.id.avg_weekly_portions);
    }

    /**
     * This must be called before calling updateUI()
     */
    protected void setStatistics(GeneralStatistics stats) {
        this.generalStats = stats;
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */

    @Override
    public void updateUI() {
        Resources res = getResources();
        // Total drinks
        if (totalDrinks != null)
            totalDrinks.setText(NumberUtil.toString(generalStats.getTotalDrinks(), res));
        if (totalPortions != null)
            totalPortions.setText(NumberUtil.toString(generalStats.getTotalPortions(), res));

        // As pure alcohol
        if (totalAlcohol != null) {
            String pureAlcoholFormatString = res.getString(R.string.stats_total_alcohol_format);
            totalAlcohol.setText(String.format(pureAlcoholFormatString,
                NumberUtil.toString(generalStats.getTotalPortionsAsPureAlcoholLiters(getContext()), res)));
        }

        // First day
        if (firstDay != null) {
            Date date = generalStats.getFirstDay();
            firstDay.setText(date != null ? dateFormat.format(date) : res
                .getString(R.string.stats_no_days));
        }
        if (allDays != null)
            allDays.setText(NumberUtil.toString(generalStats.getNumberOfRecordedDays(), res));

        // Sober days
        if (soberDays != null)
            soberDays.setText(String.format("%s (%s %%)",
                NumberUtil.toString(generalStats.getNumberOfSoberDays(), res),
                NumberUtil.toString(generalStats.getSoberDayPercentage(), res)));
        // Drunk days
        if (drunkDays != null)
            drunkDays.setText(String.format("%s (%s %%)",
                NumberUtil.toString(generalStats.getNumberOfDrunkDays(), res),
                NumberUtil.toString(generalStats.getDrunkDayPercentage(), res)));

        // Average portions per day
        if (avgPortionsAllDays != null)
            avgPortionsAllDays.setText(NumberUtil.toString(generalStats.getAvgPortionsAllDays(),
                res));
        if (avgPortionsDrunkDays != null)
            avgPortionsDrunkDays.setText(NumberUtil.toString(
                generalStats.getAvgPortionsDrunkDays(), res));
        if (avgWeeklyPortions != null)
            avgWeeklyPortions.setText(NumberUtil.toString(generalStats.getAvgWeeklyPortions(),
                res));
    }
}
