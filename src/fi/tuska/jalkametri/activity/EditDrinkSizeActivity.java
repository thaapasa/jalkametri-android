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
        super(R.string.title_edit_drink_size, NO_HELP_TEXT);
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
        setContentView(R.layout.drink_size_modify);
        super.onCreate(savedInstanceState);

        // Load the preparation data from intents
        Bundle extras = getIntent().getExtras();
        this.selectedSize = (DrinkSize) extras.get(KEY_SELECTED_SIZE);
        this.originalId = selectedSize.getIndex();

        initializeComponents();

        // Initialize the drink size selector
        drinkSizeSelector = new DrinkSizeSelector(this, adapter, true, true,
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
