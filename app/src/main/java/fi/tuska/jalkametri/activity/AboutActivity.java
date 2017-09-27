package fi.tuska.jalkametri.activity;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * Activity for showing an about screen. The screen shows information about the
 * software.
 *
 * @author Tuukka Haapasalo
 */
public class AboutActivity extends JalkametriActivity implements GUIActivity {

    private static final String TAG = "AboutActivity";

    private TextView versionNumber;
    private TextView versionText;
    private TextView versionShort;

    public AboutActivity() {
        super(R.string.title_about, R.string.help_about);
        setShowDefaultHelpMenu(true);
    }

    /*
     * Standard activity functions --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        versionNumber = (TextView) findViewById(R.id.version_number);
        versionText = (TextView) findViewById(R.id.version_info);
        versionShort = (TextView) findViewById(R.id.version_short);

        initComponents();

        updateUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    /*
     * Activity item initialization --------------------------------------------
     */
    private void initComponents() {
        setVersionNumber();
    }

    public void setVersionNumber() {
        try {
            String vNum = getPackageManager().getPackageInfo(getPackageName(),
                    0).versionName;
            versionNumber.setText(vNum);
        } catch (NameNotFoundException e) {
            versionNumber.setText("0.0");
        }
    }

    @Override
    public void updateUI() {
        Resources res = getResources();
        final boolean hasLicense = getPrefs().isLicensePurchased();
        // Set the version text to reflect the license purchasing status
        versionShort.setText(res
                .getString(hasLicense ? R.string.version_licensed
                        : R.string.version_free));
        versionText.setText(res
                .getString(hasLicense ? R.string.about_version_licensed
                        : R.string.about_version_free));
    }

    /*
     * Custom actions ----------------------------------------------------------
     */
    public void onOKPressed(View v) {
        // Dismiss this activity when OK is pressed
        onBackPressed();
    }

    public void purchaseLicense(View v) {
        // Purchase the license
        if (getPrefs().isLicensePurchased()) {
            LogUtil.i(TAG, "License already bought, not repurchasing");
            // Already bought
            updateUI();
        }
    }

    public void checkLicense(View v) {
        // Check for existing license
        if (getPrefs().isLicensePurchased()) {
            LogUtil.i(TAG, "License already bought, not checking");
            updateUI();
        }
    }

    /*
     * Return from activities --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateUI();
    }
}
