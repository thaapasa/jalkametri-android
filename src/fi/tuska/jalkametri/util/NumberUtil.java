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

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import android.content.res.Resources;

public final class NumberUtil {

    private NumberUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convenience method for parsing a double; defaults to 0 if the number
     * could not be parsed.
     */
    public static double readDouble(String value, Locale locale) {
        try {
            // Use the English number formatter for reading the number
            NumberFormat f = NumberFormat.getNumberInstance(Locale.ENGLISH);
            // Change the Finnish separator characters to English ones
            String valStr = value.replace(',', '.');
            // Should now accept both Finnish and English decimal separator
            // chars
            return f.parse(valStr).doubleValue();
        } catch (ParseException e) {
            return 0d;
        }
    }

    public static String toString(int value, Resources res) {
        NumberFormat f = new TimeUtil(res).getNumberFormat();
        return f.format(value);
    }

    public static String toString(double value, Resources res) {
        NumberFormat f = new TimeUtil(res).getNumberFormat();
        return f.format(value);
    }

}
