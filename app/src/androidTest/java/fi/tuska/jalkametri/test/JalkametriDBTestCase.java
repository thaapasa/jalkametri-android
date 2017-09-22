package fi.tuska.jalkametri.test;

import android.content.Context;
import fi.tuska.jalkametri.DBActivity;
import fi.tuska.jalkametri.UnitTestDBAdapter;
import fi.tuska.jalkametri.activity.GUIActivity;
import fi.tuska.jalkametri.db.DBAdapter;

public abstract class JalkametriDBTestCase extends JalkametriTestCase {

    /**
     * Will be initialized during setUp(). Do not reference from constructors!
     */
    protected DBAdapter adapter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.adapter = new UnitTestDBAdapter(getContext());
    }

    protected final DBGUIActivity dummyParentActivity = new DBGUIActivity();

    private class DBGUIActivity implements DBActivity, GUIActivity {

        @Override
        public void updateUI() {
            // Nada
        }

        @Override
        public Context getContext() {
            return JalkametriDBTestCase.this.getContext();
        }

        @Override
        public DBAdapter getDBAdapter() {
            return adapter;
        }

    }
}
