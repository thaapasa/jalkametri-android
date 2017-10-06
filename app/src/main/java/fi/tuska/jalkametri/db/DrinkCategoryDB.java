package fi.tuska.jalkametri.db;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;

import java.util.ArrayList;
import java.util.List;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_CATEGORY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_COMMENT;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ICON;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

public class DrinkCategoryDB extends AbstractDB implements DrinkCategory {

    private static final long serialVersionUID = -1170053131030356479L;

    public static final String TABLE_NAME = "drinks";
    private static final String KEY_STRENGTH = "strength";

    public static final String SQL_CREATE_TABLE_DRINKS_1 = "CREATE TABLE " + TABLE_NAME
            + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "name TEXT NOT NULL, "
            + "cat_id INTEGER NOT NULL REFERENCES categories (id), " + "strength FLOAT NOT NULL, "
            + "icon TEXT NOT NULL, " + "pos INTEGER NOT NULL);";
    public static final String SQL_CREATE_TABLE_DRINKS_2 = "CREATE TABLE " + TABLE_NAME
            + " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + "name TEXT NOT NULL, "
            + "cat_id INTEGER NOT NULL REFERENCES categories (id), " + "strength FLOAT NOT NULL, "
            + "icon TEXT NOT NULL, " + "comment TEXT NOT NULL DEFAULT '', "
            + "pos INTEGER NOT NULL);";

    private final long index;
    private String name;
    private String icon;
    private final DrinkSizes sizes;
    private final DrinkSizeConnectionDB sizeConn;

    // private static final String COLUMN_NUM_SIZES = "(SELECT COUNT(*) FROM "
    // + DrinkSizeConnectionDB.TABLE_NAME + " WHERE drink_id = drinks.id)";

    public DrinkCategoryDB(DBAdapter adapter, DrinkSizes sizes, long index, String name,
                           String icon) {
        super(adapter, TABLE_NAME);
        this.index = index;
        this.icon = icon;
        this.name = name;
        this.sizes = sizes;
        this.sizeConn = new DrinkSizeConnectionDB(adapter);
        DBDataObject.enforceBackedObject(index);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public String toString() {
        return name + " (" + icon + ")";
    }

    @Override
    public Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes) {
        int newOrder = getLargestOrderNumber() + 1;
        return createDrink(name, strength, icon, sizes, newOrder);
    }

    @Override
    public Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes,
                             int order) {
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_CATEGORY_ID, getIndex());
        newValues.put(KEY_STRENGTH, strength);
        newValues.put(KEY_ICON, icon);
        newValues.put(KEY_ORDER, order);
        long id = adapter.getDatabase().insert(TABLE_NAME, null, newValues);
        if (id < 0)
            return null;

        Drink drink = getDrink(id);
        for (DrinkSize size : sizes) {
            boolean res = sizeConn.addSize(drink, size);
            assert res;
        }
        return drink;
    }

    @Override
    public boolean updateDrink(long index, Drink drinkInfo) {
        DBDataObject.enforceBackedObject(index);
        ContentValues newValues = new ContentValues();
        DrinkSelectionHelper.createCommonValues(newValues, drinkInfo);

        int updated = adapter.getDatabase().update(TABLE_NAME, newValues, getIndexClause(index),
                null);
        assert updated <= 1;
        return updated > 0;
    }

    @Override
    public boolean deleteDrink(long index) {
        // Check that the object to-be-deleted is a database-backed object
        DBDataObject.enforceBackedObject(index);

        Drink drink = getDrink(index);
        DrinkSizeConnectionDB connDB = new DrinkSizeConnectionDB(adapter);
        connDB.deleteConnectionsForDrink(drink);
        int modified = adapter.getDatabase().delete(TABLE_NAME, DBAdapter.ID_WHERE_CLAUSE,
                new String[]{String.valueOf(index)});

        assert modified <= 1;
        return modified > 0;
    }

    @Override
    public Drink getDrink(long index) {
        // Check that the given index is a valid ID
        DBDataObject.enforceBackedObject(index);

        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
                new String[]{KEY_NAME, KEY_STRENGTH, KEY_ICON, KEY_COMMENT, KEY_ORDER},
                KEY_ID + " = " + index + " AND " + KEY_CATEGORY_ID + " = " + getIndex(), null, null,
                null, KEY_ORDER);

        Drink drink = null;
        if (cursor.moveToFirst()) {
            int c = -1;
            drink = createDrink(index, cursor.getString(++c), cursor.getDouble(++c),
                    cursor.getString(++c), cursor.getString(++c));
        }
        cursor.close();
        return drink;
    }

    @Override
    public List<Drink> getDrinks() {
        Cursor cursor = adapter.getDatabase().query(TABLE_NAME,
                new String[]{KEY_ID, KEY_NAME, KEY_STRENGTH, KEY_ICON, KEY_COMMENT, KEY_ORDER},
                KEY_CATEGORY_ID + " = " + getIndex(), null, null, null, KEY_ORDER);
        int count = cursor.getCount();
        List<Drink> list = new ArrayList<Drink>(count);
        if (cursor.moveToFirst()) {
            do {
                int c = -1;
                Drink drink = createDrink(cursor.getLong(++c), cursor.getString(++c),
                        cursor.getDouble(++c), cursor.getString(++c), cursor.getString(++c));
                list.add(drink);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    private Drink createDrink(long index, String name, double strength, String icon, String comment) {
        List<DrinkSize> drinkSizes = sizeConn.getDrinkSizes(index, sizes);
        return new Drink(index, getIndex(), name, strength, icon, comment, drinkSizes);
    }

    @Override
    public String getIconText(Resources res) {
        return name;
    }

    @Override
    public boolean isBacked() {
        assert DBDataObject.isValidID(index);
        return true;
    }

}
