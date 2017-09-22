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
