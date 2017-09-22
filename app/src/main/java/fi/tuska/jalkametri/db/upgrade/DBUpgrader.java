package fi.tuska.jalkametri.db.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * An interface for actions that update the database schema and/or contents
 * when jAlkaMetri is updated from a version to another.
 *
 * @author Tuukka Haapasalo
 */
public interface DBUpgrader {

    void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion);

}
