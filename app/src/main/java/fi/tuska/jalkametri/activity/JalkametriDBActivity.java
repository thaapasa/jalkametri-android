package fi.tuska.jalkametri.activity;

import android.os.Bundle;
import fi.tuska.jalkametri.DBActivity;
import fi.tuska.jalkametri.db.DBAdapter;

/**
 * Abstract base class for jAlkaMetri activities that use the database;
 * contains common functionality.
 *
 * @author Tuukka Haapasalo
 */
public abstract class JalkametriDBActivity extends JalkametriActivity implements GUIActivity,
    DBActivity {

    protected DBAdapter adapter;

    protected JalkametriDBActivity(int titleResourceId, int helpTextResId) {
        super(titleResourceId, helpTextResId);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new DBAdapter(this);
    }

    @Override
    protected void onPause() {
        adapter.close();
        super.onPause();
    }

    @Override
    public DBAdapter getDBAdapter() {
        return adapter;
    }

}
