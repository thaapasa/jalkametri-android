package fi.tuska.jalkametri.activity;

import static fi.tuska.jalkametri.Common.KEY_ORIGINAL;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.data.CategorySelection;
import fi.tuska.jalkametri.data.IconName;
import fi.tuska.jalkametri.gui.IconPickerDialog;
import fi.tuska.jalkametri.gui.IconView;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.ObjectCallback;

/**
 * Selects the drink details. This activity can be used as part of the drink
 * selecting path, or it can be fired up directly with the known drink
 * details.
 *
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkActivity - SelectDrinkSizeActivity - SelectDrinkDetailsActivity.
 *
 * @author Tuukka Haapasalo
 */
public class EditCategoryActivity extends JalkametriActivity {

    private static final String KEY_SELECTED_CATEGORY = "selected_category";
    private static final String TAG = "EditCategoryActivity";

    private EditText nameEdit;
    private IconView iconView;

    private CategorySelection selection;
    private long originalID = 0;

    private final ObjectCallback<IconName> iconNameCallback = new ObjectCallback<IconName>() {
        @Override
        public void objectSelected(IconName icon) {
            // Update icon
            LogUtil.d(TAG, "Selecting icon %s", icon.getIcon());
            iconView.setIcon(icon);
        }
    };

    public EditCategoryActivity() {
        super(R.string.title_edit_category, NO_HELP_TEXT);
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */
    public static boolean prepareForCategoryAdd(Context parent, Intent intent) {
        intent.putExtra(KEY_SELECTED_CATEGORY, new CategorySelection());
        return true;
    }

    public static boolean prepareForCategoryModification(Context parent, Intent intent,
        DrinkCategory category) {
        LogUtil.d(TAG, "Preparing to edit category %s (%d)", category, category.getIndex());
        intent.putExtra(KEY_SELECTED_CATEGORY, new CategorySelection(category));
        intent.putExtra(KEY_ORIGINAL, Long.valueOf(category.getIndex()));
        return true;
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_details);

        Bundle extras = getIntent().getExtras();
        selection = (CategorySelection) extras.get(KEY_SELECTED_CATEGORY);
        assert selection != null;
        originalID = extras.getLong(KEY_ORIGINAL);

        nameEdit = (EditText) findViewById(R.id.name_edit);
        iconView = (IconView) findViewById(R.id.icon);
    }

    @Override
    protected void onPause() {
        updateSelectionFromUI();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUIFromSelection();
    }

    @Override
    public void onBackPressed() {
        updateSelectionFromUI();
        super.onBackPressed();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        CategorySelection sel = (CategorySelection) savedInstanceState.get(KEY_SELECTED_CATEGORY);
        if (sel != null) {
            this.selection = sel;
        }

        updateUIFromSelection();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateSelectionFromUI();
        outState.putSerializable(KEY_SELECTED_CATEGORY, selection);
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    public boolean isModifying() {
        return originalID > 0;
    }

    @Override
    public void updateUI() {
        // Nothing required
    }

    public void onOKPressed(View okButton) {
        updateSelectionFromUI();
        setResult(RESULT_OK, DrinkActivities.createCategoryResult(selection, originalID));
        finish();
    }

    public void onClickIcon(View v) {
        LogUtil.d(TAG, "Selecting icon...");
        showDialog(Common.DIALOG_SELECT_ICON);
    }

    public void updateUIFromSelection() {
        assert selection != null;
        nameEdit.setText(selection.getName());
        String icon = selection.getIcon();
        iconView.setIcon(icon);
    }

    public void updateSelectionFromUI() {
        // Drink type details
        selection.setName(nameEdit.getText().toString());
        String icon = iconView.getIcon().getIcon();
        selection.setIcon(icon);
    }

    /*
     * Dialog handling
     * -------------------------------------------------------------
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case Common.DIALOG_SELECT_ICON:
            dialog = new IconPickerDialog(this, iconNameCallback);
            return dialog;
        }
        return super.onCreateDialog(id);
    }

}
