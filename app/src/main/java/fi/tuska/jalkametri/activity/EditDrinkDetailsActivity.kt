package fi.tuska.jalkametri.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import fi.tuska.jalkametri.Common
import fi.tuska.jalkametri.Common.KEY_ORIGINAL
import fi.tuska.jalkametri.CommonActivities
import fi.tuska.jalkametri.DrinkActivities
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.data.*
import fi.tuska.jalkametri.gui.DrinkSizeSelector
import fi.tuska.jalkametri.gui.IconPickerDialog
import fi.tuska.jalkametri.gui.IconView
import fi.tuska.jalkametri.util.LogUtil
import fi.tuska.jalkametri.util.NumberUtil
import fi.tuska.jalkametri.util.ObjectCallback
import org.joda.time.Instant
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import org.joda.time.LocalTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

/**
 * Selects the drink details. This activity can be used as part of the drink
 * selecting path, or it can be fired up directly with the known drink
 * details.
 *
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkTypeActivity - SelectDrinkSizeActivity - EditDrinkDetailsActivity.
 */
class EditDrinkDetailsActivity : JalkametriDBActivity(R.string.title_edit_drink_details, R.string.help_edit_drink) {

    private var viewModel: ViewModel? = null

    private class ViewModel(private val activity: EditDrinkDetailsActivity) {

        private val prefs = activity.prefs
        private val resources = activity.resources
        private val timeUtil = activity.timeUtil
        private val extras = activity.intent.extras

        var selection: DrinkSelection = extras.get(KEY_SELECTED_DRINK_SELECTION) as DrinkSelection
            private set
        val originalID: Long = extras.getLong(KEY_ORIGINAL)

        private val dateEditFormatter: DateTimeFormatter = DateTimeFormat.forPattern(resources.getString(R.string.date_format)).withZone(timeUtil.timeZone).withLocale(prefs.locale)
        private val timeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(resources.getString(R.string.time_format)).withZone(timeUtil.timeZone).withLocale(prefs.locale)
        private val dateClickListener = OnClickListener {
            DatePickerDialog(activity,
                    OnDateSetListener { _, y, m, d -> setSelectedDate(LocalDate(y, m, d)) },
                    selectedDate.year, selectedDate.monthOfYear, selectedDate.dayOfMonth).show()
        }
        private val timeClickListener = OnClickListener {
            TimePickerDialog(activity,
                    OnTimeSetListener { view, h, m -> setSelectedTime(LocalTime(h, m)) },
                    selectedTime.hourOfDay, selectedTime.minuteOfHour, true).show()
        }
        private val nameEdit: EditText = activity.findViewById(R.id.name_edit) as EditText
        private val strengthEdit: EditText = activity.findViewById(R.id.strength_edit) as EditText
        private val commentEdit: EditText = activity.findViewById(R.id.comment_edit) as EditText
        private val iconView: IconView = activity.findViewById(R.id.icon) as IconView
        private val dateEdit: EditText = (activity.findViewById(R.id.date_edit) as EditText).apply {
            setOnClickListener(dateClickListener)
        }
        private val timeEdit: EditText = (activity.findViewById(R.id.time_edit) as EditText).apply {
            setOnClickListener(timeClickListener)
        }
        private val showTimeSelection = true
        private var selectedDate: LocalDate = LocalDate.now(timeUtil.timeZone)
        private var selectedTime: LocalTime = LocalTime.now(timeUtil.timeZone)
        val drinkSizeSelector: DrinkSizeSelector = (DrinkSizeSelector(activity, activity.adapter, true,
                true, Common.DIALOG_SELECT_SIZE_ICON)).apply {
            initializeComponents(selection.size)
        }

        init {
            val okButton = activity.findViewById(R.id.drink_button) as Button
            val okTitle = extras.getString(KEY_OK_BUTTON_TITLE)
            if (okTitle != null) {
                okButton.text = okTitle
            }
            activity.invalidateView()
        }

