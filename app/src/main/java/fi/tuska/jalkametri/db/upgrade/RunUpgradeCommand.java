package fi.tuska.jalkametri.db.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * A class that allows the DB upgrade process to run custom SQL commands to
 * update the database schema and/or contents.
 *
 * @author Tuukka Haapasalo
 */
public class RunUpgradeCommand implements DBUpgrader {

    private final String[] updateCmds;

    public RunUpgradeCommand(String... updateCmds) {
        this.updateCmds = updateCmds;
    }

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        for (String updateS : updateCmds) {
            LogUtil.INSTANCE.d(DBAdapter.TAG, "Running upgrade SQL: \"%s\"", updateS);
            db.execSQL(updateS);
        }
    }

    @Override
    public String toString() {
        return "DB upgrader [running " + updateCmds.length + " commands]";
    }

}
