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

import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import fi.tuska.jalkametri.R;

/**
 * A simple activity that shows a "legal" disclaimer.
 *
 * @author Tuukka Haapasalo
 */
public class DisclaimerActivity extends JalkametriActivity {

    public DisclaimerActivity() {
        super(R.string.title_disclaimer, NO_HELP_TEXT);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disclaimer);
        findMainView();
    }

    @Override
    public void onBackPressed() {
        // Do not allow return on back button
        Resources res = getResources();
        Toast.makeText(DisclaimerActivity.this, res.getString(R.string.disclaimer_readtext),
            Toast.LENGTH_LONG).show();
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    public void onOKPressed(View v) {
        // Mark that the disclaimer has been read
        Editor editor = prefs.edit();
        prefs.setDisclaimerRead(editor, true);
        editor.commit();

        // Dismiss this activity when OK is pressed
        super.onBackPressed();
    }

    public void onWhatPressed(View v) {
        // Show a message that asks the user to read the text again
        Resources res = getResources();
        Toast.makeText(DisclaimerActivity.this, res.getString(R.string.disclaimer_readagain),
            Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateUI() {
        // Nothing required
    }

}
