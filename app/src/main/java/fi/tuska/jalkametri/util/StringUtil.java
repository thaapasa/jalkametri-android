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
 * Utility functions for handling strings.
 * 
 * @author Tuukka Haapasalo
 */
public final class StringUtil {

    private StringUtil() {
        // No instantiation required
    }

    /**
     * Converts the first letter in the string to upper case; e.g.,
     * "toodledyDoo" becomes "ToodledyDoo".
     * 
     * @param str the string
     * @return the string with upper-cased first letter
     */
    public static String uppercaseFirstLetter(String str) {
        if (str == null)
            return null;
        if (str.length() <= 1)
            return str.toUpperCase();
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
