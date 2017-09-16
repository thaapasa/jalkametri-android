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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import fi.tuska.jalkametri.R;

/**
 * A simple activity that shows some help for a jAlcoMeter activity.
 *
 * @author Tuukka Haapasalo
 */
public class HelpActivity extends JalkametriActivity {

    private static final String HELP_TEXT_RES_ID = "helpResId";

    public HelpActivity() {
        super(R.string.title_help, NO_HELP_TEXT);
    }

    /**
     * Fires up a help activity showing the given help text.
     *
     * @param helpTextId the help text resource id
     * @param parent the parent activity
     */
    public static void showHelp(int helpTextId, Activity parent) {
        Intent intent = new Intent(parent, HelpActivity.class);
        intent.putExtra(HELP_TEXT_RES_ID, helpTextId);
        parent.startActivity(intent);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int resId = extras.getInt(HELP_TEXT_RES_ID);
            TextView helpText = (TextView) findViewById(R.id.help_text);
            helpText.setText(getResources().getText(resId));
        }

    }

    @Override
    public void updateUI() {
        // Nothing required
    }

    /*
     * Custom actions ------------------------------------------------------
     */

    public void onOKPressed(View v) {
        onBackPressed();
    }

}
