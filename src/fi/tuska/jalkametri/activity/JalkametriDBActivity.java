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
package fi.tuska.jalkametri.activity;

import android.os.Bundle;
import fi.tuska.jalkametri.DBActivity;
import fi.tuska.jalkametri.db.DBAdapter;

/**
 * Abstract base class for jAlkaMetri activities that use the database;
 * contains common functionality.
 * 
 * @author Tuukka Haapasalo
 */
public abstract class JalkametriDBActivity extends JalkametriActivity implements GUIActivity,
    DBActivity {

    protected DBAdapter adapter;

    protected JalkametriDBActivity(int titleResourceId, int helpTextResId) {
        super(titleResourceId, helpTextResId);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.adapter = new DBAdapter(this);
    }

    @Override
    protected void onPause() {
        adapter.close();
        super.onPause();
    }

    @Override
    public DBAdapter getDBAdapter() {
        return adapter;
    }

}
