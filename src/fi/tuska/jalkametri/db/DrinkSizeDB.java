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

import static fi.tuska.jalkametri.db.DBAdapter.KEY_ICON;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;

public class DrinkSizeDB extends AbstractDB implements DrinkSizes {

    public static final String SQL_CREATE_TABLE_SIZES = "CREATE TABLE sizes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
        + "name TEXT NOT NULL, "
        + "volume FLOAT NOT NULL, "
        + "icon TEXT NOT NULL, "
        + "pos INTEGER NOT NULL);";

    public static final String TABLE_NAME = "sizes";
    private static final String KEY_VOLUME = "volume";

    private Map<Long, DrinkSize> sizeMap;
    private List<DrinkSize> sizeList;

    public DrinkSizeDB(DBAdapter adapter) {
        super(adapter, TABLE_NAME);
    }

    /**
     * C: create
     */
    @Override
    public DrinkSize createSize(String name, double volume, String icon) {
        int newOrder = getLargestOrderNumber() + 1;
        return createSize(name, volume, icon, newOrder);
    }

    /**
     * C: create
     */
    @Override
    public DrinkSize createSize(String name, double volume, String icon, int order) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_VOLUME, volume);
        newValues.put(KEY_ICON, icon);
        newValues.put(KEY_ORDER, order);
        long id = adapter.getDatabase().insert(TABLE_NAME, null, newValues);
        if (id < 0) {
            return null;
        }

        if (sizeList == null) {
            // Sizes are not initialized; initialize now
            loadSizes();
            return getSize(id);
        } else {
            // Just load this size from DB. This should make initial drink
            // library creation faster.
            DrinkSize size = loadSize(id);
            assert size != null;
            // And manually add it to the size list
            sizeList.add(size);
            sizeMap.put(size.getIndex(), size);
            return size;
        }
    }

    /**
     * R: read
     */
    @Override
    public DrinkSize getSize(long index) {
        DBDataObject.enforceBackedObject(index);

        if (sizeList == null) {
            loadSizes();
        }
        return sizeMap.get(index);
    }

    /**
     * U: update
     */
    @Override
    public boolean updateSize(long index, DrinkSize size) {
        DBDataObject.enforceBackedObject(index);

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, size.getName());
        newValues.put(KEY_VOLUME, size.getVolume());
        newValues.put(KEY_ICON, size.getIcon());

        int updated = adapter.getDatabase().update(TABLE_NAME, newValues, getIndexClause(index),
            null);
        assert updated <= 1;
        return updated > 0;
    }

    /**
     * D: delete
     */
    @Override
    public boolean deleteSize(long index) {
        DBDataObject.enforceBackedObject(index);

        int deleted = adapter.getDatabase().delete(TABLE_NAME, getIndexClause(index), null);
        assert deleted <= 1;
        return deleted > 0;
    }

    private void loadSizes() {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { KEY_ID, KEY_NAME, KEY_VOLUME, KEY_ICON, KEY_ORDER }, null, null, null,
            null, KEY_VOLUME);
        int count = cursor.getCount();
        sizeList = new ArrayList<DrinkSize>(count);
        sizeMap = new HashMap<Long, DrinkSize>();
        if (cursor.moveToFirst()) {
            do {
                DrinkSize size = createDrinkSize(cursor.getLong(0), cursor.getString(1),
                    cursor.getDouble(2), cursor.getString(3));
                sizeList.add(size);
                sizeMap.put(size.getIndex(), size);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private DrinkSize loadSize(long id) {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
            new String[] { KEY_ID, KEY_NAME, KEY_VOLUME, KEY_ICON, KEY_ORDER },
            getIndexClause(id), null, null, null, KEY_VOLUME);

        DrinkSize size = null;
        if (cursor.moveToFirst()) {
            size = createDrinkSize(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2),
                cursor.getString(3));
        }
        cursor.close();
        return size;
    }

    /**
     * Tries to find the given size from the list of drink sizes; return it if
     * found, null otherwise.
     */
    @Override
    public DrinkSize findSize(DrinkSize size) {
        if (size == null) {
            return null;
        }
        if (sizeList == null) {
            loadSizes();
        }
        for (DrinkSize dbSize : sizeList) {
            if (size.equals(dbSize))
                return dbSize;
        }
        return null;
    }

    @Override
    public DrinkSize getDefaultSize() {
        if (sizeList == null) {
            loadSizes();
        }
        if (sizeList.size() == 0)
            return new DrinkSize();
        return sizeList.get(0);
    }

    @Override
    public List<DrinkSize> getAllSizes() {
        if (sizeList == null) {
            loadSizes();
        }
        return sizeList;
    }

    private DrinkSize createDrinkSize(long index, String name, double volume, String icon) {
        return new DrinkSize(index, name, volume, icon);
    }

    @Override
    public void invalidate() {
        sizeList = null;
        sizeMap = null;
    }

    @Override
    public List<DrinkSize> getSizes(Drink drink) {
        DBDataObject.enforceBackedObject(drink);
        DrinkSizeConnectionDB conn = new DrinkSizeConnectionDB(adapter);
        return conn.getDrinkSizes(drink, this);
    }

}
