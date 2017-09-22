package fi.tuska.jalkametri.db;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.database.Cursor;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.DataObject;

public abstract class AbstractDB {

    protected final DBAdapter adapter;
    protected final String tableName;

    protected final DateFormat sqlDateFormat = new SimpleDateFormat(Common.SQL_DATE_FORMAT_STR);

    protected AbstractDB(DBAdapter adapter, String tableName) {
        this.adapter = adapter;
        this.tableName = tableName;
    }

    protected int getLargestOrderNumber() {
        Cursor cursor = adapter.getDatabase().query(tableName, new String[] { KEY_ORDER }, null,
            null, null, null, KEY_ORDER + " DESC");
        return getSingleInt(cursor, 0);
    }

    public String getIndexClause(long index) {
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
    protected double getSingleDouble(Cursor cursor, double defaultValue) {
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
    protected int getSingleInt(Cursor cursor, int defaultValue) {
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
    protected long getSingleLong(Cursor cursor, long defaultValue) {
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
    protected String getSingleString(Cursor cursor, String defaultValue) {
        String value = defaultValue;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    protected String[] getIndexValues(DataObject... objects) {
        String[] res = new String[objects.length];
        for (int i = 0; i < res.length; ++i) {
            res[i] = String.valueOf(objects[i].getIndex());
        }
        return res;
    }

}
