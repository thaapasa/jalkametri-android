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
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * An action that drops the old tables and creates new ones. Used when an
 * older version is installed on top of a new version (this should only happen
 * during debugging/testing).
 * 
 * @author Tuukka Haapasalo
 */
public class DropAndCreate extends DBCreator implements DBUpgrader {

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        LogUtil.d(DBAdapter.TAG, "Dropping tables");
        Resources res = context.getResources();
        // Drop tables
        String[] tables = res.getStringArray(R.array.database_tables);
        for (String tableName : tables) {
            String updateS = "DROP TABLE IF EXISTS " + tableName;
            LogUtil.d(DBAdapter.TAG, updateS);
            db.execSQL(updateS);
        }

        // Re-create tables
        super.updateDB(context, db, fromVersion, toVersion);
    }

    @Override
    public String toString() {
        return "DB drop-creator [drops tables and re-creates them]";
    }

}
