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
import fi.tuska.jalkametri.util.CollectionUtils;

/**
 * A class that allows DB upgrading to run multiple commands.
 * 
 * @author Tuukka Haapasalo
 */
public class RunMultipleCommands implements DBUpgrader {

    private final DBUpgrader[] upgraders;

    public RunMultipleCommands(DBUpgrader... upgraders) {
        this.upgraders = upgraders;
    }

    @Override
    public void updateDB(Context context, SQLiteDatabase db, int fromVersion, int toVersion) {
        for (DBUpgrader upgrader : upgraders) {
            upgrader.updateDB(context, db, fromVersion, toVersion);
        }
    }

    @Override
    public String toString() {
        return "Multiple commands: [" + CollectionUtils.implodeArray(upgraders, ", ") + "]";
    }

}
