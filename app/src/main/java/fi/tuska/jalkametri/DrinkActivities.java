package fi.tuska.jalkametri;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;
import fi.tuska.jalkametri.activity.EditCategoryActivity;
import fi.tuska.jalkametri.activity.EditDrinkDetailsActivity;
import fi.tuska.jalkametri.activity.EditDrinkSizeActivity;
import fi.tuska.jalkametri.activity.SelectDrinkCategoryActivity;
import fi.tuska.jalkametri.activity.SelectDrinkSizeActivity;
import fi.tuska.jalkametri.activity.SelectSizeForDrinkActivity;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.CategorySelection;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.media.ToastLibrary;
import fi.tuska.jalkametri.util.AssertionUtils;
import fi.tuska.jalkametri.util.LogUtil;

import static fi.tuska.jalkametri.Common.KEY_ORIGINAL;
import static fi.tuska.jalkametri.Common.KEY_RESULT;
import static org.joda.time.Instant.now;

/**
 * Helper functions for setting up drink selection, modification, and so on.
 *
 * @author Tuukka Haapasalo
 */
public final class DrinkActivities {

    private static final String TAG = "DrinkActions";

    private DrinkActivities() {
        // No instantiation required
    }

    /**
     * Start an activity for selecting a drink.
     */
    public static int startSelectDrink(Activity parent) {
        return startSelectDrink(parent, Common.ACTIVITY_CODE_SELECT_DRINK);
    }

    /**
     * Start an activity for selecting a drink.
     */
    public static int startSelectDrink(Activity parent, int code) {
        Intent i = new Intent(parent, SelectDrinkCategoryActivity.class);
        parent.startActivityForResult(i, code);
        return code;
    }

