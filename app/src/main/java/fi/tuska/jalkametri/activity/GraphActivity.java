package fi.tuska.jalkametri.activity;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.activity.StatisticsDailyActivity.Type;
import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.dao.Statistics;
import fi.tuska.jalkametri.data.DailyStatisticsGraph;
import fi.tuska.jalkametri.db.StatisticsDB;
import fi.tuska.jalkametri.gui.GraphView;
import fi.tuska.jalkametri.gui.GraphView.Graph;

/**
 * Shows a graph on the whole screen.
 *
 * @author Tuukka Haapasalo
 */
public class GraphActivity extends JalkametriDBActivity {

    private static final String KEY_DATE_START = "GraphActivity_startDate";
    private static final String KEY_DATE_END = "GraphActivity_endDate";
    private static final String KEY_TYPE = "GraphActivity_type";

    private TextView title;

    private Graph stats;
    private GraphView graphView;

    public GraphActivity() {
        super(R.string.title_statistics, NO_HELP_TEXT);
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */
    public static void showGraph(Activity parent, Date start, Date end, Type type) {
        Intent i = new Intent(parent, GraphActivity.class);
        i.putExtra(KEY_DATE_START, start);
        i.putExtra(KEY_DATE_END, end);
        i.putExtra(KEY_TYPE, type);
        parent.startActivity(i);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph);

        Intent intent = getIntent();
        // Grab the intent data
        Type type = (Type) intent.getSerializableExtra(KEY_TYPE);
        Date start = (Date) intent.getSerializableExtra(KEY_DATE_START);
        Date end = (Date) intent.getSerializableExtra(KEY_DATE_END);


        title = (TextView) findViewById(R.id.title);
        title.setText(StatisticsDailyActivity.getTitle(this, type, start));
        graphView = (GraphView) findViewById(R.id.graph);
        graphView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GraphActivity.this.finish();
            }
        });
        graphView.clear();

        Graph g = loadGraph(intent, type, start, end);
        if (g != null) {
            graphView.addGraph(g);
        }
    }

    private Graph loadGraph(Intent intent, Type type, Date start, Date end) {
        Statistics stats = new StatisticsDB(adapter, prefs, this);
        List<DailyDrinkStatistics> dailyStats = stats.getDailyDrinkAmounts(start, end);
        return DailyStatisticsGraph.getGraph(type, dailyStats, prefs, this);
    }

    @Override
    public void updateUI() {
        // Nothing needed
    }

}
