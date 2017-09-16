/**
 * Copyright 2006-2011 Tuukka Haapasalo
 *
 * This file is part of jAlkaMetri.
 *
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
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
        findMainView();

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
