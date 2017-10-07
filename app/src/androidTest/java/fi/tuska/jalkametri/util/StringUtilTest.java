package fi.tuska.jalkametri.util;

import android.test.AndroidTestCase;

/**
 * Unit tests for the string handling utilities.
 *
 * @author Tuukka Haapasalo
 */
public class StringUtilTest extends AndroidTestCase {

    public void testUppercaseFirstLetter() {
        assertEquals(null, StringUtil.INSTANCE.uppercaseFirstLetter(null));
        assertEquals("Moro", StringUtil.INSTANCE.uppercaseFirstLetter("moro"));
        assertEquals("Moro", StringUtil.INSTANCE.uppercaseFirstLetter("Moro"));
        assertEquals("MORO", StringUtil.INSTANCE.uppercaseFirstLetter("MORO"));
        assertEquals("MORO", StringUtil.INSTANCE.uppercaseFirstLetter("mORO"));
    }

}
