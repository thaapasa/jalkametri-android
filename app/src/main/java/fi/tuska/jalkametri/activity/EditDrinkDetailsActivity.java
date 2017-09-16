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

import static fi.tuska.jalkametri.Common.KEY_ORIGINAL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.CommonActivities;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.data.DrinkStrengthCalculator;
import fi.tuska.jalkametri.data.IconName;
import fi.tuska.jalkametri.gui.DrinkSizeSelector;
import fi.tuska.jalkametri.gui.IconPickerDialog;
import fi.tuska.jalkametri.gui.IconView;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.NumberUtil;
import fi.tuska.jalkametri.util.ObjectCallback;
import fi.tuska.jalkametri.util.TimeUtil;

/**
 * Selects the drink details. This activity can be used as part of the drink
 * selecting path, or it can be fired up directly with the known drink
 * details.
 *
 * <p>
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkTypeActivity - SelectDrinkSizeActivity -
 * EditDrinkDetailsActivity.
 *
 * @author Tuukka Haapasalo
 */
public class EditDrinkDetailsActivity extends JalkametriDBActivity {

    private static final String KEY_OK_BUTTON_TITLE = "ok_button_title";
    private static final String KEY_SELECTED_DRINK_SELECTION = "selected_drink_selection";
    private static final String KEY_SHOW_TIME_PICKER = "show_time_picker";
    private static final String KEY_SHOW_SIZE_ICON_EDIT = "show_size_icon_edit";
    private static final String KEY_SHOW_SIZE_SELECTION = "show_size_selection";

    private static final String TAG = "SelectDrinkDetailsActivity";

    private EditText nameEdit;
    private EditText strengthEdit;
    private EditText commentEdit;
    private TimePicker timePicker;
    private EditText dateEdit;
    private View dateEditorArea;
    private TextView dateEditText;
    private IconView iconView;
    private boolean showTimeSelection = true;
    private DrinkSizeSelector drinkSizeSelector;
    private Date selectedDate;

    private DateFormat dateEditFormatter;

    private DrinkSelection selection;
    private long originalID = 0;

    private final ObjectCallback<IconName> iconNameCallback = new ObjectCallback<IconName>() {
        @Override
        public void objectSelected(IconName icon) {
            // Update icon
            LogUtil.d(TAG, "Selecting icon %s", icon.getIcon());
            iconView.setIcon(icon);
        }
    };

