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
package fi.tuska.jalkametri.db;

import static fi.tuska.jalkametri.db.DBAdapter.KEY_COMMENT;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_ICON;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_SIZE_NAME;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_STRENGTH;
import static fi.tuska.jalkametri.db.DBAdapter.KEY_VOLUME;
import android.content.ContentValues;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;

public class DrinkSelectionHelper {

    public static void createCommonValues(ContentValues values, Drink drink) {
        assert drink != null;
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
            assert size != null;
            values.put(KEY_VOLUME, size.getVolume());
            values.put(KEY_SIZE_NAME, size.getName());
        }
    }

}
