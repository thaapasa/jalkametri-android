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

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.activity.StatisticsDailyActivity.Type;

/**
 * Shows statistics about your drinking habits. The truth is often harsh;
 * ignorance bliss.
 * 
 * @author Tuukka Haapasalo
 */
public class StatisticsActivity extends TabActivity {

    public static final String TAG = "StatisticsActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics);

        TabHost tabHost = getTabHost(); // The activity TabHost

        // Create an Intent to launch an Activity for the tab (to be reused)
        Intent intent = new Intent().setClass(this, StatisticsSummaryActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        TabHost.TabSpec spec = tabHost.newTabSpec("summary")
            .setIndicator(getResources().getString(R.string.stats_tab_summary))
            .setContent(intent);
        tabHost.addTab(spec);

        // Do the same for the other tabs
        addDailyStatisticsTab(tabHost, Type.Yearly, R.string.stats_tab_yearly);
        addDailyStatisticsTab(tabHost, Type.Monthly, R.string.stats_tab_monthly);
        addDailyStatisticsTab(tabHost, Type.Weekly, R.string.stats_tab_weekly);

        tabHost.setCurrentTab(0);
    }

    private void addDailyStatisticsTab(TabHost tabHost, StatisticsDailyActivity.Type type,
        int titleResId) {

        Intent intent = new Intent().setClass(this, StatisticsDailyActivity.class);
        intent.putExtra(StatisticsDailyActivity.TYPE, type);
        TabHost.TabSpec spec = tabHost.newTabSpec(type.toString())
            .setIndicator(getResources().getString(titleResId)).setContent(intent);

        tabHost.addTab(spec);
    }

}
