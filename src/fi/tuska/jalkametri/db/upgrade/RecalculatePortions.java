/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with jAlkaMetri (LICENSE.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri.db.upgrade;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_PORTIONS;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_STRENGTH;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_VOLUME;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.data.PreferencesImpl;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * Recalculates the portions stored in database. This is required when updating between certain versions of the database
 * (when the portion information was added to the database tables).
 * 
 * @author Tuukka Haapasalo
 */
public class RecalculatePortions implements DBUpgrader {

    private static final String[] ALL_TABLES = { HistoryDB.TABLE_NAME };
    private final String[] modifyTables;

    public RecalculatePortions() {
        this.modifyTables = ALL_TABLES;
    }

    public RecalculatePortions(String... tables) {
        this.modifyTables = tables;
    }

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        Preferences prefs = new PreferencesImpl(context);
        double stdAlcWeight = prefs.getStandardDrinkAlcoholWeight();
        for (String tableName : modifyTables) {
            String updateS = "UPDATE " + tableName + " SET " + KEY_PORTIONS + " = ((((" + KEY_VOLUME + " * "
                + KEY_STRENGTH + ") / 100) * " + Common.ALCOHOL_LITER_WEIGHT + ") / " + stdAlcWeight + ")";
            LogUtil.d(DBAdapter.TAG, "Running upgrade SQL: \"%s\"", updateS);
            db.execSQL(updateS);
        }
    }

}
