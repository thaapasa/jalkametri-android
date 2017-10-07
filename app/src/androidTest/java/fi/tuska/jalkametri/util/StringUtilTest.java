package fi.tuska.jalkametri.util;

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
