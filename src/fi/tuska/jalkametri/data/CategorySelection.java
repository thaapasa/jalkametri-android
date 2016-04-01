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

import java.io.Serializable;
import java.util.List;

import android.content.res.Resources;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.db.DBDataObject;

/**
 * Basic data-holding implementation for drink categories (for editing, before
 * information is stored to DB).
 * 
 * @author Tuukka Haapasalo
 */
public class CategorySelection extends DBDataObject implements DrinkCategory, Serializable {

    private static final long serialVersionUID = -8375927023878631885L;

    private String name = "";
    private String icon = Common.DEFAULT_ICON_NAME;

    public CategorySelection() {
        super();
    }

    public CategorySelection(DrinkCategory category) {
        super();
        name = category.getName();
        icon = category.getIcon();
    }

    @Override
    public String getIconText(Resources res) {
        return icon;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes) {
        throw new UnsupportedOperationException(
            "addDrink() not supported for the category selection type");
    }

    @Override
    public Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes, int order) {
        throw new UnsupportedOperationException(
            "addDrink() not supported for the category selection type");
    }

    @Override
    public Drink getDrink(long index) {
        throw new UnsupportedOperationException(
            "getDrink() not supported for the category selection type");
    }

    @Override
    public List<Drink> getDrinks() {
        throw new UnsupportedOperationException(
            "getDrinks() not supported for the category selection type");
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return String.format("Category %s, icon %s", name, icon);
    }

    @Override
    public boolean deleteDrink(long index) {
        throw new UnsupportedOperationException(
            "deleteDrink() not supported for the category selection type");
    }

    @Override
    public boolean updateDrink(long index, Drink drinkInfo) {
        throw new UnsupportedOperationException(
            "updateDrink() not supported for the category selection type");
    }

}
