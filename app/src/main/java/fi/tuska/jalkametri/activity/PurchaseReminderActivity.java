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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_reminder);
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
