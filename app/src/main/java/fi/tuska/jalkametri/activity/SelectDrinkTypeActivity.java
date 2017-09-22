package fi.tuska.jalkametri.activity;

import java.util.List;

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
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.gui.Confirmation;
import fi.tuska.jalkametri.gui.NamedIconAdapter;
import fi.tuska.jalkametri.util.LogUtil;

/**
 * An activity for selecting a drink type. This activity requires that the
 * drink category has been already selected (via SelectDrinkCategoryActivity).
 *
 * <p>
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkTypeActivity - SelectDrinkSizeActivity -
 * EditDrinkDetailsActivity.
 *
 * @author Tuukka Haapasalo
 */
public class SelectDrinkTypeActivity extends JalkametriDBActivity implements GUIActivity {

    private static final String TAG = "SelectDrinkTypeActivity";

    private static final String KEY_SELECTED_CATEGORY_ID = "drink_category_id";
    private static final String KEY_SELECTED_CATEGORY_NAME = "drink_category_name";

    private DrinkCategory category;

    private GridView list;
    private NamedIconAdapter<Drink> listAdapter;
    private Long catID;

    public SelectDrinkTypeActivity() {
        super(R.string.title_select_drink_type, NO_HELP_TEXT);
    }

    /*
     * Functions for preparing the activity to be shown
     * --------------------------------------------
     */

    /**
     * Call to prepare an intent for showing the selected category.
     */
    public static void prepareForCategory(Intent intent, DrinkCategory category) {
        intent.putExtra(KEY_SELECTED_CATEGORY_ID, category.getIndex());
        intent.putExtra(KEY_SELECTED_CATEGORY_NAME, category.getName());
    }

    /*
     * Standard activity functions
     * --------------------------------------------
     */

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_icon);

        Bundle extras = getIntent().getExtras();
        catID = (Long) extras.get(KEY_SELECTED_CATEGORY_ID);

        DrinkLibrary library = new DrinkLibraryDB(adapter);
        category = library.getCategory(catID);

        this.list = (GridView) findViewById(R.id.list);

        LogUtil.d(TAG, "Selected category: %s", extras.get(KEY_SELECTED_CATEGORY_NAME));

        initComponents();
        updateUI();
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    private void initComponents() {
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Drink drink = listAdapter.getItem(position);
                selectDrink(drink);
            }
        });
        registerForContextMenu(list);
    }

    @Override
    public void updateUI() {
        loadLibraries(category);
    }

    private void loadLibraries(DrinkCategory category) {
        List<Drink> drinks = category.getDrinks();
        listAdapter = new NamedIconAdapter<Drink>(this, drinks, true, Common.DEFAULT_ICON_RES);
        list.setAdapter(listAdapter);
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    private void selectDrink(Drink drink) {
        LogUtil.d(TAG, "Selected drink %s", drink.getName());

        List<DrinkSize> drinkSizes = drink.getDrinkSizes();
        if (drinkSizes.size() == 1) {
            // Only 1 drink size, select that automatically
            DrinkSize size = drinkSizes.get(0);
            if (size != null) {
                DrinkSelection sel = new DrinkSelection(drink);
                sel.setSize(size);
                sel.setTime(timeUtil.getCurrentTime());
                setResult(RESULT_OK, DrinkActivities.createDrinkSelectionResult(sel, null));
                finish();
                return;
            }
        }
        // More than one size (or no sizes at all); show drink size selection
        DrinkActivities.startSelectDrinkSize(this, drink);
    }

    /*
     * Return from activities
     * --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
            case Common.ACTIVITY_CODE_SELECT_DRINK_SIZE:
            case Common.ACTIVITY_CODE_SELECT_DRINK_DETAILS:
                setResult(RESULT_OK, data);
                finish();
                break;

            case Common.ACTIVITY_CODE_CREATE_DRINK: {
                // Insert the drink into database
                DrinkSelection drink = DrinkActivities.getDrinkSelectionFromResult(data);
                DrinkActions.createDrink(category, drink, this);
            }
                break;

            case Common.ACTIVITY_CODE_MODIFY_DRINK: {
                // Insert the drink into database
                DrinkSelection selection = DrinkActivities.getDrinkSelectionFromResult(data);
                long originalId = data.getExtras().getLong(Common.KEY_ORIGINAL);
                DrinkActions.updateDrink(category, originalId, selection.getDrink(), this);
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
        // Create the context menu for category list items (when long clicking
        // on a drink category icon)
        if (view == list) {
            AdapterContextMenuInfo mi = (AdapterContextMenuInfo) info;
            int pos = mi.position;
            if (pos != AdapterView.INVALID_POSITION) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.drink_type_actions, menu);
                Drink sel = listAdapter.getItem(pos);
                menu.setHeaderTitle(sel.getName());
                return;
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
        int pos = mi.position;
        if (pos != AdapterView.INVALID_POSITION) {
            final Drink drink = listAdapter.getItem(pos);
            DBDataObject.enforceBackedObject(drink);
            switch (item.getItemId()) {

            case R.id.action_drink:
                // Move to drink details selection
                List<DrinkSize> sizeList = drink.getDrinkSizes();
                if (sizeList.size() > 0) {
                    // Select the first drink size
                    DrinkSize size = drink.getDrinkSizes().get(0);
                    DrinkActivities.startSelectDrinkDetails(SelectDrinkTypeActivity.this,
                        new DrinkSelection(drink, size));
                } else {
                    // No drink sizes defined; must go to drink size selection
                    DrinkActivities.startSelectDrinkSize(this, drink);
                }
                return true;

            case R.id.action_delete:
                // Delete this drink
                Confirmation.showConfirmation(this, R.string.confirm_delete_drink,
                    new Runnable() {
                        @Override
                        public void run() {
                            DrinkActions.deleteDrink(category, drink,
                                SelectDrinkTypeActivity.this);
                        }
                    });
                return true;

            case R.id.action_show_sizes:
                // Show the drink size list
                DrinkActivities.startSelectDrinkSize(this, drink);
                return true;

            case R.id.action_modify:
                DrinkActivities.startModifyDrink(this, drink);
                return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Options menu handling
     * --------------------------------------------------
     */

    /**
     * Create the options menu for the drink type selection activity.
     * Contents: Add new drink (action_add_drink).
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.drinks_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add_drink:
            DrinkLibrary library = new DrinkLibraryDB(adapter);
            DrinkActivities.startCreateDrink(this, library.getDrinkSizes());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
