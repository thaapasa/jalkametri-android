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
package fi.tuska.jalkametri.dao;

import java.util.List;

import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;

public interface DrinkCategory extends NamedIcon, DataObject {

    /**
     * C: create
     */
    Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes);
    Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes, int order);

    /**
     * R: read
     */
    Drink getDrink(long index);

    /**
     * U: update
     */
    boolean updateDrink(long index, Drink drinkInfo);

    /**
     * D: delete
     */
    boolean deleteDrink(long index);

    List<Drink> getDrinks();

    void setName(String name);

    void setIcon(String icon);

}
