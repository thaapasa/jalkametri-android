package fi.tuska.jalkametri.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.gui.DrinkSizeSelector;
import fi.tuska.jalkametri.gui.IconPickerDialog;

/**
 * Modifies an existing drink size.
 *
 * @author Tuukka Haapasalo
 */
public class EditDrinkSizeActivity extends JalkametriDBActivity {

    private static final String KEY_SELECTED_SIZE = "selected_size";

    private DrinkSizeSelector drinkSizeSelector;
    private DrinkSize selectedSize;
    private Long originalId;

    public EditDrinkSizeActivity() {
        super(R.string.title_edit_drink_size, Companion.getNO_HELP_TEXT());
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /**
     * Call to prepare an intent for showing the size editor for the given
     * drink size
     */
    public static void prepareForDrinkSizeEdit(Intent intent, DrinkSize size) {
        DBDataObject.enforceBackedObject(size);
        intent.putExtra(KEY_SELECTED_SIZE, size);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_size_modify);

        // Load the preparation data from intents
        Bundle extras = getIntent().getExtras();
        this.selectedSize = (DrinkSize) extras.get(KEY_SELECTED_SIZE);
        this.originalId = selectedSize.getIndex();

        initializeComponents();

        // Initialize the drink size selector
        drinkSizeSelector = new DrinkSizeSelector(this, getAdapter(), true, true,
            Common.DIALOG_SELECT_SIZE_ICON);
        drinkSizeSelector.initializeComponents(selectedSize);
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
        // Drink size
        DrinkSize sel = (DrinkSize) savedInstanceState.get(KEY_SELECTED_SIZE);
        if (sel != null) {
            this.selectedSize = sel;
        }

        updateUIFromSelection();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateSelectionFromUI();
        outState.putSerializable(KEY_SELECTED_SIZE, selectedSize);
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    @Override
    public void updateUI() {
        // Nothing here
    }

    private void initializeComponents() {
        // Nothing here
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */

    public void onOKPressed(View okButton) {
        updateSelectionFromUI();
        setResult(RESULT_OK, DrinkActivities.createDrinkSizeResult(selectedSize, originalId));
        finish();
    }

    public void updateUIFromSelection() {
        assert selectedSize != null;
        drinkSizeSelector.setDrinkSize(selectedSize, false);
    }

    public void updateSelectionFromUI() {
        DrinkSize size = drinkSizeSelector.getDrinkSize();
        selectedSize = size;
    }

    /*
     * Dialog handling
     * -------------------------------------------------------------
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case Common.DIALOG_SELECT_SIZE_ICON:
            dialog = new IconPickerDialog(this, drinkSizeSelector.getSetSizeIconCallback());
            return dialog;
        }
        return super.onCreateDialog(id);
    }

}
