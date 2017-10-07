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
        assertEquals("en", LocalizationUtil.INSTANCE.getDEFAULT_LOCALE().getLanguage());

        // Check that current locale is the default
        assertEquals(LocalizationUtil.INSTANCE.getDEFAULT_LOCALE(), getCurrentLocale());

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_EN());
        assertEquals(LocalizationUtil.INSTANCE.getLOCALE_EN(), getCurrentLocale());

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_FI());
        assertEquals(LocalizationUtil.INSTANCE.getLOCALE_FI(), getCurrentLocale());

        switchLocale(LocalizationUtil.INSTANCE.getLOCALE_EN());
        assertEquals(LocalizationUtil.INSTANCE.getLOCALE_EN(), getCurrentLocale());
    }

}
