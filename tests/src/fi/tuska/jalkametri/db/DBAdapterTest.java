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

import fi.tuska.jalkametri.db.DBAdapter;
import fi.tuska.jalkametri.test.JalkametriTestCase;

public class DBAdapterTest extends JalkametriTestCase {

    public void testSQLTimeFormat() {
        assertEquals("00:00", DBAdapter.formatAsSQLTime(0, 0));
        assertEquals("05:00", DBAdapter.formatAsSQLTime(5, 0));
        assertEquals("15:00", DBAdapter.formatAsSQLTime(15, 0));
        assertEquals("00:01", DBAdapter.formatAsSQLTime(0, 1));
        assertEquals("05:08", DBAdapter.formatAsSQLTime(5, 8));
        assertEquals("23:46", DBAdapter.formatAsSQLTime(23, 46));
        assertEquals("123:146", DBAdapter.formatAsSQLTime(123, 146));
    }

}
