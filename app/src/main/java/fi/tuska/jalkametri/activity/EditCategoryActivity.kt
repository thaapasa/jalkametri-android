package fi.tuska.jalkametri.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import fi.tuska.jalkametri.Common.KEY_ORIGINAL
import fi.tuska.jalkametri.DrinkActivities
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.DrinkCategory
import fi.tuska.jalkametri.data.CategorySelection
import fi.tuska.jalkametri.gui.IconPickerDialog
import fi.tuska.jalkametri.gui.IconView
import fi.tuska.jalkametri.util.LogUtil

/**
 * Selects the drink details. This activity can be used as part of the drink
 * selecting path, or it can be fired up directly with the known drink
 * details.
 *
 *
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkActivity - SelectDrinkSizeActivity - SelectDrinkDetailsActivity.
 *
 * @author Tuukka Haapasalo
 */
class EditCategoryActivity : JalkametriActivity(R.string.title_edit_category, JalkametriActivity.Companion.NO_HELP_TEXT) {

    private var viewModel: ViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)
        viewModel = ViewModel(this)
    }

    class ViewModel(val activity: EditCategoryActivity) {

        private val extras = activity.intent.extras
        private var _selection: CategorySelection = extras.get(KEY_SELECTED_CATEGORY) as CategorySelection
        private val nameEdit: EditText = activity.findViewById(R.id.name_edit) as EditText
        private val iconView: IconView = activity.findViewById(R.id.icon) as IconView
        val originalID: Long = extras.getLong(KEY_ORIGINAL)

        val isModifying: Boolean
            get() = originalID > 0

        var selection: CategorySelection
            set(s) {
                this._selection = s
                updateUIFromSelection()
            }
            get() {
                updateSelectionFromUI()
                return this._selection
            }

        fun updateUIFromSelection() {
            nameEdit.setText(_selection.name)
            val icon = _selection.icon
            iconView.setIcon(icon)
        }

        fun updateSelectionFromUI() {
            // Drink type details
            _selection.name = nameEdit.text.toString()
            val icon = iconView.icon.icon
            _selection.icon = icon
        }

        fun pickIcon() {
            activity.showCustomDialog(IconPickerDialog.createDialog { icon ->
                // Update icon
                LogUtil.d(TAG, "Selecting icon %s", icon.icon)
                iconView.icon = icon
            })
        }
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewModel?.selection = savedInstanceState.get(KEY_SELECTED_CATEGORY) as CategorySelection
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_SELECTED_CATEGORY, viewModel?.selection)
    }

    override fun updateUI() {
        // Nothing required
    }

    fun onOKPressed(v: View) {
        viewModel?.let {
            setResult(RESULT_OK, DrinkActivities.createCategoryResult(it.selection, it.originalID))
        }
        finish()
    }

    fun onClickIcon(v: View) {
        LogUtil.d(TAG, "Selecting icon...")
        viewModel?.pickIcon()
    }

    companion object {

        private val KEY_SELECTED_CATEGORY = "selected_category"
        private val TAG = "EditCategoryActivity"

        fun prepareForCategoryAdd(parent: Context, intent: Intent): Boolean {
            intent.putExtra(KEY_SELECTED_CATEGORY, CategorySelection())
            return true
        }

        fun prepareForCategoryModification(parent: Context, intent: Intent,
                                           category: DrinkCategory): Boolean {
            LogUtil.d(TAG, "Preparing to edit category %s (%d)", category, category.index)
            intent.putExtra(KEY_SELECTED_CATEGORY, CategorySelection(category))
            intent.putExtra(KEY_ORIGINAL, java.lang.Long.valueOf(category.index))
            return true
        }
    }


}
