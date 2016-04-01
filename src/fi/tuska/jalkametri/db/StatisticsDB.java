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

import static fi.tuska.jalkametri.db.DBAdapter.KEY_PORTIONS;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_TIME;
import static fi.tuska.jalkametri.db.DBAdapter.TAG;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.dao.GeneralStatistics;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.dao.Statistics;
import fi.tuska.jalkametri.data.DailyDrinkStatisticsImpl;
import fi.tuska.jalkametri.data.GeneralStatisticsImpl;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;

public class StatisticsDB extends AbstractDB implements Statistics {

    public static final String TABLE_NAME = HistoryDB.TABLE_NAME;

    private static final String COL_COUNT = "COUNT(*)";
    private static final String COL_PORTIONS = "SUM(" + KEY_PORTIONS + ")";
    private static final String COL_DRUNKDAYS = "DISTINCT(TRIM(SUBSTR(DATETIME(" + KEY_TIME
        + ", '-%s'), 0, 11))) AS thedate";
    private static final String COL_THEDATE = "TRIM(SUBSTR(DATETIME(" + KEY_TIME
        + ", '-%s'), 0, 11)) AS thedate";
    private static final String KEY_THEDATE = "thedate";

    private final Context context;
    private final Preferences prefs;
    private final TimeUtil timeUtil;

    public StatisticsDB(DBAdapter adapter, Preferences prefs, Context context) {
        super(adapter, TABLE_NAME);
        this.prefs = prefs;
        this.context = context;
        this.timeUtil = new TimeUtil(context);
    }

    /**
     * Loads the general statistics from the database.
     * 
     * @return the general statistics implementation
     */
    @Override
    public GeneralStatistics getGeneralStatistics() {
        return getGeneralStatistics(null, null);
    }

    @Override
    public GeneralStatistics getGeneralStatistics(Date start, Date end) {

        // Wrap all the queries in a single transaction
        adapter.beginTransaction();
        try {
            // First day
            GeneralStatisticsImpl stats = new GeneralStatisticsImpl(start, end,
                getFirstDrinkEventTime(start), context);
            stats.setTotalDrinks(getNumberOfDrinkEvents(start, end));
            stats.setTotalPortions(getNumberOfPortions(start, end));
            stats.setDrunkDays(getNumberOfDrunkDays(start, end));
            adapter.setTransactionSuccessful();
            return stats;
        } finally {
            adapter.endTransaction();
        }
    }

    @Override
    public List<DailyDrinkStatistics> getDailyDrinkAmounts(Date start, Date end) {
        String colSpec = getTheDateGroupColumnSpec();
        LogUtil.d(TAG, "Querying for daily drink amounts between %s and %s; colSpec is %s",
            start, end, colSpec);
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { colSpec, "SUM(portions)", "COUNT(*)" },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end),
            KEY_THEDATE, null, KEY_THEDATE + " ASC");
        List<DailyDrinkStatistics> res = new ArrayList<DailyDrinkStatistics>(cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                DailyDrinkStatistics stats = new DailyDrinkStatisticsImpl(cursor.getString(0),
                    cursor.getDouble(1), cursor.getInt(2), context);
                res.add(stats);
                // LogUtil.d(TAG, "Stats: " + stats);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return res;
    }

    private Date getFirstDrinkEventTime(Date start) {
        Date sysStart = getFirstDrinkEventTime();
        return (start != null && sysStart.getTime() < start.getTime()) ? start : sysStart;
    }

    /**
     * @return a valid date; either the date of the first drink event; or the
     * current time, if no drinks have been recorded
     */
    @Override
    public Date getFirstDrinkEventTime() {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, new String[] { KEY_TIME }, null,
            null, null, null, KEY_TIME + " ASC", "1");
        String date = getSingleString(cursor, null);
        try {
            return date != null ? sqlDateFormat.parse(date) : new Date();
        } catch (ParseException e) {
            LogUtil.w(TAG, "Invalid date value in database: %s", date);
            return new Date();
        }
    }

    private long getNumberOfDrinkEvents(Date start, Date end) {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { COL_COUNT, getTheDateGroupColumnSpec() },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end), null,
            null, null);
        return getSingleLong(cursor, 0);
    }

    private double getNumberOfPortions(Date start, Date end) {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { COL_PORTIONS, getTheDateGroupColumnSpec() },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end), null,
            null, null);
        return getSingleDouble(cursor, 0);
    }

    private int getNumberOfDrunkDays(Date start, Date end) {
        String colSpec = getTimeGroupColumnSpec();
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, new String[] { colSpec },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end), null,
            null, null);
        int res = cursor.getCount();
        cursor.close();
        return res;
    }

    protected String getTimeGroupColumnSpec() {
        return String.format(COL_DRUNKDAYS,
            DBAdapter.formatAsSQLTime(prefs.getDayChangeHour(), prefs.getDayChangeMinute()));
    }

    protected String getTheDateGroupColumnSpec() {
        return String.format(COL_THEDATE,
            DBAdapter.formatAsSQLTime(prefs.getDayChangeHour(), prefs.getDayChangeMinute()));
    }

    protected String getDateSelection(String timeCol, Date start, Date end) {
        if (start == null && end == null)
            return null;
        if (end == null)
            return timeCol + " >= ?";
        if (start == null)
            return timeCol + " <= ?";
        return timeCol + " >= ? AND " + timeCol + " <= ?";
    }

    protected String[] getDateSelectionArgs(Date start, Date end) {
        if (start == null && end == null)
            return null;
        if (end == null)
            return new String[] { timeUtil.toSQLDate(start) };
        if (start == null)
            return new String[] { timeUtil.toSQLDate(end) };
        return new String[] { timeUtil.toSQLDate(start), timeUtil.toSQLDate(end) };
    }
}
