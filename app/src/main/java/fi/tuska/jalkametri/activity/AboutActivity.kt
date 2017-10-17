package fi.tuska.jalkametri.activity

import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Bundle
import android.view.View
import android.widget.TextView
import fi.tuska.jalkametri.R

class AboutActivity : JalkametriActivity(R.string.title_about, R.string.help_about), GUIActivity {

    var viewModel: ViewModel? = null

    init {
        setShowDefaultHelpMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        viewModel = ViewModel(this)
        updateUI()
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    // Dismiss this activity when OK is pressed
    fun onOKPressed(v: View) = onBackPressed()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        updateUI()
    }

    override fun updateUI() {
        viewModel?.updateUI()
    }

    class ViewModel(val activity: AboutActivity) {
        private val versionNumber: TextView = activity.findViewById(R.id.version_number) as TextView
        private val versionText: TextView = activity.findViewById(R.id.version_info) as TextView
        private val versionShort: TextView = activity.findViewById(R.id.version_short) as TextView

        init {
            initComponents()
        }

        private fun initComponents() {
            setVersionNumber()
        }

        private fun setVersionNumber() {
            versionNumber.text = try {
                activity.packageManager.getPackageInfo(activity.packageName, 0).versionName
            } catch (e: NameNotFoundException) {
                "0.0"
            }

        }

        fun updateUI() {
            val res = activity.resources
            val hasLicense = activity.prefs.isLicensePurchased
            // Set the version text to reflect the license purchasing status
            versionShort.text = res.getString(if (hasLicense) R.string.version_licensed
            else R.string.version_free)
            versionText.text = res.getString(if (hasLicense) R.string.about_version_licensed
            else R.string.about_version_free)
        }

    }

    companion object {
        private val TAG = "AboutActivity"
    }
}
