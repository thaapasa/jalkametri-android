package fi.tuska.jalkametri.db;

import android.content.ContentValues;
import android.database.Cursor;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.util.AssertionUtils;

import java.util.ArrayList;
import java.util.List;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_ICON;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ID;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ORDER;

public class DrinkLibraryDB extends AbstractDB implements DrinkLibrary {

    public static final String TABLE_NAME = "categories";

    public static final String SQL_CREATE_TABLE_CATEGORIES = "CREATE TABLE categories (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + "name TEXT NOT NULL, " + "icon TEXT NOT NULL, " + "pos INTEGER NOT NULL);";

    private DrinkSizes sizes;

    public DrinkLibraryDB(DBAdapter db) {
        super(db, TABLE_NAME);
        sizes = new DrinkSizeDB(db);
    }

    @Override
    public List<DrinkCategory> getCategories() {
        Cursor cursor = db.getDatabase().query(TABLE_NAME,
                new String[]{KEY_ID, KEY_NAME, KEY_ICON, KEY_ORDER}, null, null, null, null,
                KEY_ORDER);
        int count = cursor.getCount();
        List<DrinkCategory> list = new ArrayList<DrinkCategory>(count);
        if (cursor.moveToFirst()) {
            do {
                list.add(createCategory(cursor.getLong(0), cursor.getString(1),
                        cursor.getString(2)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    @Override
    public DrinkCategory getCategory(long index) {
        // Check that the given index is a valid ID
        DBDataObject.enforceBackedObject(index);

        Cursor cursor = db.getDatabase().query(TABLE_NAME,
                new String[]{KEY_NAME, KEY_ICON, KEY_ORDER}, getIndexClause(index), null, null,
                null, KEY_ORDER);

        DrinkCategory cat = null;
        if (cursor.moveToFirst()) {
            cat = createCategory(index, cursor.getString(0), cursor.getString(1));
        }
        cursor.close();
        return cat;
    }

    private DrinkCategoryDB createCategory(long index, String name, String icon) {
        return new DrinkCategoryDB(db, sizes, index, name, icon);
    }

    @Override
    public DrinkCategory createCategory(String name, String icon) {
        int newOrder = getLargestOrderNumber() + 1;
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, name);
        newValues.put(KEY_ICON, icon);
        newValues.put(KEY_ORDER, newOrder);
        long id = db.getDatabase().insert(TABLE_NAME, null, newValues);
        return id >= 0 ? getCategory(id) : null;
    }

    @Override
    public void invalidate() {
        // No cached data to invalidate
    }

    @Override
    public DrinkSizes getDrinkSizes() {
        return sizes;
    }

    @Override
    public void clearDrinksSizesCategories() {
        db.getDatabase().delete(DrinkSizeConnectionDB.TABLE_NAME, null, null);
        db.getDatabase().delete(DrinkSizeDB.TABLE_NAME, null, null);
        db.getDatabase().delete(DrinkCategoryDB.TABLE_NAME, null, null);
        db.getDatabase().delete(TABLE_NAME, null, null);
        sizes.invalidate();
    }

    @Override
    public boolean updateCategory(long id, DrinkCategory category) {
        // Check that the given index is a valid ID
        DBDataObject.enforceBackedObject(id);

        ContentValues newValues = new ContentValues();
        newValues.put(KEY_NAME, category.getName());
        newValues.put(KEY_ICON, category.getIcon());
        int affected = db.getDatabase().update(TABLE_NAME, newValues, getIndexClause(id),
                null);
        AssertionUtils.INSTANCE.expect(affected <= 1);
        return affected > 0;
    }

    @Override
    public boolean deleteCategory(long id) {
        // Check that the given index is a valid ID
        DBDataObject.enforceBackedObject(id);

        int affected = db.getDatabase().delete(TABLE_NAME, getIndexClause(id), null);
        AssertionUtils.INSTANCE.expect(affected <= 1);
        return affected > 0;
    }

}