        fun select(drink: DrinkSelection) {
            selection = drink
            updateUIFromSelection()
        }

        val iconNameCallback = ObjectCallback<IconName> { icon ->
            // Update icon
            LogUtil.d(TAG, "Selecting icon %s", icon.icon)
            iconView.icon = icon
        }

        val isModifying: Boolean
            get() = originalID > 0

        private fun setSelectedDate(date: LocalDate) {
            selectedDate = date
            dateEdit.setText(dateEditFormatter.print(selectedDate))
        }

        private fun setSelectedTime(time: LocalTime) {
            selectedTime = time
            timeEdit.setText(timeFormatter.print(selectedTime))
        }

        fun updateSelectionFromUI() {
            selection.drink.let {
                it.name = nameEdit.text.toString()
                it.strength = NumberUtil.readDouble(strengthEdit.text.toString(), prefs.locale)
                val icon = iconView.icon.icon
                it.icon = icon
                it.comment = commentEdit.text.toString()
            }
            selection.size = drinkSizeSelector.drinkSize
            run {
                val now = LocalDateTime.now(timeUtil.timeZone)
                val time = selectedTime.withSecondOfMinute(now.secondOfMinute)
                val instant = selectedDate.toLocalDateTime(time).toDateTime(timeUtil.timeZone).toInstant()
                selection.time = instant
            }
        }

        fun updateUIFromSelection() {
            selection.drink.let {
                nameEdit.setText(it.name)
                strengthEdit.setText(NumberUtil.toString(it.strength, resources))
                val icon = it.icon
                iconView.setIcon(icon)
                commentEdit.setText(it.comment)
            }
            drinkSizeSelector.setDrinkSize(selection.size, false)
            selection.time.let {
                val time = LocalDateTime(it, timeUtil.timeZone)
                // cal.time = it
                //timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
                //timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
                setSelectedTime(time.toLocalTime())
                setSelectedDate(time.toLocalDate())
            }
        }

        fun saveState(outState: Bundle?) = outState?.putSerializable(KEY_SELECTED_DRINK_SELECTION, selection)
        fun restoreState(inState: Bundle?) = (inState?.get(KEY_SELECTED_DRINK_SELECTION) as DrinkSelection?)?.let { select(it) }

        fun updateStrength(strength: Double) {
            LogUtil.d(TAG, "Setting strength to %f", strength)
            selection.drink.strength = strength
            updateUIFromSelection()
        }
    }


