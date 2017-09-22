package fi.tuska.jalkametri.data;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import fi.tuska.jalkametri.DBActivity;
import fi.tuska.jalkametri.JalkametriWidget;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.activity.GUIActivity;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.drinks.DefaultDrinkDatabase;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.db.DrinkSizeConnectionDB;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.gui.TaskExecutor;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;

public final class DrinkActions {

    private static final String TAG = "DrinkActions";

    private DrinkActions() {
        // Prevent instantiation
    }

    /*
     * Drink library ----------------------------------------------------------
     */

    public static <T extends Activity & DBActivity & GUIActivity> void resetDrinkLibrary(final T parent,
        final Runnable onCompletion) {

        TaskExecutor.execute(parent, R.string.drink_library_reset, new Runnable() {
            @Override
            public void run() {
                DefaultDrinkDatabase.createDefaultDatabase(parent, parent.getDBAdapter(), true);
            }
        }, onCompletion);
    }

    public static void recalculateHistoryPortions(final DBAdapter adapter, final Context context,
        final Runnable onCompletion) {
        TaskExecutor.execute(context, R.string.prefs_recalculating_portions, new Runnable() {
            @Override
            public void run() {
                LogUtil.i(TAG, "Recalculating portions in history database");
                HistoryDB history = new HistoryDB(adapter, context);
                history.recalculatePortions();
            }
        }, onCompletion);
    }

    /*
     * Drinks ----------------------------------------------------------
     */

    /**
     * Creates a new drink into the drink library
     */
    public static void createDrink(DrinkCategory category, DrinkSelection selection, GUIActivity parent) {
        LogUtil.i(TAG, "Creating new drink %s", selection);
        Drink drink = selection.getDrink();
        DrinkSize[] sizes = new DrinkSize[1];
        sizes[0] = selection.getSize();
        Drink inserted = category.createDrink(drink.getName(), drink.getStrength(), drink.getIcon(), sizes);
        if (inserted == null)
            LogUtil.w(TAG, "New drink was not inserted!");
        if (parent != null) {
            parent.updateUI();
        }
    }

    /**
     * Deletes a drink from the drink library
     */
    public static void deleteDrink(DrinkCategory category, Drink drink, GUIActivity parent) {
        LogUtil.i(TAG, "Deleting drink %s", drink);
        category.deleteDrink(drink.getIndex());

        if (parent != null) {
            parent.updateUI();
        }
    }

    /**
     * Modifies an existing drink event in the history.
     */
    public static void updateDrink(DrinkCategory category, long originalID, Drink modifications, GUIActivity parent) {
        category.updateDrink(originalID, modifications);

        if (parent != null) {
            parent.updateUI();
        }
    }

    /*
     * Drink categories ----------------------------------------------------------
     */

    /**
     * Adds a new category into the drink library.
     */
    public static void createDrinkCategory(DrinkLibrary library, DrinkCategory category, GUIActivity parent) {
        LogUtil.i(TAG, "Adding category: %s", category);
        DrinkCategory inserted = library.createCategory(category.getName(), category.getIcon());
        if (inserted == null)
            LogUtil.w(TAG, "New category was not inserted!");
        if (parent != null) {
            parent.updateUI();
        }
    }

    /**
     * Modifies an existing drink category.
     */
    public static void updateDrinkCategory(DrinkLibrary library, long id, DrinkCategory category, GUIActivity parent) {
        LogUtil.i(TAG, "Editing category: %s (%d)", category, id);
        library.updateCategory(id, category);

        if (parent != null) {
            parent.updateUI();
        }
    }

    /**
     * Deletes the drink category.
     */
    public static void deleteDrinkCategory(DrinkLibrary library, DrinkCategory category, GUIActivity parent) {
        LogUtil.i(TAG, "Deleting drink category %s", category);
        boolean success = library.deleteCategory(category.getIndex());
        if (!success)
            LogUtil.w(TAG, "Category deleting failed!");

        if (parent != null) {
            parent.updateUI();
        }
    }

    /*
     * Drink sizes ----------------------------------------------------------
     */

    /**
     * Adds the given size to this drink.
     */
    public static <T extends GUIActivity & DBActivity> void addSizeToDrink(Drink drink, DrinkSize size, T parent) {
        LogUtil.d(TAG, "Adding size %s to drink %s", size, drink);

        DrinkSizeConnectionDB connDB = new DrinkSizeConnectionDB(parent.getDBAdapter());
        boolean res = connDB.addSize(drink, size);
        assert res;

        parent.updateUI();
    }

