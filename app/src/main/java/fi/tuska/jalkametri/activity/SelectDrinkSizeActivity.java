package fi.tuska.jalkametri.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.gui.DrinkDetailsDialog;
import fi.tuska.jalkametri.gui.NamedIconAdapter;
import fi.tuska.jalkametri.util.AssertionUtils;
import fi.tuska.jalkametri.util.LogUtil;

import java.util.List;

import static android.view.Gravity.CENTER;
import static fi.tuska.jalkametri.Common.DEFAULT_ICON_RES;
import static org.joda.time.Instant.now;

/**
 * Selects the drink size.
 * <p>
 * <p>
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkTypeActivity - SelectDrinkSizeActivity -
 * EditDrinkDetailsActivity.
 *
 * @author Tuukka Haapasalo
 */
public class SelectDrinkSizeActivity extends JalkametriDBActivity {

    private static final String TAG = "SelectDrinkSizeActivity";
    private static final String KEY_SELECTED_DRINK = "selected_drink";

    private GridView list;
    private NamedIconAdapter<DrinkSize> listAdapter;
    private Drink selectedDrink;

    public SelectDrinkSizeActivity() {
        super(R.string.title_select_drink_size, Companion.getNO_HELP_TEXT());
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /**
     * Call to prepare an intent for showing the size selection for the given
     * drink.
     */
    public static void prepareForDrink(Intent intent, Drink drink) {
        DBDataObject.enforceBackedObject(drink);
        intent.putExtra(KEY_SELECTED_DRINK, drink);
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_icon);

        Bundle extras = getIntent().getExtras();
        this.selectedDrink = (Drink) extras.get(KEY_SELECTED_DRINK);
        AssertionUtils.INSTANCE.expect(selectedDrink != null);
        AssertionUtils.INSTANCE.expect(selectedDrink.isBacked());

        this.list = (GridView) findViewById(R.id.list);

        LogUtil.INSTANCE.d(TAG, "Selected drink: %s", selectedDrink);

        initComponents();
        updateUI();
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    @Override
    public void updateUI() {
        DrinkLibrary library = new DrinkLibraryDB(getAdapter());
        DrinkSizes sizeProvider = library.getDrinkSizes();
        loadLibraries(sizeProvider);
    }

    private void initComponents() {
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrinkSize size = listAdapter.getItem(position);
                selectDrink(size);
            }
        });
        registerForContextMenu(list);
    }

    private void loadLibraries(DrinkSizes sizeProvider) {
        List<DrinkSize> drinkSizes = sizeProvider.getSizes(selectedDrink);
        listAdapter = new NamedIconAdapter<>(this, drinkSizes,
                DEFAULT_ICON_RES, true, CENTER, null);
        list.setAdapter(listAdapter);
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    private void selectDrink(DrinkSize size) {
        DrinkSelection sel = new DrinkSelection(selectedDrink, size);
        sel.setTime(now());
        setResult(RESULT_OK, DrinkActivities.createDrinkSelectionResult(sel, null));
        finish();
    }

    protected void showEventDetails(DrinkEvent event) {
        showCustomDialog(DrinkDetailsDialog.Companion.createDialog(event, false));
    }

    /*
     * Return from activities
     * --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                // Selected the drink, drinking it
                case Common.ACTIVITY_CODE_SELECT_DRINK_DETAILS: {
                    setResult(RESULT_OK, data);
                    finish();
                }
                break;

                // Adding a new size for this drink
                case Common.ACTIVITY_CODE_ADD_DRINK_SIZE: {
                    DrinkSize size = DrinkActivities.getDrinkSizeFromResult(data);
                    DrinkActions.addSizeToDrink(selectedDrink, size, this);
                }
                break;

                // Adding a new size for this drink
                case Common.ACTIVITY_CODE_MODIFY_DRINK_SIZE: {
                    DrinkSize size = DrinkActivities.getDrinkSizeFromResult(data);
                    long originalID = data.getLongExtra(Common.KEY_ORIGINAL, 0);
                    DrinkActions.updateDrinkSize(originalID, size, this);
                }
                break;

            }
        }
    }

    /*
     * Context menu handling
     * ----------------------------------------------------------
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo info) {
        super.onCreateContextMenu(menu, view, info);
        // Create the context menu for the size list items (when long clicking
        // on a drink size icon)
        if (view == list) {
            AdapterContextMenuInfo mi = (AdapterContextMenuInfo) info;
            int pos = mi.position;
            if (pos != AdapterView.INVALID_POSITION) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.drink_size_actions, menu);
                DrinkSize sel = listAdapter.getItem(pos);
                menu.setHeaderTitle(sel.getIconText(getResources()));
                return;
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
        int pos = mi.position;
        // Assuming that the context menu is from the list view
        if (pos != AdapterView.INVALID_POSITION) {
            switch (item.getItemId()) {

                case R.id.action_drink_size: {
                    // Drink the selected drink size
                    DrinkSize size = listAdapter.getItem(pos);
                    DrinkActivities.startSelectDrinkDetails(this, new DrinkSelection(selectedDrink,
                            size));
                }
                return true;

                case R.id.action_modify_size: {
                    // Modifies the selected drink size
                    final DrinkSize size = listAdapter.getItem(pos);
                    LogUtil.INSTANCE.i(TAG, "Request to modify drink size %s", size);
                    DrinkActivities.startModifyDrinkSize(this, size);
                }
                return true;

                case R.id.action_delete_size: {
                    // Delete the selected drink size from the drink
                    final DrinkSize size = listAdapter.getItem(pos);
                    LogUtil.INSTANCE.i(TAG, "Request to delete drink size %s", size);
                    DrinkActions.removeSizeFromDrink(selectedDrink, size, this);
                }
                return true;

                case R.id.action_show_info:
                    // Show the information for this drink size
                    DrinkEvent ev = new DrinkEvent(selectedDrink, listAdapter.getItem(pos), now());
                    showEventDetails(ev);
                    return true;

            }
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Options menu handling
     * --------------------------------------------------
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drink_size_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_size:
                DrinkActivities.startAddDrinkSize(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