    public EditDrinkDetailsActivity() {
        super(R.string.title_edit_drink_details, R.string.help_edit_drink);
        setShowDefaultHelpMenu(true);
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /**
     * @return true if the drink selection was OK (all required information
     * was found); false if the selection could not be prepared.
     */
    public static boolean prepareForDrinkSelection(Context parent, Intent intent,
        DrinkSelection sel) {
        Resources res = parent.getResources();
        if (sel.getDrink() == null) {
            Toast.makeText(parent, res.getText(R.string.msg_drink_not_set), Toast.LENGTH_SHORT)
                .show();
            return false;
        }
        if (sel.getSize() == null) {
            Toast.makeText(parent, res.getText(R.string.msg_drink_size_not_set),
                Toast.LENGTH_SHORT).show();
            return false;
        }
        if (sel.getTime() == null) {
            Date time = new Date();
            sel.setTime(time);
        }

        LogUtil.d(TAG, "Preparing to edit details for %s", sel);
        intent.putExtra(KEY_SELECTED_DRINK_SELECTION, sel);
        intent.putExtra(KEY_OK_BUTTON_TITLE, res.getString(R.string.action_drink));
        intent.putExtra(KEY_SHOW_TIME_PICKER, true);
        intent.putExtra(KEY_SHOW_SIZE_ICON_EDIT, false);
        intent.putExtra(KEY_SHOW_SIZE_SELECTION, true);
        return true;
    }

    public static void prepareForDrinkEventModification(Context parent, Intent intent,
        DrinkEvent event, boolean showTime, boolean showSizeSelection, boolean showSizeIconEdit) {
        Resources res = parent.getResources();
        LogUtil.d(TAG, "Preparing to edit details for %s", event);
        intent.putExtra(KEY_SELECTED_DRINK_SELECTION, event);
        intent.putExtra(KEY_ORIGINAL, Long.valueOf(event.getIndex()));
        intent.putExtra(KEY_OK_BUTTON_TITLE, res.getString(R.string.action_ok));
        intent.putExtra(KEY_SHOW_TIME_PICKER, showTime);
        intent.putExtra(KEY_SHOW_SIZE_ICON_EDIT, showSizeIconEdit);
        intent.putExtra(KEY_SHOW_SIZE_SELECTION, showSizeSelection);
    }

    public static void prepareForDrinkModification(Context context, Intent intent, Drink drink) {
        Resources res = context.getResources();
        LogUtil.d(TAG, "Preparing to edit details for %s", drink);
        // For convenience, this editor always edits drink events, so create a
        // dummy event
        DrinkEvent event = new DrinkEvent(drink, new DrinkSize(),
            new TimeUtil(context).getCurrentTime());
        intent.putExtra(KEY_SELECTED_DRINK_SELECTION, event);
        // We are modifying the drink now, so store the drink identifier
        intent.putExtra(KEY_ORIGINAL, Long.valueOf(drink.getIndex()));
        intent.putExtra(KEY_OK_BUTTON_TITLE, res.getString(R.string.action_ok));
        intent.putExtra(KEY_SHOW_TIME_PICKER, false);
        intent.putExtra(KEY_SHOW_SIZE_ICON_EDIT, false);
        intent.putExtra(KEY_SHOW_SIZE_SELECTION, false);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.drink_details);
        super.onCreate(savedInstanceState);

        dateEditFormatter = new SimpleDateFormat(getResources().getString(R.string.date_format));
        selectedDate = timeUtil.getCurrentTime();

        Bundle extras = getIntent().getExtras();
        selection = (DrinkSelection) extras.get(KEY_SELECTED_DRINK_SELECTION);
        assert selection != null;

        originalID = extras.getLong(KEY_ORIGINAL);

        showTimeSelection = extras.getBoolean(KEY_SHOW_TIME_PICKER);
        if (!showTimeSelection) {
            View v = findViewById(R.id.time_edit_area);
            v.setVisibility(View.GONE);
        }
        boolean showSizeIconEdit = extras.getBoolean(KEY_SHOW_SIZE_ICON_EDIT);
        boolean showSizeSelection = extras.getBoolean(KEY_SHOW_SIZE_SELECTION);

        nameEdit = (EditText) findViewById(R.id.name_edit);
        strengthEdit = (EditText) findViewById(R.id.strength_edit);
        commentEdit = (EditText) findViewById(R.id.comment_edit);
        timePicker = (TimePicker) findViewById(R.id.time_edit);
        timePicker.setIs24HourView(true);
        iconView = (IconView) findViewById(R.id.icon);

        dateEdit = (EditText) findViewById(R.id.date_edit);
        dateEdit.setOnClickListener(dateClickListener);
        dateEditorArea = findViewById(R.id.date_edit_area);

        dateEditText = (TextView) findViewById(R.id.date_edit_text);

        Button okButton = (Button) findViewById(R.id.drink_button);
        String okTitle = extras.getString(KEY_OK_BUTTON_TITLE);
        if (okTitle != null) {
            okButton.setText(okTitle);
        }

        initializeComponents();

        // Initialize the drink size selector
        drinkSizeSelector = new DrinkSizeSelector(this, adapter, showSizeSelection,
            showSizeIconEdit, Common.DIALOG_SELECT_SIZE_ICON);
        drinkSizeSelector.initializeComponents(selection.getSize());

        tryToHideSoftKeyboard(nameEdit);

        // Update the view
        invalidateView();
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
            DrinkSelection sel = (DrinkSelection) savedInstanceState
                .get(KEY_SELECTED_DRINK_SELECTION);
            if (sel != null) {
                this.selection = sel;
            }
        }

        updateUIFromSelection();
        updateDateEditorShown();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        updateSelectionFromUI();
        outState.putSerializable(KEY_SELECTED_DRINK_SELECTION, selection);
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    @Override
    public void updateUI() {
    }

