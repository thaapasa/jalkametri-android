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

import java.io.File;

import fi.tuska.jalkametri.test.JalkametriTestCase;

/**
 * Unit tests for the time utility functions.
 * 
 * @author Tuukka Haapasalo
 */
public class FileUtilTest extends JalkametriTestCase {

    private File getDirectory() {
        File dir = new File(getContext().getCacheDir(), "test");
        if (!dir.exists())
            dir.mkdir();
        return dir;
    }

    public void testFileHandling() {
        File a = new File(getDirectory(), "a.tmp");
        String testData1 = "Moi äÄåÅ!%$";
        assertTrue(FileUtil.writeUTF8ToFileSafe(testData1, a));
        String read = FileUtil.readUTF8FromFileSafe(a);
        assertNotNull(read);
        assertEquals(testData1, read);

        File b = new File(getDirectory(), "b.tmp");
        assertTrue(FileUtil.copyFileSafe(a, b));

        String testData2 = "Kikkeliskoo€ plipplop";
        assertTrue(FileUtil.writeUTF8ToFileSafe(testData2, a));
        read = FileUtil.readUTF8FromFileSafe(a);
        assertNotNull(read);
        assertEquals(testData2, read);

        read = FileUtil.readUTF8FromFileSafe(b);
        assertNotNull(read);
        assertEquals(testData1, read);
    }

}
