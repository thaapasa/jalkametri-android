package fi.tuska.jalkametri.db;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import fi.tuska.jalkametri.db.upgrade.DBCreator;
import fi.tuska.jalkametri.db.upgrade.DBUpgrader;
import fi.tuska.jalkametri.db.upgrade.DropAndCreate;
import fi.tuska.jalkametri.db.upgrade.RecalculatePortions;
import fi.tuska.jalkametri.db.upgrade.RunMultipleCommands;
import fi.tuska.jalkametri.db.upgrade.RunUpgradeCommand;
import fi.tuska.jalkametri.util.AssertionUtils;
import fi.tuska.jalkametri.util.LogUtil;
import org.joda.time.LocalTime;

import static java.util.Locale.ENGLISH;

public class DBAdapter {

    // Wait for two minutes at maximum
    private static final int MAX_DB_WAIT_TIME_MILLIS = 1000 * 60 * 2;

    private static final String DATABASE_NAME = "jalkametri.db";
    public static final int DATABASE_VERSION = 36;

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_ICON = "icon";
    public static final String KEY_ORDER = "pos";
    public static final String KEY_PORTIONS = "portions";
    public static final String KEY_CATEGORY_ID = "cat_id";
    public static final String KEY_STRENGTH = "strength";
    public static final String KEY_VOLUME = "volume";
    public static final String KEY_SIZE_ID = "size_id";
    public static final String KEY_SIZE_NAME = "size_name";
    public static final String KEY_TIME = "time";
    public static final String KEY_COMMENT = "comment";

    public static final String TAG = "DBAdapter";

    public static final String ID_WHERE_CLAUSE = KEY_ID + " = ?";

    private SQLiteDatabase db;
    private final Context context;
    private DBHelper helper;

    private static DBUpgrader dbCreator = new DBCreator();
    private static DBUpgrader dbReCreator = new DropAndCreate();

    // The integer key is the version currently installed.
    private static TreeMap<Integer, DBUpgrader> upgraders = new TreeMap<Integer, DBUpgrader>();

    private static boolean databaseLocked = false;
    /** The object on which the database synchronization lock is taken. */
    private static Object databaseLock = new Object();

    static {
        // When updating from version [0 -> 31] to [X], run DropAndCreate when
        // updating
        upgraders.put(0, new DropAndCreate());
        // When updating from version [32] to [X], run RunUpgradeCommand(...)
        // Add the favourites table (the old version)
        upgraders.put(32, new RunUpgradeCommand(FavouritesDB.SQL_CREATE_TABLE_FAVOURITES_1));
        // Update from [33] to [X]
        // Add comment fields
        upgraders.put(33, new RunUpgradeCommand(
            "ALTER TABLE drinks ADD COLUMN comment TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE history ADD COLUMN comment TEXT NOT NULL DEFAULT ''",
            "ALTER TABLE favourites ADD COLUMN comment TEXT NOT NULL DEFAULT ''"));
        // Update from [34] to [X]
        // Add portions column to history table, recalculate portions
        upgraders.put(34, new RunMultipleCommands(new RunUpgradeCommand(
            "ALTER TABLE history ADD COLUMN portions FLOAT NOT NULL DEFAULT 0"),
            new RecalculatePortions("history")));
        // Update from [35] to [X]
        // Add indexes to history table
        upgraders.put(35, new RunUpgradeCommand(HistoryDB.SQL_CREATE_HISTORY_INDEX));
    }

    public DBAdapter(Activity activity) {
        this.context = activity.getApplicationContext();
        this.helper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public DBAdapter(Context context) {
        this.context = context.getApplicationContext();
        this.helper = new DBHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * For testing: override and use this constructor to specify test database
     * name
     */
    protected DBAdapter(Context context, String dbName, int dbVersion) {
        this.context = context.getApplicationContext();
        this.helper = new DBHelper(context, dbName, null, dbVersion);
    }

    public String getDatabaseFilename() {
        return DATABASE_NAME;
    }

    public Context getContext() {
        return context;
    }

    public void open() {
        if (db != null) {
            return;
        }
        db = helper.getWritableDatabase();
    }

    public void lockDatabase() {
        synchronized (databaseLock) {
            close();
            databaseLocked = true;
            LogUtil.INSTANCE.d(TAG, "Locked database");
        }
    }

    public void unlockDatabase() {
        synchronized (databaseLock) {
            databaseLocked = false;
            LogUtil.INSTANCE.d(TAG, "Unlocked database");
            // Wake up waiters
            databaseLock.notifyAll();
        }
    }

    public void close() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    protected SQLiteDatabase getDatabase() {
        if (db == null)
            open();
        AssertionUtils.INSTANCE.expect(db != null);
        return db;
    }

    public void beginTransaction() {
        getDatabase();
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        getDatabase();
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        if (db != null) {
            db.endTransaction();
        }
    }

    private void waitForLock() {
        // Wait until database is not locked
        long startWaitTime = System.currentTimeMillis();
        synchronized (databaseLock) {
            while (databaseLocked) {
                // Database is locked, wait until lock is released
                try {
                    databaseLock.wait(MAX_DB_WAIT_TIME_MILLIS);
                } catch (InterruptedException e) {
                    // No matter, continue loop
                }
                // If database is not locked, we're done
                if (!databaseLocked)
                    return;
                // Check if database has been locked for too long
                long curTime = System.currentTimeMillis();
                if (curTime - startWaitTime > MAX_DB_WAIT_TIME_MILLIS) {
                    // Waited for too long
                    LogUtil.INSTANCE.w(TAG, "open() has waited for too long, unlocking database");
                    databaseLocked = false;
                    notifyAll();
                    return;
                }
            }
        }
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            LogUtil.INSTANCE.w(TAG, "Creating the database tables from scratch");
            dbCreator.updateDB(context, db, 0, DATABASE_VERSION);
        }

        private int getStartOfUpgrade(int oldVersion) {
            SortedMap<Integer, DBUpgrader> head = upgraders.headMap(oldVersion + 1);
            if (!head.isEmpty()) {
                return head.lastKey();
            }
            return 0;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            int curV = oldVersion;
            if (newVersion < oldVersion) {
                LogUtil.INSTANCE.w(TAG, "Reverse-upgrading DB from version %s to %s", oldVersion,
                    newVersion);
                dbReCreator.updateDB(context, db, oldVersion, newVersion);
                return;
            }

            LogUtil.INSTANCE.w(TAG, "Upgrading DB from version %d to %d", oldVersion, newVersion);

            int startVersion = getStartOfUpgrade(oldVersion);
            SortedMap<Integer, DBUpgrader> runUpdates = upgraders.tailMap(startVersion);

            for (Entry<Integer, DBUpgrader> e : runUpdates.entrySet()) {
                DBUpgrader upgrader = e.getValue();
                int newV = e.getKey();
                if (newV >= newVersion)
                    break;
                LogUtil.INSTANCE.w(TAG, "Running DB upgrader for version %d: %s", newV, upgrader);
                upgrader.updateDB(context, db, curV, newV);
                curV = newV;
            }
        }

    }

    public static final String formatAsSQLTime(LocalTime time) {
        return String.format(ENGLISH, "%02d:%02d", time.getHourOfDay(), time.getMinuteOfHour());
    }
    public static final String formatAsSQLTime(int hours, int minutes) {
        return String.format(ENGLISH, "%02d:%02d", hours, minutes);
    }

}
