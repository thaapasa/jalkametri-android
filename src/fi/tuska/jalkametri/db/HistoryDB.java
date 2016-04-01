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
package fi.tuska.jalkametri.db;

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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;

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
            Date time = selection.getTime();
            assert time != null;
            values.put(KEY_TIME, sqlDateFormat.format(time));
        }
        // Portions
        values.put(KEY_PORTIONS, selection.getPortions(context));
    }

    @Override
    public void createDrink(DrinkSelection selection) {
        ContentValues newValues = new ContentValues();
        createValues(newValues, selection);
        long id = adapter.getDatabase().insert(TABLE_NAME, null, newValues);
        assert id >= 0;
    }

    @Override
    public void updateEvent(long index, DrinkEvent event) {
        DBDataObject.enforceBackedObject(index);

        ContentValues newValues = new ContentValues();
        createValues(newValues, event);
        adapter.getDatabase().update(TABLE_NAME, newValues, getIndexClause(index), null);
    }

    @Override
    public List<DrinkEvent> getDrinks(Date day, boolean ascending) {
        Preferences prefs = new PreferencesImpl(context);
        Calendar start = timeUtil.getStartOfDay(day, prefs);
        Calendar end = timeUtil.getCalendar(start);
        end.add(Calendar.DAY_OF_MONTH, 1);
        return getDrinks(start.getTime(), end.getTime(), ascending);
    }

    private static final String[] DRINK_QUERY_COLUMNS = new String[] { KEY_ID, KEY_NAME,
        KEY_STRENGTH, KEY_VOLUME, KEY_SIZE_NAME, KEY_ICON, KEY_COMMENT, KEY_TIME };
    private static final String TIME_QUERY_WHERE = KEY_TIME + " >= ? AND " + KEY_TIME + " < ?";

    private static final String[] PREVIOUS_QUERY_COLUMNS = new String[] { KEY_ID, KEY_NAME,
        KEY_STRENGTH, KEY_VOLUME, KEY_SIZE_NAME, KEY_ICON, KEY_COMMENT, KEY_TIME,
        COLUMN_GROUP_NAME };

    @Override
    public List<DrinkEvent> getDrinks(Date fromTime, Date toTime, boolean ascending) {
        LogUtil.d(TAG, "Querying for drinks between %s and %s", fromTime, toTime);

        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, DRINK_QUERY_COLUMNS,
            TIME_QUERY_WHERE,
            new String[] { sqlDateFormat.format(fromTime), sqlDateFormat.format(toTime) }, null,
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
        LogUtil.d(TAG, "Querying for previous drinks");

        Cursor cursor = adapter.getDatabase().query(false, TABLE_NAME, PREVIOUS_QUERY_COLUMNS,
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
        LogUtil.d(TAG, "Querying for drink %d", index);

        Cursor cursor = adapter.getDatabase().query(true, TABLE_NAME, DRINK_QUERY_COLUMNS,
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

        Date drinkTime = null;
        try {
            drinkTime = sqlDateFormat.parse(time);
        } catch (ParseException e) {
            LogUtil.w(TAG, e.getMessage());
        }

        return new DrinkEvent(eventId, drink, size, drinkTime);
    }

    @Override
    public void clearDay(Date day) {
        Preferences prefs = new PreferencesImpl(context);
        Calendar start = timeUtil.getStartOfDay(day, prefs);
        Calendar end = timeUtil.getCalendar(start);
        end.add(Calendar.DAY_OF_MONTH, 1);
        clearDrinks(start.getTime(), end.getTime());
    }

    @Override
    public void clearDrinks(Date fromTime, Date toTime) {
        LogUtil.i(TAG, "Deleting drinks between %s and %s", fromTime, toTime);
        adapter.getDatabase().delete(TABLE_NAME, TIME_QUERY_WHERE,
            new String[] { sqlDateFormat.format(fromTime), sqlDateFormat.format(toTime) });
    }

    @Override
    public boolean deleteEvent(long index) {
        DBDataObject.enforceBackedObject(index);

        int deleted = adapter.getDatabase().delete(TABLE_NAME, getIndexClause(index), null);
        return deleted > 0;
    }

    @Override
    public double countPortions(Date fromTime, Date toTime) {
        LogUtil.d(TAG, "Querying for portions between %s and %s", fromTime, toTime);

        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { "SUM(" + KEY_PORTIONS + ")" }, TIME_QUERY_WHERE,
            new String[] { sqlDateFormat.format(fromTime), sqlDateFormat.format(toTime) }, null,
            null, null);
        return getSingleDouble(cursor, 0);
    }

    @Override
    public double countTotalPortions() {
        LogUtil.d(TAG, "Querying for total portions");

        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { "SUM(" + KEY_PORTIONS + ")" }, null, null, null, null, null);
        return getSingleDouble(cursor, 0);
    }

    @Override
    public void clearAll() {
        LogUtil.i(TAG, "Clearing entire history database!");
        adapter.getDatabase().delete(TABLE_NAME, null, null);
    }

    @Override
    public void recalculatePortions() {
        Preferences prefs = new PreferencesImpl(context);
        double stdAlcWeight = prefs.getStandardDrinkAlcoholWeight();
        String updateS = "UPDATE " + tableName + " SET " + KEY_PORTIONS + " = ((((" + KEY_VOLUME + " * " + KEY_STRENGTH
            + ") / 100) * " + Common.ALCOHOL_LITER_WEIGHT + ") / " + stdAlcWeight + ")";
        LogUtil.d(DBAdapter.TAG, "Running upgrade SQL: \"%s\"", updateS);
        adapter.getDatabase().execSQL(updateS);
    }

}
