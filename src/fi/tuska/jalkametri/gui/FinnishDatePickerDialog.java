package fi.tuska.jalkametri.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.util.LogUtil;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Fixes the month showing in a Finnish date picker dialog.
 * 
 * @author haapatu
 */
public class FinnishDatePickerDialog extends DatePickerDialog {

    private static final String TAG = "FinnishDatePickerDialog";

    private static final Locale FINNISH_LOCALE = new Locale("fi");

    private final int initialMonthOfYear;

    public FinnishDatePickerDialog(Context context, OnDateSetListener callBack, int year,
        int monthOfYear, int dayOfMonth) {
        super(context, callBack, year, monthOfYear, dayOfMonth);
        initialMonthOfYear = monthOfYear;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FINNISH_LOCALE.equals(getContext().getResources().getConfiguration().locale)) {
            // Only fix the layout for the Finnish language
            fixPickerLayout();
        }
    }

    /**
     * Fixes the date picker layout so that months are shown properly in
     * Finnish. Fetches all the required objects at the beginning, and returns
     * without doing any modifications if some of the objects could not be
     * retrieved (which could happen if the Android DatePicker implementation
     * changes).
     */
    public void fixPickerLayout() {
        // First, find the date picker object
        DatePicker picker = (DatePicker) getPrivateFieldValue(this, DatePickerDialog.class,
            "mDatePicker");
        if (picker == null)
            return;

        // Then, find the month picker object
        LinearLayout monthPicker = (LinearLayout) getPrivateFieldValue(picker, DatePicker.class,
            "mMonthPicker");
        if (monthPicker == null)
            return;

        // Fetch the month text field itself
        EditText monthTextField = (EditText) getPrivateFieldValue(monthPicker,
            monthPicker.getClass(), "mText");
        if (monthTextField == null)
            return;

        // Set the weight sum to 1 so that the height of the month name
        // can be stretched
        monthPicker.setWeightSum(1);
        // Set the month picker to fill the entire vertical space
        monthPicker.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
            LayoutParams.FILL_PARENT, 1f));

        // Change the shown month names
        if (!setMonthNames(monthPicker, monthPicker.getClass(), 1, 12, getMonthNames())) {
            LogUtil.w(TAG, "Setting the month names has failed");
        }

        // Set a bit smaller text size
        monthTextField.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        // Center the text (there will be extra space around it now)
        monthTextField.setGravity(Gravity.CENTER);
        // Set the weight to 1 so that the name field will be stretched
        monthTextField.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
            LayoutParams.WRAP_CONTENT, 1f));
    }

    public String[] getMonthNames() {
        Resources res = getContext().getResources();
        return new String[] { res.getString(R.string.month_short_1),
            res.getString(R.string.month_short_2), res.getString(R.string.month_short_3),
            res.getString(R.string.month_short_4), res.getString(R.string.month_short_5),
            res.getString(R.string.month_short_6), res.getString(R.string.month_short_7),
            res.getString(R.string.month_short_8), res.getString(R.string.month_short_9),
            res.getString(R.string.month_short_10), res.getString(R.string.month_short_11),
            res.getString(R.string.month_short_12) };
    }

    /**
     * Invokes the setRange and setCurrent methods on the (hidden, Android
     * internal) NumberPicker class.
     */
    public boolean setMonthNames(Object object, Class<?> declaringClass, int start, int end,
        String[] values) {
        try {
            Method setRange = declaringClass.getMethod("setRange", int.class, int.class,
                String[].class);
            Method setCurrent = declaringClass.getMethod("setCurrent", int.class);

            // Call setRange to update the month names
            setRange.invoke(object, start, end, values);

            // Call setCurrent to set the initial selected month
            // initialMonthOfYear is zero-based, so add one
            setCurrent.invoke(object, initialMonthOfYear + 1);
            return true;
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return false;
    }

    public Object getPrivateFieldValue(Object object, Class<?> declaringClass, String fieldName) {
        try {
            // getDeclaredField retrieves private fields also, but doesn't
            // check other classes (makes sense!)
            Field d = declaringClass.getDeclaredField(fieldName);
            d.setAccessible(true);
            return d.get(object);
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }

}
