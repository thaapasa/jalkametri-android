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
package fi.tuska.jalkametri.db.upgrade;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.DrinkCategoryDB;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.db.DrinkSizeConnectionDB;
import fi.tuska.jalkametri.db.DrinkSizeDB;
import fi.tuska.jalkametri.db.FavouritesDB;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * This class is used to create the databases, when no old installation of
 * jAlkaMetri exists.
 * 
 * @author Tuukka Haapasalo
 */
public class DBCreator implements DBUpgrader {

    private static final String[] OLD_TABLES = new String[] {
        DrinkLibraryDB.SQL_CREATE_TABLE_CATEGORIES, DrinkCategoryDB.SQL_CREATE_TABLE_DRINKS_1,
        DrinkSizeDB.SQL_CREATE_TABLE_SIZES, DrinkSizeConnectionDB.SQL_CREATE_TABLE_DRINKS_SIZES,
        HistoryDB.SQL_CREATE_TABLE_HISTORY_1 };

    private static final String[] NEW_TABLES = new String[] {
        DrinkLibraryDB.SQL_CREATE_TABLE_CATEGORIES, DrinkCategoryDB.SQL_CREATE_TABLE_DRINKS_2,
        DrinkSizeDB.SQL_CREATE_TABLE_SIZES, DrinkSizeConnectionDB.SQL_CREATE_TABLE_DRINKS_SIZES,
        HistoryDB.SQL_CREATE_TABLE_HISTORY_2, HistoryDB.SQL_CREATE_HISTORY_INDEX,
        FavouritesDB.SQL_CREATE_TABLE_FAVOURITES_2 };

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        if (fromVersion != 0) {
            LogUtil.d(DBAdapter.TAG, "Creating old version DB tables from scratch");
            runUpdates(OLD_TABLES, db);
        } else {
            LogUtil.d(DBAdapter.TAG, "Creating new version DB tables from scratch");
            runUpdates(NEW_TABLES, db);
        }

        clearLibraryInitialization(context);
    }

    private void runUpdates(String[] updates, SQLiteDatabase db) {
        for (String createSQL : updates) {
            LogUtil.d(DBAdapter.TAG, createSQL);
            db.execSQL(createSQL);
        }
    }

    @Override
    public String toString() {
        return "DB creator [creates tables from scratch]";
    }

    private void clearLibraryInitialization(Context context) {
        LogUtil.d(DBAdapter.TAG, "Clearing drink library initialization");
        Preferences prefs = new PreferencesImpl(context);
        Editor editor = prefs.edit();
        prefs.setDrinkLibraryInitialized(editor, false);
        editor.commit();
    }

}
