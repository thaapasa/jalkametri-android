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
package fi.tuska.jalkametri.util;

/**
 * Common utilities.
 * 
 * @author Tuukka Haapasalo
 */
public final class CommonUtil {

    private CommonUtil() {
        // Prevent instantiation
    }

    /**
     * Check if the given objects are either both null or equal to each
     * other (as per equals()).
     */
    public static <T> boolean nullOrEquals(T o1, T o2) {
        if (o1 == null)
            return o2 == null;
        if (o2 == null)
            return false;
        return o1.equals(o2);
    }

}
