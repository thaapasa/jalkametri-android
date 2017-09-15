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

import java.io.Serializable;

import fi.tuska.jalkametri.dao.DataObject;

public abstract class DBDataObject implements DataObject, Serializable {

    private static final long serialVersionUID = 7481036354555634797L;

    private static final long INVALID_ID = -1;

    private final long index;

    protected DBDataObject() {
        index = INVALID_ID;
    }

    protected DBDataObject(long index) {
        this.index = index;
        assert index != 0;
        enforceBackedObject(index);
    }

    @Override
    public final long getIndex() {
        return index;
    }

    /**
     * @return true if this data object is backed to a back-end storage (e.g.,
     * into a database.
     */
    @Override
    public boolean isBacked() {
        return index != INVALID_ID;
    }

    public static long getInvalidID() {
        return INVALID_ID;
    }

    public static boolean isValidID(long index) {
        return index != INVALID_ID;
    }

    /**
     * Checks that the given index is valid for an object that is backed to
     * back-end storage.
     * 
     * @param index the object index
     * @throws IllegalArgumentException if the index does not belong to a
     * backed object
     */
    public static void enforceBackedObject(long index) {
        if (index == INVALID_ID)
            throw new IllegalArgumentException(
                "Object identifier is invalid; object is not backed to database");
    }

    /**
     * Checks that the given object is backed to back-end storage.
     * 
     * @param object the object to check
     * @throws IllegalArgumentException if the object is not backed
     */
    public static void enforceBackedObject(DataObject object) {
        if (!object.isBacked())
            throw new IllegalArgumentException(
                "Object identifier is invalid; object is not backed to database");
    }

}
