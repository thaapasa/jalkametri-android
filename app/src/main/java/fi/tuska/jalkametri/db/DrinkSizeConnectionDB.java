package fi.tuska.jalkametri.db;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.util.AssertionUtils;

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
            DrinkSizeDB sizeDB = new DrinkSizeDB(db);
            DrinkSize foundSize = sizeDB.findSize(size);
            if (foundSize != null) {
                size = foundSize;
            } else {
                size = sizeDB.createSize(size.getName(), size.getVolume(), size.getIcon());
            }
            AssertionUtils.INSTANCE.expect(size != null);
        }
        DBDataObject.enforceBackedObject(size);

        if (hasSize(drink, size))
            return true;

        int newOrder = getLargestOrderNumber() + 1;

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_DRINK_ID, drink.getIndex());
        newValues.put(KEY_SIZE_ID, size.getIndex());
        newValues.put(KEY_ORDER, newOrder);

        long id = db.getDatabase().insert(TABLE_NAME, null, newValues);
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

        int deleted = db.getDatabase().delete(TABLE_NAME, REMOVE_QUERY_WHERE,
            new String[] { String.valueOf(drink.getIndex()), String.valueOf(size.getIndex()) });
        AssertionUtils.INSTANCE.expect(deleted <= 1);

        if (deleted > 0) {
            // Check if there are any connections left for the drink size
            int count = getNumberOfUses(size);
            if (count == 0) {
                // This drink size is not used anymore
                DrinkSizes sizeDB = new DrinkSizeDB(db);
                boolean res = sizeDB.deleteSize(size.getIndex());
                AssertionUtils.INSTANCE.expect(res);
            }
        }

        return deleted > 0;
    }

    private int getNumberOfUses(DrinkSize size) {
        Cursor cursor = db.getDatabase().query(TABLE_NAME, new String[] { "COUNT(*)" },
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

        db.getDatabase().delete(TABLE_NAME, DRINK_ID_WHERE_CLAUSE,
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

        Cursor cursor = db.getDatabase().query(tableName, new String[] { KEY_SIZE_ID },
            KEY_DRINK_ID + " = " + drinkID, null, null, null, KEY_ORDER);

        if (cursor.moveToFirst()) {
            do {
                long sizeID = cursor.getLong(0);
                DrinkSize size = sizeDB.getSize(sizeID);
                AssertionUtils.INSTANCE.expect(size != null);
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

        Cursor cursor = db.getDatabase().query(TABLE_NAME, new String[] { "COUNT(*)" },
            FIND_DRINK_SIZE_CONNECTION_SELECTION, getIndexValues(drink, size), null, null, null);
        int count = getSingleInt(cursor, 0);
        AssertionUtils.INSTANCE.expect(count <= 1);
        return count > 0;
    }

    protected int getLargestOrderNumber(Drink drink) {
        DBDataObject.enforceBackedObject(drink);

        Cursor cursor = db.getDatabase().query(tableName, new String[] { KEY_ORDER },
            KEY_DRINK_ID + " = " + drink.getIndex(), null, null, null, KEY_ORDER);
        return getSingleInt(cursor, 0);
    }

}
