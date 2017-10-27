package fi.tuska.jalkametri.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.util.AssertionUtils;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_COMMENT;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ICON;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_PORTIONS;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_SIZE_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_STRENGTH;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_TIME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_VOLUME;
import static fi.tuska.jalkametri.db.DBAdapter.TAG;
import static org.joda.time.Duration.standardDays;

public class HistoryDB extends AbstractDB implements History {

    public static final String TABLE_NAME = "history";

    public static final String KEY_GROUP_NAME = "unique_name";
    public static final String COLUMN_GROUP_NAME = "(name || ', ' || size_name) AS "
            + KEY_GROUP_NAME;

    public static final String SQL_CREATE_TABLE_HISTORY_1 = "CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "drink_id INTEGER REFERENCES drinks (id) ON DELETE SET NULL, "
            + "name TEXT NOT NULL, "
            + "strength FLOAT NOT NULL, "
            + "volume FLOAT NOT NULL, "
            + "size_name TEXT NOT NULL, "
            + "icon TEXT NOT NULL, " + "time TEXT UNIQUE NOT NULL);";

    public static final String SQL_CREATE_TABLE_HISTORY_2 = "CREATE TABLE history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "drink_id INTEGER REFERENCES drinks (id) ON DELETE SET NULL, "
            + "name TEXT NOT NULL, "
            + "strength FLOAT NOT NULL, "
            + "volume FLOAT NOT NULL, "
            + "portions FLOAT NOT NULL DEFAULT 0, "
            + "size_name TEXT NOT NULL, "
            + "icon TEXT NOT NULL, "
            + "comment TEXT NOT NULL DEFAULT '', "
            + "time TEXT UNIQUE NOT NULL);";

    public static final String SQL_CREATE_HISTORY_INDEX = "CREATE INDEX IF NOT EXISTS history_time_idx ON history (time)";

    private final Context context;
    private final TimeUtil timeUtil;

    public HistoryDB(DBAdapter adapter, Context context) {
        super(adapter, TABLE_NAME);
        this.context = context;
        this.timeUtil = new TimeUtil(context);
    }

    private void createValues(ContentValues values, DrinkSelection selection) {
        DrinkSelectionHelper.createCommonValues(values, selection);
        {
            Instant time = selection.getTime();
            AssertionUtils.INSTANCE.expect(time != null);
            values.put(KEY_TIME, sqlDateFormat.print(time));
        }
        // Portions
        values.put(KEY_PORTIONS, selection.getPortions(context));
    }

    @Override
    public void createDrink(DrinkSelection selection) {
        ContentValues newValues = new ContentValues();
        createValues(newValues, selection);
        long id = db.getDatabase().insert(TABLE_NAME, null, newValues);
        AssertionUtils.INSTANCE.expect(id >= 0);
    }

    @Override
    public void updateEvent(long index, DrinkEvent event) {
        DBDataObject.enforceBackedObject(index);

        ContentValues newValues = new ContentValues();
        createValues(newValues, event);
        db.getDatabase().update(TABLE_NAME, newValues, getIndexClause(index), null);
    }

    @Override
    public List<DrinkEvent> getDrinks(LocalDate day, boolean ascending) {
        Preferences prefs = new PreferencesImpl(context);
        Instant start = timeUtil.getStartOfDrinkDay(day, prefs);
        Instant end = start.plus(standardDays(1));
        return getDrinks(start, end, ascending);
    }

    private static final String[] DRINK_QUERY_COLUMNS = new String[]{KEY_ID, KEY_NAME,
            KEY_STRENGTH, KEY_VOLUME, KEY_SIZE_NAME, KEY_ICON, KEY_COMMENT, KEY_TIME};
    private static final String TIME_QUERY_WHERE = KEY_TIME + " >= ? AND " + KEY_TIME + " < ?";

    private static final String[] PREVIOUS_QUERY_COLUMNS = new String[]{KEY_ID, KEY_NAME,
            KEY_STRENGTH, KEY_VOLUME, KEY_SIZE_NAME, KEY_ICON, KEY_COMMENT, KEY_TIME,
            COLUMN_GROUP_NAME};

