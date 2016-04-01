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

import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;

public class DrinkSizeConnectionDB extends AbstractDB {

    public static final String SQL_CREATE_TABLE_DRINKS_SIZES = "CREATE TABLE drinks_sizes (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
        + "drink_id INTEGER NOT NULL REFERENCES drinks (id), "
        + "size_id INTEGER NOT NULL REFERENCES sizes (id), " + "pos INTEGER NOT NULL);";

    public static final String TABLE_NAME = "drinks_sizes";
    private static final String KEY_DRINK_ID = "drink_id";
    private static final String KEY_SIZE_ID = "size_id";

    private static final String DRINK_ID_WHERE_CLAUSE = KEY_DRINK_ID + " = ?";

    public DrinkSizeConnectionDB(DBAdapter adapter) {
        super(adapter, TABLE_NAME);
    }

    /**
     * Adds the connection from the drink to this size.
     * 
     * @return true if the size was added (or already existed); false if the
     * insertion failed.
     */
    public boolean addSize(Drink drink, DrinkSize size) {
        DBDataObject.enforceBackedObject(drink);
        if (!size.isBacked()) {
            // Try to find a matching size from DB
            DrinkSizeDB sizeDB = new DrinkSizeDB(adapter);
            DrinkSize foundSize = sizeDB.findSize(size);
            if (foundSize != null) {
                size = foundSize;
            } else {
                size = sizeDB.createSize(size.getName(), size.getVolume(), size.getIcon());
            }
            assert size != null;
        }
        DBDataObject.enforceBackedObject(size);

        if (hasSize(drink, size))
            return true;

        int newOrder = getLargestOrderNumber() + 1;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_DRINK_ID, drink.getIndex());
        newValues.put(KEY_SIZE_ID, size.getIndex());
        newValues.put(KEY_ORDER, newOrder);

        long id = adapter.getDatabase().insert(TABLE_NAME, null, newValues);
        return id >= 0;
    }

    private static final String REMOVE_QUERY_WHERE = KEY_DRINK_ID + " = ? AND " + KEY_SIZE_ID
        + " = ?";

    /**
     * Removes the connection from the drink.
     * 
     * If the size is not used in any drink any more, deletes the size from
     * the database.
     */
    public boolean removeConnectionFromDrink(Drink drink, DrinkSize size) {
        DBDataObject.enforceBackedObject(drink);
        DBDataObject.enforceBackedObject(size);

        int deleted = adapter.getDatabase().delete(TABLE_NAME, REMOVE_QUERY_WHERE,
            new String[] { String.valueOf(drink.getIndex()), String.valueOf(size.getIndex()) });
        assert deleted <= 1;

        if (deleted > 0) {
            // Check if there are any connections left for the drink size
            int count = getNumberOfUses(size);
            if (count == 0) {
                // This drink size is not used anymore
                DrinkSizes sizeDB = new DrinkSizeDB(adapter);
                boolean res = sizeDB.deleteSize(size.getIndex());
                assert res;
            }
        }

        return deleted > 0;
    }

    private int getNumberOfUses(DrinkSize size) {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, new String[] { "COUNT(*)" },
            KEY_SIZE_ID + " = " + size.getIndex(), null, null, null, null);

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public boolean deleteConnectionsForDrink(Drink drink) {
        DBDataObject.enforceBackedObject(drink);

        adapter.getDatabase().delete(TABLE_NAME, DRINK_ID_WHERE_CLAUSE,
            new String[] { String.valueOf(drink.getIndex()) });
        return true;
    }

    public List<DrinkSize> getDrinkSizes(Drink drink, DrinkSizes sizeDB) {
        DBDataObject.enforceBackedObject(drink);

        return getDrinkSizes(drink.getIndex(), sizeDB);
    }

    public List<DrinkSize> getDrinkSizes(long drinkID, DrinkSizes sizeDB) {
        DBDataObject.enforceBackedObject(drinkID);

        List<DrinkSize> sizes = new ArrayList<DrinkSize>();

        Cursor cursor = adapter.getDatabase().query(tableName, new String[] { KEY_SIZE_ID },
            KEY_DRINK_ID + " = " + drinkID, null, null, null, KEY_ORDER);

        if (cursor.moveToFirst()) {
            do {
                long sizeID = cursor.getLong(0);
                DrinkSize size = sizeDB.getSize(sizeID);
                assert size != null;
                sizes.add(size);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return sizes;
    }

    private static final String FIND_DRINK_SIZE_CONNECTION_SELECTION = KEY_DRINK_ID + " = ? AND "
        + KEY_SIZE_ID + " = ?";

    public boolean hasSize(Drink drink, DrinkSize size) {
        DBDataObject.enforceBackedObject(drink);
        DBDataObject.enforceBackedObject(size);

        Cursor cursor = adapter.getDatabase().query(TABLE_NAME, new String[] { "COUNT(*)" },
            FIND_DRINK_SIZE_CONNECTION_SELECTION, getIndexValues(drink, size), null, null, null);
        int count = getSingleInt(cursor, 0);
        assert count <= 1;
        return count > 0;
    }

    protected int getLargestOrderNumber(Drink drink) {
        DBDataObject.enforceBackedObject(drink);

        Cursor cursor = adapter.getDatabase().query(tableName, new String[] { KEY_ORDER },
            KEY_DRINK_ID + " = " + drink.getIndex(), null, null, null, KEY_ORDER);
        return getSingleInt(cursor, 0);
    }

}
