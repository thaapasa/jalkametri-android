package fi.tuska.jalkametri.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import fi.tuska.jalkametri.CommonActivities
import fi.tuska.jalkametri.JalkametriApplication
import fi.tuska.jalkametri.PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.Preferences
import fi.tuska.jalkametri.util.TimeUtil

/**
 * Abstract base class for jAlkaMetri activities; contains common
 * functionality.
 */
abstract class JalkametriActivity(private val titleResourceId: Int, private val helpTextId: Int) : Activity(), GUIActivity {

    private var mainView: View? = null
    private var showDefaultHelpMenu = false

    val jalkametriApplication: JalkametriApplication
        get() = application as JalkametriApplication

    val prefs: Preferences
        get() = jalkametriApplication.prefs

    val timeUtil: TimeUtil
        get() = jalkametriApplication.timeUtil

    private var firstRun: Boolean = false

    /**
     * Returns true on the first run, false afterwards. The value is reset at
     * onCreate(), so if this is called from onStart(), it will return true at
     * the first onStart() invocation, and false after that.
     */
    val isFirstRunAfterCreate: Boolean
        get() {
            val res = firstRun
            firstRun = false
            return res
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val res = resources
        // Update title to enforce correct language
        title = res.getString(titleResourceId)

        this.firstRun = true
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        this.mainView = findViewById(R.id.main_view)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        this.mainView = findViewById(R.id.main_view)
    }

    protected fun invalidateView() {
        mainView?.invalidate()
    }

    protected fun setShowDefaultHelpMenu(state: Boolean) {
        this.showDefaultHelpMenu = state
    }

    override fun getContext(): Context {
        return applicationContext
    }

    protected fun hideSoftKeyboard() {
        window.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    protected fun tryToHideSoftKeyboard(view: View) {
        val service = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        service.hideSoftInputFromInputMethod(view.windowToken, 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val s = super.onCreateOptionsMenu(menu)
        if (showDefaultHelpMenu && helpTextId != NO_HELP_TEXT) {
            val inflater = menuInflater
            inflater.inflate(R.menu.menu_only_help, menu)
            return true
        }
        return s
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item))
            return true

        if (item.itemId == R.id.action_help) {
            // Show the help screen for this activity
            showHelp(null)
            return true
        }

        return false
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_devel_clear_def_library_created ->
                // Debugging action: clear default drink library creation flag
                if (DEVELOPER_FUNCTIONALITY_ENABLED) {
                    prefs.edit().let {
                        prefs.setDrinkLibraryInitialized(it, false)
                        it.commit()
                    }
                    Toast.makeText(this, "Drink library flag cleared", Toast.LENGTH_SHORT).show()
                    return true
                }

            R.id.action_devel_clear_disclaimer_read ->
                // Debugging action: set license information
                if (DEVELOPER_FUNCTIONALITY_ENABLED) {
                    prefs.edit().let {
                        prefs.setDisclaimerRead(it, false)
                        it.commit()
                    }
                    Toast.makeText(this, "Disclaimer read flag reset", Toast.LENGTH_SHORT).show()
                    return true
                }
        }
        return super.onContextItemSelected(item)
    }

    open fun showDrinkCalculator(v: View?) = CommonActivities.showCalculator(this)
    fun showDrinkHistory(v: View?) = CommonActivities.showDrinkHistory(this, prefs)
    fun showPreferences(v: View?) = CommonActivities.showPreferences(this)
    fun showStatistics(v: View?) = CommonActivities.showStatistics(this)

    fun showHelp(v: View?) {
        if (helpTextId != NO_HELP_TEXT) {
            HelpActivity.showHelp(helpTextId, this)
        }
    }

    companion object {
        val NO_HELP_TEXT = 0
    }

}
