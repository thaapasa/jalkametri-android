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
package fi.tuska.jalkametri.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;

public class DrinkSupport {

    public static final Drink getBeer() {
        List<DrinkSize> sizes = new ArrayList<DrinkSize>();
        sizes.add(getPint());
        Drink beer = new Drink("Beer", 4.6d, "drink_beer_pint", "Beer is good", sizes);
        return beer;
    }

    public static final DrinkSize getPint() {
        DrinkSize pint = new DrinkSize("Pint", 0.568d, "size_pint_pint");
        return pint;
    }

    public static final DrinkSelection getBeerSelection(Date time) {
        DrinkSelection sel = new DrinkSelection(getBeer(), getPint(), time);
        return sel;
    }

}
