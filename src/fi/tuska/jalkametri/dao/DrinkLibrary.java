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

public interface DrinkLibrary extends DataLibrary {

    /**
     * C: create
     * 
     * Adds a new category to the drink library.
     * 
     * @return the new category; or null, if an error occurred
     */
    DrinkCategory createCategory(String name, String icon);

    /**
     * R: read
     */
    DrinkCategory getCategory(long index);

    /**
     * U: update
     */
    boolean updateCategory(long id, DrinkCategory category);

    /**
     * D: delete
     */
    boolean deleteCategory(long id);

    /**
     * @return the drink categories (e.g., a category might be for beers,
     * spirits, wines, etc.)
     */
    List<DrinkCategory> getCategories();

    DrinkSizes getDrinkSizes();

    void clearDrinksSizesCategories();

}
