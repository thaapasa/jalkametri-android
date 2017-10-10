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
import fi.tuska.jalkametri.util.AssertionUtils
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

    private var nameEdit: EditText? = null
    private var iconView: IconView? = null

    private var selection: CategorySelection? = null
    private var originalID: Long = 0

    val isModifying: Boolean
        get() = originalID > 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_details)

        val extras = intent.extras
        selection = extras.get(KEY_SELECTED_CATEGORY) as CategorySelection
        AssertionUtils.expect(selection != null)
        originalID = extras.getLong(KEY_ORIGINAL)

        nameEdit = findViewById(R.id.name_edit) as EditText
        iconView = findViewById(R.id.icon) as IconView
    }

    override fun onPause() {
        updateSelectionFromUI()
        super.onPause()
    }

    override fun onStart() {
        super.onStart()
        updateUIFromSelection()
    }

    override fun onBackPressed() {
        updateSelectionFromUI()
        super.onBackPressed()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val sel = savedInstanceState.get(KEY_SELECTED_CATEGORY) as CategorySelection?
        if (sel != null) {
            this.selection = sel
        }

        updateUIFromSelection()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        updateSelectionFromUI()
        outState.putSerializable(KEY_SELECTED_CATEGORY, selection)
    }

    override fun updateUI() {
        // Nothing required
    }

    fun onOKPressed(v: View) {
        updateSelectionFromUI()
        setResult(RESULT_OK, DrinkActivities.createCategoryResult(selection, originalID))
        finish()
    }

    fun onClickIcon(v: View) {
        LogUtil.d(TAG, "Selecting icon...")
        showCustomDialog(IconPickerDialog.createDialog { icon ->
            // Update icon
            LogUtil.d(TAG, "Selecting icon %s", icon.icon)
            iconView!!.icon = icon
        })
    }

    fun updateUIFromSelection() {
        AssertionUtils.expect(selection != null)
        nameEdit!!.setText(selection!!.name)
        val icon = selection!!.icon
        iconView!!.setIcon(icon)
    }

    fun updateSelectionFromUI() {
        // Drink type details
        selection!!.name = nameEdit!!.text.toString()
        val icon = iconView!!.icon.icon
        selection!!.icon = icon
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
