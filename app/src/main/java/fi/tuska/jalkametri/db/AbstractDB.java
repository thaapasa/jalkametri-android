package fi.tuska.jalkametri.db;

import android.database.Cursor;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.DataObject;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

public abstract class AbstractDB {

    private final Locale DB_LOCALE = Locale.ENGLISH;
    private final DateTimeZone DB_ZONE = DateTimeZone.UTC;
    final DateTimeFormatter sqlDateFormat = DateTimeFormat.forPattern(Common.SQL_DATE_FORMAT_STR).withLocale(DB_LOCALE).withZone(DB_ZONE);

    protected final DBAdapter adapter;
    final String tableName;

    AbstractDB(DBAdapter adapter, String tableName) {
        this.adapter = adapter;
        this.tableName = tableName;
    }

    int getLargestOrderNumber() {
        Cursor cursor = adapter.getDatabase().query(tableName, new String[]{KEY_ORDER}, null,
                null, null, null, KEY_ORDER + " DESC");
        return getSingleInt(cursor, 0);
    }

    String getIndexClause(long index) {
        StringBuilder b = new StringBuilder(KEY_ID);
        b.append(" = ");
        b.append(index);
        return b.toString();
    }

    public String getIndexClause(DataObject object) {
        StringBuilder b = new StringBuilder(KEY_ID);
        b.append(" = ");
        b.append(object.getIndex());
        return b.toString();
    }

    /**
     * @return the single double value returned by the query (the first column
     * of the first row); or the given default value, if nothing is returned
     * by the query. Always closes the cursor.
     */
    double getSingleDouble(Cursor cursor, double defaultValue) {
        double value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getDouble(0);
        }
        cursor.close();
        return value;
    }

    /**
     * @return the single int value returned by the query (the first column of
     * the first row); or the given default value, if nothing is returned by
     * the query. Always closes the cursor.
     */
    int getSingleInt(Cursor cursor, int defaultValue) {
        int value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        return value;
    }

    /**
     * @return the single long value returned by the query (the first column
     * of the first row); or the given default value, if nothing is returned
     * by the query. Always closes the cursor.
     */
    long getSingleLong(Cursor cursor, long defaultValue) {
        long value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getInt(0);
        }
        cursor.close();
        return value;
    }

    /**
     * @return the single string value returned by the query (the first column
     * of the first row); or the given default value, if nothing is returned
     * by the query. Always closes the cursor.
     */
    String getSingleString(Cursor cursor, String defaultValue) {
        String value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    String[] getIndexValues(DataObject... objects) {
        String[] res = new String[objects.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = String.valueOf(objects[i].getIndex());
        }
        return res;
    }

}
