package fi.tuska.jalkametri.db.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import fi.tuska.jalkametri.util.CollectionUtils;

/**
 * A class that allows DB upgrading to run multiple commands.
 *
 * @author Tuukka Haapasalo
 */
public class RunMultipleCommands implements DBUpgrader {

    private final DBUpgrader[] upgraders;

    public RunMultipleCommands(DBUpgrader... upgraders) {
        this.upgraders = upgraders;
    }

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        for (DBUpgrader upgrader : upgraders) {
            upgrader.updateDB(context, db, fromVersion, toVersion);
        }
    }

    @Override
    public String toString() {
        return "Multiple commands: [" + CollectionUtils.implodeArray(upgraders, ", ") + "]";
    }

}
