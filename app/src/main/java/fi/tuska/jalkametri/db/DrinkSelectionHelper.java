package fi.tuska.jalkametri.db;

import android.content.ContentValues;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.util.AssertionUtils;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_COMMENT;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ICON;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_SIZE_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_STRENGTH;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_VOLUME;

public class DrinkSelectionHelper {

    public static void createCommonValues(ContentValues values, Drink drink) {
        AssertionUtils.INSTANCE.expect(drink != null);
        values.put(KEY_NAME, drink.getName());
        values.put(KEY_STRENGTH, drink.getStrength());
        values.put(KEY_ICON, drink.getIcon());
        values.put(KEY_COMMENT, drink.getComment());
    }

    public static void createCommonValues(ContentValues values, DrinkSelection selection) {
        {
            Drink drink = selection.getDrink();
            createCommonValues(values, drink);
        }
        {
            DrinkSize size = selection.getSize();
            AssertionUtils.INSTANCE.expect(size != null);
            values.put(KEY_VOLUME, size.getVolume());
            values.put(KEY_SIZE_NAME, size.getName());
        }
    }

}
