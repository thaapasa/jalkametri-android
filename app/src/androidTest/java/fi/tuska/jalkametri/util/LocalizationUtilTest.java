package fi.tuska.jalkametri.util;

import fi.tuska.jalkametri.test.JalkametriTestCase;

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
