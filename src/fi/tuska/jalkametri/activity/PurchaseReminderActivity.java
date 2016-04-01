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
import android.view.View;
import fi.tuska.jalkametri.CommonActivities;
import fi.tuska.jalkametri.R;

/**
 * A simple activity that shows a license purchasing reminder.
 * 
 * @author Tuukka Haapasalo
 */
public class PurchaseReminderActivity extends JalkametriActivity {

    public PurchaseReminderActivity() {
        super(R.string.title_reminder, NO_HELP_TEXT);
    }

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.purchase_reminder);
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public void updateUI() {
        // Nothing required
    }

    @Override
    public void onBackPressed() {
        // Back button disabled
    }
    
    /*
     * Custom actions ------------------------------------------------------
     */

    public void purchaseLicense(View v) {
        // Go to about screen
        CommonActivities.showAbout(this);
        finish();
    }

    public void dontPurchase(View v) {
        finish();
    }

}
