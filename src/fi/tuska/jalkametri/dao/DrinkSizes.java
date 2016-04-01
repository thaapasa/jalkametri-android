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

public interface DrinkSizes extends DataLibrary {

    List<DrinkSize> getAllSizes();

    /**
     * C: create
     */
    DrinkSize createSize(String name, double size, String icon);
    DrinkSize createSize(String name, double size, String icon, int order);

    /**
     * R: read
     */
    DrinkSize getSize(long index);

    /**
     * U: update
     */
    boolean updateSize(long index, DrinkSize newSize);

    /**
     * D: delete
     */
    boolean deleteSize(long index);

    /**
     * Tries to find the given size from the list of drink sizes; return it if
     * found, null otherwise.
     */
    DrinkSize findSize(DrinkSize size);

    DrinkSize getDefaultSize();

    List<DrinkSize> getSizes(Drink drink);

}
