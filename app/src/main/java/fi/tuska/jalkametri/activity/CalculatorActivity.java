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

import java.util.Locale;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkStrengthCalculator;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.NumberUtil;

/**
 * A simple drink strength calculator.
 * 
 * @author Tuukka Haapasalo
 */
public class CalculatorActivity extends JalkametriActivity {

    private static final String TAG = "CalculatorActivity";

    private static final String KEY_RESULT_CALC = "result_drink_calc";
    private static final String KEY_STORE_CALC = "store_drink_calc";
    private static final String KEY_DRINK_SELECTION = "drink_selection";

    private EditText strengthEdit;
    private EditText volumeEdit;
    private TextView strengthShow;
    private TextView volumeShow;
    private TextView portionsShow;

    private DrinkStrengthCalculator calc;

    public CalculatorActivity() {
        super(R.string.title_calculator, R.string.help_calculator);
        setShowDefaultHelpMenu(true);
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /**
     * Call to prepare an intent for showing the selected category.
     */
    public static void prepareForDrink(Intent intent, DrinkSelection drink) {
        intent.putExtra(KEY_DRINK_SELECTION, drink);
    }

    public static Intent createCalculatorResult(DrinkStrengthCalculator calc) {
        Intent data = new Intent();
        data.putExtra(KEY_RESULT_CALC, calc);
        return data;
    }

    public static DrinkStrengthCalculator getCalculatorFromResult(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null)
            return null;
        DrinkStrengthCalculator sel = (DrinkStrengthCalculator) extras.get(KEY_RESULT_CALC);
        return sel;
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculator);

        this.calc = new DrinkStrengthCalculator();

        this.strengthEdit = (EditText) findViewById(R.id.strength_edit);
        this.volumeEdit = (EditText) findViewById(R.id.volume_edit);
        this.strengthShow = (TextView) findViewById(R.id.strength_show);
        this.volumeShow = (TextView) findViewById(R.id.volume_show);
        this.portionsShow = (TextView) findViewById(R.id.portions_show);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            DrinkSelection drink = (DrinkSelection) extras.get(KEY_DRINK_SELECTION);
            if (drink != null) {
                Resources res = getResources();
                this.strengthEdit.setText(NumberUtil
                    .toString(drink.getDrink().getStrength(), res));
                this.volumeEdit.setText(NumberUtil.toString(drink.getSize().getVolume(), res));
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        DrinkStrengthCalculator c = (DrinkStrengthCalculator) savedInstanceState
            .get(KEY_STORE_CALC);
        if (c != null) {
            this.calc = c;
        }
        updateUI();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_STORE_CALC, calc);
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    public void onOKPressed(View v) {
        setResult(RESULT_OK, createCalculatorResult(calc));
        finish();
    }

    public void onClearPressed(View v) {
        LogUtil.d(TAG, "Clearing calculator");

        // Clear the drink strength calculator
        calc.clear();
        updateUI();
    }

    public void onAddPressed(View v) {
        Locale locale = prefs.getLocale();
        double vol = NumberUtil.readDouble(volumeEdit.getText().toString(), locale);
        double str = NumberUtil.readDouble(strengthEdit.getText().toString(), locale);
        LogUtil.d(TAG, "Adding selected component: %f liters of %f %%", vol, str);
        calc.addComponent(vol, str);

        updateUI();
    }

    @Override
    public void updateUI() {
        Resources res = getResources();
        // Update the mix
        strengthShow.setText(NumberUtil.toString(calc.getStrength(), res));
        volumeShow.setText(NumberUtil.toString(calc.getVolume(), res));
        portionsShow.setText(NumberUtil.toString(calc.getPortions(getContext()), res));
        LogUtil.d(TAG, "Alcohol weight is %f", calc.getAlcoholWeight());
    }

}
