package fi.tuska.jalkametri.activity;

import android.os.Bundle;
import fi.tuska.jalkametri.R;

/**
 * Shows statistics about your drinking habits. The truth is often harsh;
 * ignorance bliss.
 *
 * @author Tuukka Haapasalo
 */
public class StatisticsSummaryActivity extends AbstractStatisticsActivity {

    public static final String TAG = "StatisticsSummaryActivity";

    public StatisticsSummaryActivity() {
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
        setContentView(R.layout.statistics_summary);

        setStatistics(statistics.getGeneralStatistics());
    }

}