    /**
     * Removes the given size from this drink. If all the sizes are deleted, removes the size from the size database.
     */
    public static <T extends GUIActivity & DBActivity> void removeSizeFromDrink(Drink drink, DrinkSize size, T parent) {
        LogUtil.d(TAG, "Removing size %s from drink %s", size, drink);

        DrinkSizeConnectionDB connDB = new DrinkSizeConnectionDB(parent.getDBAdapter());
        boolean res = connDB.removeConnectionFromDrink(drink, size);
        assert res;

        parent.updateUI();
    }

    /**
     * Modifies a drink size in the drink library.
     */
    public static <T extends GUIActivity & DBActivity> void updateDrinkSize(long originalID, DrinkSize size, T parent) {
        LogUtil.d(TAG, "Updating size %s with id %d", size, originalID);

        DrinkSizes sizeLib = new DrinkLibraryDB(parent.getDBAdapter()).getDrinkSizes();
        boolean res = sizeLib.updateSize(originalID, size);
        assert res;

        parent.updateUI();
    }

    /*
     * Drink events (history) ----------------------------------------------------------
     */

    /**
     * Modifies an existing drink event in the history.
     */
    public static <T extends GUIActivity & DBActivity> void updateDrinkEvent(History history, long originalID,
        DrinkSelection modifications, T parent) {
        DrinkEvent event = history.getDrink(originalID);
        event.setDrink(modifications.getDrink());
        event.setSize(modifications.getSize());
        event.setTime(modifications.getTime());
        history.updateEvent(originalID, event);

        if (parent != null) {
            JalkametriWidget.triggerRecalculate(parent.getContext(), parent.getDBAdapter());
            parent.updateUI();
        }
    }

    /**
     * Deletes a drink event from the drink history.
     */
    public static <T extends GUIActivity & DBActivity> void deleteDrinkEvent(History history, DrinkEvent event, T parent) {
        history.deleteEvent(event.getIndex());

        if (parent != null) {
            JalkametriWidget.triggerRecalculate(parent.getContext(), parent.getDBAdapter());
            parent.updateUI();
        }
    }

    /**
     * Removes all drink events from a given day from the drink history.
     */
    public static <T extends GUIActivity & DBActivity> void clearEventsFromDay(History history, Date day, T parent) {
        LogUtil.d(TAG, "Clearing all drinks of %s", day);
        history.clearDay(day);

        if (parent != null) {
            JalkametriWidget.triggerRecalculate(parent.getContext(), parent.getDBAdapter());
            parent.updateUI();
        }
    }

    /**
     * Adds a drink for the currently selected day.
     */
    public static <T extends GUIActivity & DBActivity> void addDrinkForSelectedDay(History history,
        DrinkSelection drink, Date day, T parent) {
        TimeUtil timeUtil = new TimeUtil(parent);
        Calendar selCal = timeUtil.getCalendar(drink.getTime());
        Calendar curCal = timeUtil.getCalendar(day);
        selCal.set(Calendar.YEAR, curCal.get(Calendar.YEAR));
        // First set day of month to 1 so that the day is not too high if a
        // shorter month is currently selected. I'm not sure if this is
        // required (depends on when the sanity of the date settings is
        // enforced), but this can't be bad.
        selCal.set(Calendar.DAY_OF_MONTH, 1);
        selCal.set(Calendar.MONTH, curCal.get(Calendar.MONTH));
        selCal.set(Calendar.DAY_OF_MONTH, curCal.get(Calendar.DAY_OF_MONTH));

        // Update 16.5.2011: We must further check if the requested time is
        // before the day changing time. If it is, the recorded day must be
        // set one higher (you see), so that the drink will be shown on this
        // day's drink list.
        Preferences prefs = new PreferencesImpl(parent.getContext());
        if (timeUtil.isTimeBefore(selCal, prefs.getDayChangeHour(), prefs.getDayChangeMinute())) {
            // This time recording goes to the morning hours, so move it to
            // the next calendar day
            selCal.add(Calendar.DAY_OF_MONTH, 1);
        }

        drink.setTime(selCal.getTime());
        history.createDrink(drink);

        JalkametriWidget.triggerRecalculate(parent.getContext(), parent.getDBAdapter());
        parent.updateUI();
    }

}
