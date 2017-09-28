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
        super(R.string.title_disclaimer, Companion.getNO_HELP_TEXT());
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);
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
        Editor editor = getPrefs().edit();
        getPrefs().setDisclaimerRead(editor, true);
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