    /**
     * Start an activity for selecting the drink size for the given drink.
     */
    public static int startSelectDrinkSize(Activity parent, Drink drink) {
        AssertionUtils.INSTANCE.expect(drink.isBacked());
        DBDataObject.enforceBackedObject(drink);

        Intent i = new Intent(parent, SelectDrinkSizeActivity.class);
        // Prepare the intent for selecting a drink size for the given
        // drink
        SelectDrinkSizeActivity.prepareForDrink(i, drink);
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_SELECT_DRINK_SIZE);
        return Common.ACTIVITY_CODE_SELECT_DRINK_SIZE;
    }

    /**
     * Start an activity for selecting a drink size to add to this drink.
     */
    public static int startAddDrinkSize(Activity parent) {
        Intent i = new Intent(parent, SelectSizeForDrinkActivity.class);
        // Prepare the intent for selecting a drink size for the given
        // drink
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_ADD_DRINK_SIZE);
        return Common.ACTIVITY_CODE_ADD_DRINK_SIZE;
    }

    /**
     * Start an activity for modifying a drink size in the drink library.
     */
    public static int startModifyDrinkSize(Activity parent, DrinkSize size) {
        Intent i = new Intent(parent, EditDrinkSizeActivity.class);
        // Prepare the intent for selecting a drink size for the given
        // drink
        EditDrinkSizeActivity.prepareForDrinkSizeEdit(i, size);
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_MODIFY_DRINK_SIZE);
        return Common.ACTIVITY_CODE_MODIFY_DRINK_SIZE;
    }

    /**
     * This preserves the original drinking time of the event.
     */
    public static int startModifyDrinkEvent(Activity parent, DrinkEvent drink,
                                            boolean showTimeSelection) {
        return startModifyDrinkEvent(parent, drink, showTimeSelection, true, false,
                Common.ACTIVITY_CODE_MODIFY_DRINK_EVENT);
    }

    public static int startCreateDrink(Activity parent, DrinkSizes sizes) {
        DrinkSize size = sizes.getDefaultSize();
        DrinkEvent basis = new DrinkEvent(new Drink(), size, now());
        return startModifyDrinkEvent(parent, basis, false, true, true,
                Common.ACTIVITY_CODE_CREATE_DRINK);
    }

    /**
     * This preserves the original drinking time of the event.
     */
    public static int startModifyDrinkEvent(Activity parent, DrinkEvent drink,
                                            boolean showTimeSelection, boolean showSizeSelector, boolean showSizeIconEdit, int code) {

        // Create and prepare and intent for modifying the selected drink
        // event
        Intent i = new Intent(parent, EditDrinkDetailsActivity.class);
        EditDrinkDetailsActivity.Companion.prepareForDrinkEventModification(parent, i, drink,
                showTimeSelection, showSizeSelector, showSizeIconEdit);
        parent.startActivityForResult(i, code);
        return code;
    }

    public static int startModifyDrink(Activity parent, Drink drink) {
        DBDataObject.enforceBackedObject(drink);

        // Create and prepare an intent for modifying the drink
        Intent i = new Intent(parent, EditDrinkDetailsActivity.class);
        EditDrinkDetailsActivity.Companion.prepareForDrinkModification(parent, i, drink);
        parent.startActivityForResult(i, Common.ACTIVITY_CODE_MODIFY_DRINK);
        return Common.ACTIVITY_CODE_MODIFY_DRINK;
    }

    /**
     * This will update the drinking time to the current time.
     */
    public static int startSelectDrinkDetails(Activity parent, DrinkSelection drink) {
        return startSelectDrinkDetails(parent, drink, Common.ACTIVITY_CODE_SELECT_DRINK_DETAILS);
    }

    /**
     * This will update the drinking time to the current time.
     */
    public static int startSelectDrinkDetails(Activity parent, DrinkSelection originalDrink,
                                              int code) {
        Intent i = new Intent(parent, EditDrinkDetailsActivity.class);
        // Create a copy of the drink selection with updated time
        DrinkSelection drink = new DrinkSelection(originalDrink.getDrink(),
                originalDrink.getSize(), now());
        // Prepare the intent for showing the selected category
        if (EditDrinkDetailsActivity.Companion.prepareForDrinkSelection(parent, i, drink)) {
            parent.startActivityForResult(i, code);
            return code;
        }
        return 0;
    }

    public static Intent createDrinkSelectionResult(DrinkSelection drink, Long originalID) {
        Intent data = new Intent();
        data.putExtra(KEY_RESULT, drink);
        if (originalID != null) {
            data.putExtra(KEY_ORIGINAL, originalID);
        }
        return data;
    }

    public static DrinkSelection getDrinkSelectionFromResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null)
            return null;
        DrinkSelection sel = (DrinkSelection) extras.get(KEY_RESULT);
        return sel;
    }

    public static int startAddCategory(Activity parent) {
        Intent i = new Intent(parent, EditCategoryActivity.class);
        // Prepare the intent for showing the selected category
        if (EditCategoryActivity.prepareForCategoryAdd(parent, i)) {
            parent.startActivityForResult(i, Common.ACTIVITY_CODE_ADD_CATEGORY);
            return Common.ACTIVITY_CODE_ADD_CATEGORY;
        }
        return 0;
    }

    public static int startModifyCategory(Activity parent, DrinkCategory category) {
        Intent i = new Intent(parent, EditCategoryActivity.class);
        // Prepare the intent for showing the selected category
        if (EditCategoryActivity.prepareForCategoryModification(parent, i, category)) {
            parent.startActivityForResult(i, Common.ACTIVITY_CODE_EDIT_CATEGORY);
            return Common.ACTIVITY_CODE_EDIT_CATEGORY;
        }
        return 0;
    }

    public static Intent createCategoryResult(CategorySelection category, Long originalID) {
        Intent data = new Intent();
        data.putExtra(KEY_RESULT, category);
        if (originalID != null) {
            data.putExtra(KEY_ORIGINAL, originalID);
        }
        return data;
    }

    public static CategorySelection getCategoryFromResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null)
            return null;
        CategorySelection category = (CategorySelection) extras.get(KEY_RESULT);
        return category;
    }

    public static void makeDrinkToast(Context context, double alcoholLevel, boolean isDrinking) {
        Resources res = context.getResources();
        Preferences prefs = new PreferencesImpl(context);
        double percentage = alcoholLevel / prefs.getMaxAlcoholLevel();
        LogUtil.INSTANCE.d(TAG, "Getting toast for %d %%", ((int) (percentage * 100)));
        Pair<Integer, Integer> toast = ToastLibrary.getToast(percentage, isDrinking);
        Toast.makeText(context, res.getString(toast.first), Toast.LENGTH_SHORT).show();
    }

    public static Intent createDrinkSizeResult(DrinkSize size, Long originalID) {
        Intent data = new Intent();
        data.putExtra(KEY_RESULT, size);
        if (originalID != null) {
            data.putExtra(KEY_ORIGINAL, originalID);
        }
        return data;
    }

    public static DrinkSize getDrinkSizeFromResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null)
            return null;
        DrinkSize size = (DrinkSize) extras.get(KEY_RESULT);
        return size;
    }

}
