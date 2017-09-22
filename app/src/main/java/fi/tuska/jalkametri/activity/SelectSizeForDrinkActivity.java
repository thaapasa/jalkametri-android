package fi.tuska.jalkametri.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.gui.DrinkSizeSelector;
import fi.tuska.jalkametri.gui.IconPickerDialog;

/**
 * Selects a new drink size to be added to a drink in the drink library. May
 * be used to either select an existing size or the create a new one.
 *
 * @author Tuukka Haapasalo
 */
public class SelectSizeForDrinkActivity extends JalkametriDBActivity {

    private static final String KEY_SELECTED_SIZE = "selected_size";

    private DrinkSizeSelector drinkSizeSelector;
    private DrinkSize selection;

    public SelectSizeForDrinkActivity() {
        super(R.string.title_select_size_for_drink, NO_HELP_TEXT);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drink_size_select);

        initializeComponents();

        // Initialize the drink size selector
        drinkSizeSelector = new DrinkSizeSelector(this, adapter, true, true,
            Common.DIALOG_SELECT_SIZE_ICON);
        drinkSizeSelector.initializeComponents(null);
        selection = drinkSizeSelector.getDrinkSize();
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
        // Drink item
        {
            DrinkSize sel = (DrinkSize) savedInstanceState.get(KEY_SELECTED_SIZE);
            if (sel != null) {
                this.selection = sel;
            }
        }

        updateUIFromSelection();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateSelectionFromUI();
        outState.putSerializable(KEY_SELECTED_SIZE, selection);
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    @Override
    public void updateUI() {
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
        setResult(RESULT_OK, DrinkActivities.createDrinkSizeResult(selection, null));
        finish();
    }

    public void updateUIFromSelection() {
        assert selection != null;
        drinkSizeSelector.setDrinkSize(selection, false);
    }

    public void updateSelectionFromUI() {
        DrinkSize size = drinkSizeSelector.getDrinkSize();
        selection = size;
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