    init {
        setShowDefaultHelpMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_details)
        hideSoftKeyboard()
        viewModel = ViewModel(this)
    }

    override fun onPause() {
        viewModel?.updateSelectionFromUI()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        viewModel?.updateUIFromSelection()
    }

    override fun onBackPressed() {
        viewModel?.updateSelectionFromUI()
        super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel?.restoreState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        viewModel?.saveState(outState)
    }

    override fun updateUI() {}

    fun onOKPressed(okButton: View) {
        viewModel?.let {
            it.updateSelectionFromUI()
            setResult(Activity.RESULT_OK, DrinkActivities.createDrinkSelectionResult(it.selection, it.originalID))
            finish()
        }
    }

    fun onClickIcon(v: View) {
        LogUtil.d(TAG, "Selecting icon...")
        showDialog(Common.DIALOG_SELECT_ICON)
    }

    override fun showDrinkCalculator(v: View?) {
        viewModel?.let {
            it.updateSelectionFromUI()
            // Use the values from current selection as the basis of the calculator
            CommonActivities.showCalculator(this, it.selection)
        }
    }

    override fun onCreateDialog(id: Int): Dialog {
        var dialog: Dialog? = null
        when (id) {
            Common.DIALOG_SELECT_ICON -> {
                dialog = IconPickerDialog(this, viewModel!!.iconNameCallback)
                return dialog
            }
            Common.DIALOG_SELECT_SIZE_ICON -> {
                dialog = IconPickerDialog(this, viewModel!!.drinkSizeSelector.setSizeIconCallback)
                return dialog
            }
        }
        return super.onCreateDialog(id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        LogUtil.d(TAG, "Return from activity %d; result %d", requestCode, resultCode)
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                Common.ACTIVITY_CODE_SHOW_CALCULATOR -> {
                    // Returning from calculator
                    CalculatorActivity.getCalculatorFromResult(data)?.let {
                        viewModel?.updateStrength(it.strength)
                    }
                }
            }
        }
    }

    companion object {

        private val KEY_OK_BUTTON_TITLE = "ok_button_title"
        private val KEY_SELECTED_DRINK_SELECTION = "selected_drink_selection"
        private val KEY_SHOW_TIME_PICKER = "show_time_picker"
        private val KEY_SHOW_SIZE_ICON_EDIT = "show_size_icon_edit"
        private val KEY_SHOW_SIZE_SELECTION = "show_size_selection"

        private val TAG = "SelectDrinkDetailsActivity"

        /**
         * @return true if the drink selection was OK (all required information
         * was found); false if the selection could not be prepared.
         */
        fun prepareForDrinkSelection(parent: Context, intent: Intent,
                                     sel: DrinkSelection): Boolean {
            val res = parent.resources
            if (sel.drink == null) {
                Toast.makeText(parent, res.getText(R.string.msg_drink_not_set), Toast.LENGTH_SHORT)
                        .show()
                return false
            }
            if (sel.size == null) {
                Toast.makeText(parent, res.getText(R.string.msg_drink_size_not_set),
                        Toast.LENGTH_SHORT).show()
                return false
            }
            if (sel.time == null) {
                val time = Instant.now()
                sel.time = time
            }

            LogUtil.d(TAG, "Preparing to edit details for %s", sel)
            intent.apply {
                putExtra(KEY_SELECTED_DRINK_SELECTION, sel)
                putExtra(KEY_OK_BUTTON_TITLE, res.getString(R.string.action_drink))
                putExtra(KEY_SHOW_TIME_PICKER, true)
                putExtra(KEY_SHOW_SIZE_ICON_EDIT, false)
                putExtra(KEY_SHOW_SIZE_SELECTION, true)
            }
            return true
        }

        fun prepareForDrinkEventModification(parent: Context, intent: Intent,
                                             event: DrinkEvent, showTime: Boolean, showSizeSelection: Boolean, showSizeIconEdit: Boolean) {
            val res = parent.resources
            LogUtil.d(TAG, "Preparing to edit details for %s", event)
            intent.apply {
                putExtra(KEY_SELECTED_DRINK_SELECTION, event)
                putExtra(KEY_ORIGINAL, java.lang.Long.valueOf(event.index))
                putExtra(KEY_OK_BUTTON_TITLE, res.getString(R.string.action_ok))
                putExtra(KEY_SHOW_TIME_PICKER, showTime)
                putExtra(KEY_SHOW_SIZE_ICON_EDIT, showSizeIconEdit)
                putExtra(KEY_SHOW_SIZE_SELECTION, showSizeSelection)
            }
        }

        fun prepareForDrinkModification(context: Context, intent: Intent, drink: Drink) {
            val res = context.resources
            LogUtil.d(TAG, "Preparing to edit details for %s", drink)
            // For convenience, this editor always edits drink events, so create a dummy event
            val event = DrinkEvent(drink, DrinkSize(), Instant.now())
            intent.apply {
                putExtra(KEY_SELECTED_DRINK_SELECTION, event)
                // We are modifying the drink now, so store the drink identifier
                putExtra(KEY_ORIGINAL, java.lang.Long.valueOf(drink.index))
                putExtra(KEY_OK_BUTTON_TITLE, res.getString(R.string.action_ok))
                putExtra(KEY_SHOW_TIME_PICKER, false)
                putExtra(KEY_SHOW_SIZE_ICON_EDIT, false)
                putExtra(KEY_SHOW_SIZE_SELECTION, false)
            }
        }
    }

}
