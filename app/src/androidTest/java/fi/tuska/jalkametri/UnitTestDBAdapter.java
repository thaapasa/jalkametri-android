package fi.tuska.jalkametri;

import android.content.Context;
import fi.tuska.jalkametri.db.DBAdapter;

public class UnitTestDBAdapter extends DBAdapter {

    /**
     * Unit test database is different from real database.
     */
    private static final String DATABASE_NAME = "jalkametri.unit.db";

    public UnitTestDBAdapter(Context context) {
        super(context, DATABASE_NAME, DBAdapter.DATABASE_VERSION);
    }

}
