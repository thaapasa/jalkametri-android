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

import fi.tuska.jalkametri.util.StringUtil;
import android.test.AndroidTestCase;

/**
 * Unit tests for the string handling utilities.
 * 
 * @author Tuukka Haapasalo
 */
public class StringUtilTest extends AndroidTestCase {

    public void testUppercaseFirstLetter() {
        assertEquals(null, StringUtil.uppercaseFirstLetter(null));
        assertEquals("Moro", StringUtil.uppercaseFirstLetter("moro"));
        assertEquals("Moro", StringUtil.uppercaseFirstLetter("Moro"));
        assertEquals("MORO", StringUtil.uppercaseFirstLetter("MORO"));
        assertEquals("MORO", StringUtil.uppercaseFirstLetter("mORO"));
    }

}
