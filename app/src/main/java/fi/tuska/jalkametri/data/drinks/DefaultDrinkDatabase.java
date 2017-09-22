package fi.tuska.jalkametri.data.drinks;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.util.LogUtil;

public final class DefaultDrinkDatabase {

    private static final String TAG = "DefaultDrinkDatabase";
    private static final Map<String, DefaultDrinkLibrary> DEFAULT_LIBRARIES = new HashMap<String, DefaultDrinkLibrary>();

    static {
        DEFAULT_LIBRARIES.put("fi", new FinnishDrinkLibrary());
        DEFAULT_LIBRARIES.put("en", new EnglishDrinkLibrary());
    }

    private DefaultDrinkDatabase() {
        // Private constructor to prevent instantiation
    }

    public static void createDefaultDatabase(Context context, DBAdapter adapter,
        boolean clearExisting) {

        Preferences prefs = new PreferencesImpl(context);
        Locale locale = prefs.getLocale();
        DefaultDrinkLibrary defLib = DEFAULT_LIBRARIES.get(locale.getLanguage());
        assert defLib != null;

        LogUtil.i(TAG, "Starting to create default drink library");
        long startTime = System.currentTimeMillis();

        // Reset the library in a single transaction
        adapter.beginTransaction();
        try {
            DrinkLibrary drinkLibrary = new DrinkLibraryDB(adapter);
            if (clearExisting) {
                drinkLibrary.clearDrinksSizesCategories();
            }
            defLib.createDefaultDrinks(drinkLibrary);
            adapter.setTransactionSuccessful();
        } finally {
            // Finish the transaction
            adapter.endTransaction();
        }

        long timeElapsed = System.currentTimeMillis() - startTime;
        LogUtil.i(TAG, "Default drink library created; this took %d ms", timeElapsed);
    }
}
