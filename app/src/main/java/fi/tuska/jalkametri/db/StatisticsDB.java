package fi.tuska.jalkametri.db;

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
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_PORTIONS;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_TIME;
import static fi.tuska.jalkametri.db.DBAdapter.TAG;
import static org.joda.time.Instant.now;

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
    public GeneralStatistics getGeneralStatistics(LocalDate startDay, LocalDate endDay) {

        Instant start = startDay != null ? timeUtil.getStartOfDrinkDay(startDay, prefs) : null;
        Instant end = endDay != null ? timeUtil.getStartOfDrinkDay(endDay, prefs) : null;

        // Wrap all the queries in a single transaction
        adapter.beginTransaction();
        try {
            // First day
            GeneralStatisticsImpl stats = new GeneralStatisticsImpl(startDay, endDay,
                getFirstDrinkEventDate(startDay), context);
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
    public List<DailyDrinkStatistics> getDailyDrinkAmounts(LocalDate startDay, LocalDate endDay) {
        Instant start = timeUtil.getStartOfDrinkDay(startDay, prefs);
        Instant end = timeUtil.getStartOfDrinkDay(endDay, prefs);
        String colSpec = getTheDateGroupColumnSpec();
        LogUtil.INSTANCE.d(TAG, "Querying for daily drink amounts between %s and %s; colSpec is %s",
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

    public LocalDate getFirstDrinkEventDate(LocalDate start) {
        LocalDate dbDate = getFirstDrinkEventTime().toDateTime(timeUtil.getTimeZone()).toLocalDate();
        return start != null && start.isBefore(dbDate) ? start : dbDate;
    }

    /**
     * @return a valid date; either the date of the first drink event; or the
     * current time, if no drinks have been recorded
     */
    @Override
    public Instant getFirstDrinkEventTime() {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, new String[] { KEY_TIME }, null,
            null, null, null, KEY_TIME + " ASC", "1");
        String date = getSingleString(cursor, null);
        try {
            return date != null ? Instant.parse(date, sqlDateFormat) : now();
        } catch (Exception e) {
            LogUtil.INSTANCE.w(TAG, "Invalid date value in database: %s", date);
            return now();
        }
    }

    private long getNumberOfDrinkEvents(Instant start, Instant end) {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { COL_COUNT, getTheDateGroupColumnSpec() },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end), null,
            null, null);
        return getSingleLong(cursor, 0);
    }

    private double getNumberOfPortions(Instant start, Instant end) {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { COL_PORTIONS, getTheDateGroupColumnSpec() },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end), null,
            null, null);
        return getSingleDouble(cursor, 0);
    }

    private int getNumberOfDrunkDays(Instant start, Instant end) {
        String colSpec = getTimeGroupColumnSpec();
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, new String[] { colSpec },
            getDateSelection(KEY_THEDATE, start, end), getDateSelectionArgs(start, end), null,
            null, null);
        int res = cursor.getCount();
        cursor.close();
        return res;
    }

    protected LocalTime getDBTimeOffset() {
        return prefs.getDayChangeTime();
    }

    protected String getTimeGroupColumnSpec() {
        return String.format(Locale.ENGLISH, COL_DRUNKDAYS,
            DBAdapter.formatAsSQLTime(getDBTimeOffset()));
    }

    protected String getTheDateGroupColumnSpec() {
        return String.format(Locale.ENGLISH, COL_THEDATE,
            DBAdapter.formatAsSQLTime(getDBTimeOffset()));
    }

    protected String getDateSelection(String timeCol, Instant start, Instant end) {
        if (start == null && end == null)
            return null;
        if (end == null)
            return timeCol + " >= ?";
        if (start == null)
            return timeCol + " <= ?";
        return timeCol + " >= ? AND " + timeCol + " <= ?";
    }

    protected String[] getDateSelectionArgs(Instant start, Instant end) {
        if (start == null && end == null)
            return null;
        if (end == null)
            return new String[] { timeUtil.toSQLDate(start) };
        if (start == null)
            return new String[] { timeUtil.toSQLDate(end) };
        return new String[] { timeUtil.toSQLDate(start), timeUtil.toSQLDate(end) };
    }
}