    @Override
    public List<DrinkEvent> getDrinks(Instant fromTime, Instant toTime, boolean ascending) {
        LogUtil.INSTANCE.d(TAG, "Querying for drinks between %s and %s", fromTime, toTime);

        Cursor cursor = db.getDatabase().query(TABLE_NAME, DRINK_QUERY_COLUMNS,
                TIME_QUERY_WHERE,
                new String[]{sqlDateFormat.print(fromTime), sqlDateFormat.print(toTime)}, null,
                null, KEY_TIME + (ascending ? " ASC" : " DESC"));
        int count = cursor.getCount();
        List<DrinkEvent> drinks = new ArrayList<DrinkEvent>(count);
        if (cursor.moveToFirst()) {
            do {
                int c = -1;
                drinks.add(createDrinkSelection(cursor.getLong(++c), cursor.getString(++c),
                        cursor.getDouble(++c), cursor.getDouble(++c), cursor.getString(++c),
                        cursor.getString(++c), cursor.getString(++c), cursor.getString(++c)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return drinks;
    }

    @Override
    public List<DrinkEvent> getPreviousDrinks(int limit) {
        LogUtil.INSTANCE.d(TAG, "Querying for previous drinks");

        Cursor cursor = db.getDatabase().query(false, TABLE_NAME, PREVIOUS_QUERY_COLUMNS,
                null, null, KEY_GROUP_NAME, null, KEY_TIME + " DESC", String.valueOf(limit));

        int count = cursor.getCount();
        List<DrinkEvent> drinks = new ArrayList<DrinkEvent>(count);
        if (cursor.moveToFirst()) {
            do {
                int c = -1;
                drinks.add(createDrinkSelection(cursor.getLong(++c), cursor.getString(++c),
                        cursor.getDouble(++c), cursor.getDouble(++c), cursor.getString(++c),
                        cursor.getString(++c), cursor.getString(++c), cursor.getString(++c)));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return drinks;
    }

    @Override
    public DrinkEvent getDrink(long index) {
        DBDataObject.enforceBackedObject(index);
        LogUtil.INSTANCE.d(TAG, "Querying for drink %d", index);

        Cursor cursor = db.getDatabase().query(true, TABLE_NAME, DRINK_QUERY_COLUMNS,
                getIndexClause(index), null, null, null, null, null);
        DrinkEvent event = null;
        if (cursor.moveToFirst()) {
            int c = -1;
            event = createDrinkSelection(cursor.getLong(++c), cursor.getString(++c),
                    cursor.getDouble(++c), cursor.getDouble(++c), cursor.getString(++c),
                    cursor.getString(++c), cursor.getString(++c), cursor.getString(++c));
        }
        cursor.close();

        return event;
    }

    private DrinkEvent createDrinkSelection(long eventId, String name, double strength,
                                            double volume, String sizeName, String icon, String comment, String time) {
        Drink drink = new Drink(name, strength, icon, comment, new ArrayList<DrinkSize>());
        DrinkSize size = new DrinkSize(sizeName, volume, icon);

        Instant drinkTime = null;
        try {
            drinkTime = Instant.parse(time, sqlDateFormat);
        } catch (Exception e) {
            LogUtil.INSTANCE.w(TAG, e.getMessage());
        }

        return new DrinkEvent(eventId, drink, size, drinkTime);
    }

    @Override
    public void clearDay(LocalDate day) {
        Preferences prefs = new PreferencesImpl(context);
        Instant start = timeUtil.getStartOfDrinkDay(day, prefs);
        Instant end = start.plus(standardDays(1));
        clearDrinks(start, end);
    }

    @Override
    public void clearDrinks(Instant fromTime, Instant toTime) {
        LogUtil.INSTANCE.i(TAG, "Deleting drinks between %s and %s", fromTime, toTime);
        db.getDatabase().delete(TABLE_NAME, TIME_QUERY_WHERE,
                new String[]{sqlDateFormat.print(fromTime), sqlDateFormat.print(toTime)});
    }

    @Override
    public boolean deleteEvent(long index) {
        DBDataObject.enforceBackedObject(index);

        int deleted = db.getDatabase().delete(TABLE_NAME, getIndexClause(index), null);
        return deleted > 0;
    }

    @Override
    public double countPortions(Instant fromTime, Instant toTime) {
        LogUtil.INSTANCE.d(TAG, "Querying for portions between %s and %s", fromTime, toTime);

        Cursor cursor = db.getDatabase().query(TABLE_NAME,
                new String[]{"SUM(" + KEY_PORTIONS + ")"}, TIME_QUERY_WHERE,
                new String[]{sqlDateFormat.print(fromTime), sqlDateFormat.print(toTime)}, null,
                null, null);
        return getSingleDouble(cursor, 0);
    }

    @Override
    public double countTotalPortions() {
        LogUtil.INSTANCE.d(TAG, "Querying for total portions");

        Cursor cursor = db.getDatabase().query(TABLE_NAME,
                new String[]{"SUM(" + KEY_PORTIONS + ")"}, null, null, null, null, null);
        return getSingleDouble(cursor, 0);
    }

    @Override
    public void clearAll() {
        LogUtil.INSTANCE.i(TAG, "Clearing entire history database!");
        db.getDatabase().delete(TABLE_NAME, null, null);
    }

    @Override
    public void recalculatePortions() {
        Preferences prefs = new PreferencesImpl(context);
        double stdAlcWeight = prefs.getStandardDrinkAlcoholWeight();
        String updateS = "UPDATE " + tableName + " SET " + KEY_PORTIONS + " = ((((" + KEY_VOLUME + " * " + KEY_STRENGTH
                + ") / 100) * " + Common.ALCOHOL_LITER_WEIGHT + ") / " + stdAlcWeight + ")";
        LogUtil.INSTANCE.d(DBAdapter.TAG, "Running upgrade SQL: \"%s\"", updateS);
        db.getDatabase().execSQL(updateS);
    }

}
