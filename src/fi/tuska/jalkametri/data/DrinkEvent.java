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

import java.util.Date;

import fi.tuska.jalkametri.dao.DataObject;
import fi.tuska.jalkametri.db.DBDataObject;

public class DrinkEvent extends DrinkSelection implements DataObject {

    private static final long serialVersionUID = -9146835260562696879L;

    private final long index;

    public DrinkEvent(long index, Drink drink, DrinkSize size, Date time) {
        super(drink, size, time);
        this.index = index;
        assert index != 0;
        DBDataObject.enforceBackedObject(index);
    }

    public DrinkEvent(Drink drink, DrinkSize size, Date time) {
        super(drink, size, time);
        this.index = DBDataObject.getInvalidID();
    }

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return getIconText(null);
    }

    @Override
    public boolean isBacked() {
        return DBDataObject.isValidID(index);
    }

}
