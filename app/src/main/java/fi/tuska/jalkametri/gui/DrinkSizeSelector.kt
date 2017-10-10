package fi.tuska.jalkametri.gui

import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import fi.tuska.jalkametri.Common
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.activity.JalkametriActivity
import fi.tuska.jalkametri.dao.DrinkSizes
import fi.tuska.jalkametri.data.DrinkSize
import fi.tuska.jalkametri.db.DBAdapter
import fi.tuska.jalkametri.db.DrinkLibraryDB
import fi.tuska.jalkametri.util.Converter
import fi.tuska.jalkametri.util.LogUtil
import fi.tuska.jalkametri.util.NumberUtil
import java.util.Locale

class DrinkSizeSelector(
        private val parent: JalkametriActivity,
        adapter: DBAdapter,
        private val selectorShown: Boolean,
        private val sizeIconEditorShown: Boolean) {

    private val locale: Locale = parent.prefs.locale
    private val sizeLib: DrinkSizes = DrinkLibraryDB(adapter).drinkSizes

    private var sizeEdit: EditText? = null
    private var sizeNameEdit: EditText? = null
    private var sizeIcon: IconView? = null
    private var spinnerSelection: DrinkSize? = null
    private var selectedDrinkSize: DrinkSize? = null

    /**
     * Selection spinner for pre-existing size entries. This can be missing
     * from the edit form and therefore be null; this class is prepared to
     * handle that. In this case the pre-selection functionality is not
     * enabled.
     */
    private var sizeSelectionSpinner: Spinner? = null
    /**
     * Adapter for the spinner; this will be null if the spinner is null, see
     * the comments for the spinner.
     */
    private var sizeSelectionAdapter: TextIconSpinnerAdapter<DrinkSize>? = null

    /**
     * A CheckBox for checking whether a custom size can be entered. This
     * class is prepared for a null value for this control; i.e., it can be
     * missing from the edit form. In this case, custom editing is always
     * enabled.
     */
    private var modifySizeCheckbox: CheckBox? = null


    private val modifyCheckBoxSelectListener = OnClickListener {
        if (modifySizeCheckbox != null) {
            modifySizeCheckbox!!.isChecked = true
        }
    }

    // Custom size selected
    // Create a new size based on the entered data
    // Return the selected drink
    val drinkSize: DrinkSize?
        get() {
            if (isCustomSizeEditingEnabled) {
                val name = sizeNameEdit!!.text.toString()
                val volume = NumberUtil.readDouble(sizeEdit!!.text.toString(), locale)
                return DrinkSize(name, volume, currentlySelectedIcon)
            } else {
                return selectedDrinkSize
            }
        }

    // If there is no modification button on the form, then it is assumed
    // that the size editing is enabled
    private val isCustomSizeEditingEnabled: Boolean
        get() = modifySizeCheckbox == null || modifySizeCheckbox!!.isChecked

    private val currentlySelectedIcon: String
        get() = if (sizeIconEditorShown && sizeIcon != null)
            sizeIcon!!.icon.icon
        else
            Common.DEFAULT_ICON_NAME

    /**
     * @param initialSelection initially selected size; may be null (in this
     * case, selected the default size)
     */
    fun initializeComponents(initialSelection: DrinkSize?) {

        val initial = initialSelection ?: sizeLib.defaultSize

        sizeEdit = parent.findViewById(R.id.size_edit) as EditText?
        sizeNameEdit = parent.findViewById(R.id.size_name_edit) as EditText?
        modifySizeCheckbox = parent.findViewById(R.id.modify_size) as CheckBox?
        sizeSelectionSpinner = parent.findViewById(R.id.size_selector) as Spinner?

        if (!selectorShown) {
            // Hide the entire size selector
            LogUtil.e(TAG, "Hiding size selector -- TODO: REMOVE THIS!")
            //View selectorArea = parent.findViewById(R.id.size_selection_area);
            //AssertionUtils.INSTANCE.expect(selectorArea != null);
            //selectorArea.setVisibility(View.GONE);
        } else {
            if (sizeIconEditorShown) {
                // Show the size icon editor
                // View sizeArea = parent.findViewById(R.id.size_icon_area);
                // sizeArea.setVisibility(View.VISIBLE);

                val sIcon = parent.findViewById(R.id.size_icon) as IconView?
                sizeIcon = sIcon
                sIcon?.setOnClickListener {
                    // Select an icon
                    if (isCustomSizeEditingEnabled) {
                        val dialog = IconPickerDialog.createDialog { icon ->
                            // Update icon
                            LogUtil.d(TAG, "Selecting icon %s", icon.icon)
                            sIcon.icon = icon
                        }
                        JalkametriActivity.showCustomDialog(parent, dialog)
                    }
                }
            }
        }
        // Modify size
        if (modifySizeCheckbox != null) {
            modifySizeCheckbox!!.isChecked = false
        }
        updateSizeEditorEnabling()

        if (selectorShown && modifySizeCheckbox != null) {
            modifySizeCheckbox!!.setOnCheckedChangeListener { buttonView, isChecked -> updateSizeEditorEnabling() }
        }

        // Size selection spinner
        if (sizeSelectionSpinner != null) {
            populateSizeSelectionSpinner(initial)
        }

        // Set size name/size edit clicking to toggle modification
        // checkbox
        sizeNameEdit!!.setOnClickListener(modifyCheckBoxSelectListener)
        sizeEdit!!.setOnClickListener(modifyCheckBoxSelectListener)

        setDrinkSize(initial, true)

        if (selectorShown && sizeSelectionSpinner != null) {
            sizeSelectionSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(adapter: AdapterView<*>, view: View, position: Int,
                                            id: Long) {
                    val size = sizeSelectionAdapter!!.getItem(position)
                    if (spinnerSelection == null || spinnerSelection != size) {
                        LogUtil.d(TAG, "DrinkSize item %s selected", size)
                        spinnerSelection = size
                        setSizeSelected(size)
                    }
                }

                override fun onNothingSelected(adapter: AdapterView<*>) {}
            }
        }
    }

    /**
     * Sets the given size.
     */
    fun setDrinkSize(size: DrinkSize, addIfMissing: Boolean) {
        this.selectedDrinkSize = size

        if (sizeSelectionAdapter == null) {
            // No size selection adapter, so just set the custom edit forms.
            populateCustomEditors(size)
            return
        }

        // Find the selection from the sizeSelectionAdapter
        val pos = sizeSelectionAdapter!!.findItem(size)
        if (pos != -1) {
            // Size was found from the size list
            sizeSelectionSpinner!!.setSelection(pos)
            // Not a custom item, set the text to the text editors
            if (modifySizeCheckbox != null) {
                modifySizeCheckbox!!.isChecked = false
            }
            spinnerSelection = size
        } else {
            // Size was not found, so this is a custom size
            // Select first element from the spinner
            if (sizeSelectionAdapter!!.itemCount > 0) {
                sizeSelectionSpinner!!.setSelection(0)
                spinnerSelection = sizeSelectionAdapter!!.getItem(0)
            } else {
                spinnerSelection = null
            }
            if (modifySizeCheckbox != null) {
                modifySizeCheckbox!!.isChecked = true
            }
        }
        // Update the text editors
        sizeNameEdit!!.setText(size.name)
        sizeEdit!!.setText(NumberUtil.toString(size.volume, parent.resources))
        if (sizeIconEditorShown && sizeIcon != null)
            sizeIcon!!.setIcon(size.icon)
    }

    private fun populateCustomEditors(size: DrinkSize) {
        sizeNameEdit!!.setText(size.name)
        sizeEdit!!.setText(NumberUtil.toString(size.volume, parent.resources))
        if (sizeIconEditorShown)
            sizeIcon!!.setIcon(size.icon)
    }

    private fun setSizeSelected(size: DrinkSize) {
        selectedDrinkSize = size
        sizeEdit!!.setText(NumberUtil.toString(size.volume, parent.resources))
        sizeNameEdit!!.setText(size.name)
        if (modifySizeCheckbox != null) {
            modifySizeCheckbox!!.isChecked = false
        }
        if (sizeIconEditorShown && sizeIcon != null)
            sizeIcon!!.setIcon(size.icon)
        updateSizeEditorEnabling()
    }

    private fun updateSizeEditorEnabling() {
        val controlsEnabled = isCustomSizeEditingEnabled

        sizeNameEdit!!.isEnabled = controlsEnabled
        sizeEdit!!.isEnabled = controlsEnabled
        if (sizeIconEditorShown && sizeIcon != null) {
            sizeIcon!!.isEnabled = controlsEnabled
        }
    }

    /**
     * Must only be called when the spinner is present on the edit form.
     */
    private fun populateSizeSelectionSpinner(initialSize: DrinkSize) {
        val sizes = sizeLib.allSizes

        sizeSelectionAdapter = TextIconSpinnerAdapter(parent, sizes,
                Converter { item -> item.getIconText(parent.resources) }, Converter { item -> item.icon })
        sizeSelectionSpinner!!.adapter = sizeSelectionAdapter
    }

    companion object {

        private val TAG = "DrinkSizeSelector"
    }

}
