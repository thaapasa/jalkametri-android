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
package fi.tuska.jalkametri.test;

import android.content.Context;
import fi.tuska.jalkametri.DBActivity;
import fi.tuska.jalkametri.UnitTestDBAdapter;
import fi.tuska.jalkametri.activity.GUIActivity;
import fi.tuska.jalkametri.db.DBAdapter;

public abstract class JalkametriDBTestCase extends JalkametriTestCase {

    /**
     * Will be initialized during setUp(). Do not reference from constructors!
     */
    protected DBAdapter adapter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.adapter = new UnitTestDBAdapter(getContext());
    }

    protected final DBGUIActivity dummyParentActivity = new DBGUIActivity();

    private class DBGUIActivity implements DBActivity, GUIActivity {

        @Override
        public void updateUI() {
            // Nada
        }

        @Override
        public Context getContext() {
            return JalkametriDBTestCase.this.getContext();
        }

        @Override
        public DBAdapter getDBAdapter() {
            return adapter;
        }

    }
}
