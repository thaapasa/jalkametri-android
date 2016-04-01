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
        setContentView(R.layout.statistics_summary);
        super.onCreate(savedInstanceState);

        setStatistics(statistics.getGeneralStatistics());
    }

}
