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

import fi.tuska.jalkametri.test.JalkametriTestCase;
import fi.tuska.jalkametri.util.LocalizationUtil;

/**
 * Unit tests for the time utility functions.
 * 
 * @author Tuukka Haapasalo
 */
public class LocalizationUtilTest extends JalkametriTestCase {

    public void testLocaleSwitching() {
        // Hard-coded check that default locale is English; change this test
        // if default locale is to be changed
        assertEquals("en", LocalizationUtil.DEFAULT_LOCALE.getLanguage());

        // Check that current locale is the default
        assertEquals(LocalizationUtil.DEFAULT_LOCALE, getCurrentLocale());

        switchLocale(LocalizationUtil.LOCALE_EN);
        assertEquals(LocalizationUtil.LOCALE_EN, getCurrentLocale());

        switchLocale(LocalizationUtil.LOCALE_FI);
        assertEquals(LocalizationUtil.LOCALE_FI, getCurrentLocale());

        switchLocale(LocalizationUtil.LOCALE_EN);
        assertEquals(LocalizationUtil.LOCALE_EN, getCurrentLocale());
    }

}
