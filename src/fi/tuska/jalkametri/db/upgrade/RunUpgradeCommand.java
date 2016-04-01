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
import android.database.sqlite.SQLiteDatabase;
import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * A class that allows the DB upgrade process to run custom SQL commands to
 * update the database schema and/or contents.
 * 
 * @author Tuukka Haapasalo
 */
public class RunUpgradeCommand implements DBUpgrader {

    private final String[] updateCmds;

    public RunUpgradeCommand(String... updateCmds) {
        this.updateCmds = updateCmds;
    }

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        for (String updateS : updateCmds) {
            LogUtil.d(DBAdapter.TAG, "Running upgrade SQL: \"%s\"", updateS);
            db.execSQL(updateS);
        }
    }

    @Override
    public String toString() {
        return "DB upgrader [running " + updateCmds.length + " commands]";
    }

}
