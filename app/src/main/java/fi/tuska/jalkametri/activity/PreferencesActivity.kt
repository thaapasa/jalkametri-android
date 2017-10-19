package fi.tuska.jalkametri.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import fi.tuska.jalkametri.Common
import fi.tuska.jalkametri.CommonActivities
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.Preferences.Gender
import fi.tuska.jalkametri.data.DrinkActions
import fi.tuska.jalkametri.gui.DataBackupHandler
import fi.tuska.jalkametri.util.LocalizationUtil
import fi.tuska.jalkametri.util.NumberUtil
import org.joda.time.LocalTime


/**
 * An activity for editing your preferences.
 *
 * @author Tuukka Haapasalo
 */
class PreferencesActivity : JalkametriDBActivity(R.string.title_preferences, R.string.help_prefs), GUIActivity {

    private var viewModel: ViewModel? = null

    init {
        setShowDefaultHelpMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefs)

        this.viewModel = ViewModel(this)
        updateUI()
        hideSoftKeyboard()
    }

    override fun onPause() {
        viewModel?.savePreferences()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        viewModel?.updateUIFromPreferences()
    }

    override fun onBackPressed() {
        if (viewModel?.onBackPressed() != false) {
            super.onBackPressed()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel?.updateUIFromPreferences()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel?.savePreferences()
    }

    fun showAbout(v: View) {
        CommonActivities.showAbout(this)
    }

    fun backupData(v: View) {
        DataBackupHandler.backupData(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        when (requestCode) {
            Common.PERMISSIONS_FOR_CREATE_BACKUP -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DataBackupHandler.backupData(this)
                }
            }
            Common.PERMISSIONS_FOR_RESTORE_BACKUP -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DataBackupHandler.restoreData(this)
                }
            }
        }
    }

    fun restoreData(v: View) {
        DataBackupHandler.restoreData(this)
    }

    override fun updateUI() {}


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        updateUI()
    }

    companion object {
        private val TAG = "PreferencesActivity"
    }

    private class ViewModel(val activity: PreferencesActivity) {

        private val resources = activity.resources
        private val prefs = activity.prefs

        private val languageValues = resources.getStringArray(R.array.language_values)
        private val genderValues = resources.getStringArray(R.array.gender_values)
        private val standardDrinkValues = resources.getStringArray(R.array.std_drink_weight_values)

        private val languageSpinner = activity.findViewById(R.id.language_edit) as Spinner
        private val genderSpinner = activity.findViewById(R.id.gender_edit) as Spinner
        private val weekStartSpinner = activity.findViewById(R.id.week_start_edit) as Spinner
        private val standardDrinkSpinner = activity.findViewById(R.id.standard_drink) as Spinner
        private val weightEdit = activity.findViewById(R.id.weight_edit) as EditText
        private val drivingLimitEdit = activity.findViewById(R.id.driving_limit_edit) as EditText
        private val dayChangeEdit = activity.findViewById(R.id.day_change_edit) as EditText
        private val timeFormat = activity.timeUtil.timeFormat

        private var dayChangeTime: LocalTime = activity.prefs.dayChangeTime

        private var allowLeave = true
        private var backRequested = false

        init {
            populateSpinners()
            dayChangeEdit.setOnClickListener {
                activity.timeUtil.pickTime(activity, dayChangeTime, { setDayChangeTime(it) })
            }
        }

        private fun setDayChangeTime(time: LocalTime) {
            dayChangeTime = time
            dayChangeEdit.setText(timeFormat.print(time))
        }

        private fun populateSpinners() {
            // Language spinner
            run {
                val adapter = ArrayAdapter.createFromResource(activity, R.array.language_options,
                        android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                languageSpinner.adapter = adapter
            }
            // Gender spinner
            run {
                val adapter = ArrayAdapter.createFromResource(activity, R.array.gender_options,
                        android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                genderSpinner.adapter = adapter
            }
            // Week start day spinner
            run {
                val adapter = ArrayAdapter.createFromResource(activity, R.array.week_start_options,
                        android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                weekStartSpinner.adapter = adapter
            }
            // Standard drink spinner
            run {
                val adapter = ArrayAdapter.createFromResource(activity,
                        R.array.std_drink_weight_options, android.R.layout.simple_spinner_item)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                standardDrinkSpinner.adapter = adapter
            }
        }


        fun updateUIFromPreferences() {
            // Populate language
            val lang = prefs.locale
            languageValues.indices.find { lang.language == languageValues[it] }?.let {
                languageSpinner.setSelection(it)
            }

            // Populate weight
            val res = resources
            val weight = prefs.weight
            weightEdit.setText(NumberUtil.toString(weight, res))

            // Populate driving limit
            val drivingLimit = prefs.drivingAlcoholLimit
            drivingLimitEdit.setText(NumberUtil.toString(drivingLimit, res))

            // Populate day change time
            setDayChangeTime(prefs.dayChangeTime)

            // Populate gender
            val gender = prefs.gender
            genderValues.indices.find { gender.toString() == genderValues[it] }?.let {
                genderSpinner.setSelection(it)
            }

            // Populate standard drink selection
            val drinkSel = prefs.standardDrinkCountry
            standardDrinkValues.indices
                    .find { drinkSel != null && drinkSel.equals(standardDrinkValues[it], ignoreCase = true) }
                    ?.let { standardDrinkSpinner.setSelection(it) }

            // Populate week start
            val startMonday = prefs.isWeekStartMonday
            // Monday is first
            weekStartSpinner.setSelection(if (startMonday) 0 else 1)
        }

        fun savePreferences() {
            val editor = prefs.edit()
            var needRecalculatePortions = false

            // Set language
            run {
                val pos = languageSpinner.selectedItemPosition
                if (pos >= 0 && pos <= languageValues.size) {
                    val languageStr = languageValues[pos]
                    val lang = LocalizationUtil.getLocale(languageStr)
                    prefs.setLocale(editor, lang)
                }
            }

            // Set weight
            val locale = prefs.locale
            val weight = NumberUtil.readDouble(weightEdit.text.toString(), locale)
            prefs.setWeight(editor, weight)

            // Set driving alcohol limit
            val drivingLimit = NumberUtil.readDouble(drivingLimitEdit.text.toString(), locale)
            prefs.setDrivingAlcoholLimit(editor, drivingLimit)

            // Set day change time
            val hour = dayChangeTime.hourOfDay
            val minute = dayChangeTime.minuteOfHour
            prefs.setDayChangeHour(editor, hour)
            prefs.setDayChangeMinute(editor, minute)

            // Set gender
            run {
                val pos = genderSpinner.selectedItemPosition
                if (pos >= 0 && pos <= genderValues.size) {
                    val genderStr = genderValues[pos]
                    val gender = Gender.valueOf(genderStr)
                    if (gender != null) {
                        prefs.setGender(editor, gender)
                    }
                }
            }

            // Set standard drink selection
            val orgStdWei = prefs.standardDrinkAlcoholWeight
            run {
                val pos = standardDrinkSpinner.selectedItemPosition
                if (pos >= 0 && pos <= standardDrinkValues.size) {
                    val stdDrinkCountry = standardDrinkValues[pos]
                    if (stdDrinkCountry != null) {
                        prefs.setStandardDrinkCountry(editor, stdDrinkCountry)
                    }
                }
            }

            // Set week start
            run {
                val pos = weekStartSpinner.selectedItemPosition
                if (pos >= 0 && pos <= genderValues.size) {
                    prefs.setWeekStartMonday(editor, pos == 0)
                }
            }

            // Commit changes
            editor.commit()

            if (prefs.standardDrinkAlcoholWeight != orgStdWei) {
                needRecalculatePortions = true
            }

            if (needRecalculatePortions) {
                recalculateHistoryPortions()
            }
        }

        private fun recalculateHistoryPortions() {
            allowLeave = false
            // Recalculate the portions in history database
            DrinkActions.recalculateHistoryPortions(activity.adapter, activity) {
                allowLeave = true
                if (backRequested) {
                    activity.onBackPressed()
                }
            }
        }

        fun onBackPressed(): Boolean {
            savePreferences()
            activity.setResult(Activity.RESULT_OK)
            return if (allowLeave) {
                backRequested = false
                true
            } else {
                backRequested = true
                false
            }
        }
    }


}