    private void initializeComponents() {
        updateDateEditorShown();
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    public boolean isModifying() {
        return originalID > 0;
    }

    public void updateDateEditorShown() {
        dateEditorArea.setVisibility(isModifying() && showTimeSelection ? View.VISIBLE
            : View.GONE);
    }

    public void onOKPressed(View okButton) {
        updateSelectionFromUI();
        setResult(RESULT_OK, DrinkActivities.createDrinkSelectionResult(selection, originalID));
        finish();
    }

    public void onClickIcon(View v) {
        LogUtil.d(TAG, "Selecting icon...");
        showDialog(Common.DIALOG_SELECT_ICON);
    }

    public void updateUIFromSelection() {
        assert selection != null;
        // Drink type details
        {
            Drink drink = selection.getDrink();
            assert drink != null;
            nameEdit.setText(drink.getName());
            strengthEdit.setText(NumberUtil.toString(drink.getStrength(), getResources()));
            String icon = drink.getIcon();
            iconView.setIcon(icon);
            commentEdit.setText(drink.getComment());
        }
        // Drink size details
        {
            DrinkSize size = selection.getSize();
            drinkSizeSelector.setDrinkSize(size, false);
        }
        // Drink time
        {
            Date drinkTime = selection.getTime();
            assert drinkTime != null;
            Calendar cal = Calendar.getInstance();
            cal.setTime(drinkTime);
            timePicker.setCurrentHour(cal.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(cal.get(Calendar.MINUTE));
            setSelectedDate(drinkTime);
        }
    }

    public void setSelectedDate(Date date) {
        selectedDate = date;
        dateEdit.setText(dateEditFormatter.format(selectedDate));
    }

    public void updateSelectionFromUI() {
        // Drink type details
        {
            Drink drink = selection.getDrink();
            assert drink != null;
            drink.setName(nameEdit.getText().toString());
            drink.setStrength(NumberUtil.readDouble(strengthEdit.getText().toString(),
                prefs.getLocale()));
            String icon = iconView.getIcon().getIcon();
            drink.setIcon(icon);
            drink.setComment(commentEdit.getText().toString());
        }
        // Drink size details
        {
            DrinkSize size = drinkSizeSelector.getDrinkSize();
            selection.setSize(size);
        }
        // Drink time
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            if (isModifying()) {
                // Set time from selected date
                cal.setTime(selectedDate);
            }

            cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
            cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
            // Seconds come from the current time
            Calendar now = Calendar.getInstance();

            if (!isModifying()) {
                // Assume current day
                if (cal.after(now)) {
                    cal.add(Calendar.DAY_OF_MONTH, -1);
                    assert !cal.after(now);
                }
            }
            selection.setTime(cal.getTime());
        }
    }

    /**
     * Show the drink calculator.
     */
    @Override
    public void showDrinkCalculator(View v) {
        updateSelectionFromUI();
        // Use the values from current selection as the basis of the
        // calculator
        CommonActivities.showCalculator(this, selection);
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
        case Common.DIALOG_SELECT_SIZE_ICON:
            dialog = new IconPickerDialog(this, drinkSizeSelector.getSetSizeIconCallback());
            return dialog;
        }
        return super.onCreateDialog(id);
    }

    /*
     * Return from activities
     * --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.d(TAG, "Return from activity %d; result %d", requestCode, resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

            case Common.ACTIVITY_CODE_SHOW_CALCULATOR: {
                // Returning from calculator
                DrinkStrengthCalculator calc = CalculatorActivity.getCalculatorFromResult(data);
                if (calc != null) {
                    // Update strength based on calculated value
                    double strength = calc.getStrength();
                    LogUtil.d(TAG, "Setting strength to %f", strength);
                    selection.getDrink().setStrength(strength);
                    updateUIFromSelection();
                }
            }

            }
        }
    }

    /**
     * OnClickListener for the date edit field.
     */
    private final OnClickListener dateClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Calendar cal = timeUtil.getCalendar(selectedDate);
            DatePickerDialog fdp = new DatePickerDialog(
                EditDrinkDetailsActivity.this, new OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                        int dayOfMonth) {
                        setSelectedDate(timeUtil.getCalendarFromDatePicker(year, monthOfYear,
                            dayOfMonth).getTime());
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
            fdp.show();
        }
    };

}
