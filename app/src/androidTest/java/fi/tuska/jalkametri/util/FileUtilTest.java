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
