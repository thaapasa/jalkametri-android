package fi.tuska.jalkametri.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.DrinkActivities;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.data.DrinkActions;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.db.DrinkLibraryDB;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.gui.Confirmation;
import fi.tuska.jalkametri.gui.DragDropGridView;
import fi.tuska.jalkametri.gui.NamedIconAdapter;
import fi.tuska.jalkametri.util.LogUtil;

import java.util.List;

import static android.view.Gravity.CENTER;
import static fi.tuska.jalkametri.Common.DEFAULT_ICON_RES;
import static org.joda.time.Instant.now;

/**
 * An activity for selecting a drink category. The path for selecting a drink
 * starts from this activity, commonly via DrinkActions.startSelectDrink()
 * which will fire up this activity.
 * <p>
 * <p>
 * The full drink selecting path is SelectDrinkCategoryActivity -
 * SelectDrinkTypeActivity - SelectDrinkSizeActivity -
 * EditDrinkDetailsActivity.
 *
 * @author Tuukka Haapasalo
 */
public class SelectDrinkCategoryActivity extends JalkametriDBActivity {

    private static final String TAG = "SelectDrinkCategoryActivity";
    private static final int NUM_PREVIOUS_DRINKS = 5;

    // private DragDropGridView categoryList;
    private GridView categoryList;
    private GridView prevDrinkList;
    private NamedIconAdapter<DrinkCategory> categoryAdapter;
    private NamedIconAdapter<DrinkEvent> prevDrinkAdapter;

    private DrinkLibrary library;
    private History history;

    // Called when drink library has been reset
    private final Runnable drinkLibraryResetHandler = new Runnable() {
        @Override
        public void run() {
            updateUI();
        }
    };

    public SelectDrinkCategoryActivity() {
        super(R.string.title_select_drink_category, R.string.help_drink_category);
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
        setContentView(R.layout.activity_select_category);

        this.library = new DrinkLibraryDB(getDb());
        this.history = new HistoryDB(getDb(), this);

        this.categoryList = (GridView) findViewById(R.id.list);
        this.prevDrinkList = (GridView) findViewById(R.id.previous_list);

        initComponents();
        updateUI();
    }

    /*
     * Activity item initialization
     * --------------------------------------------
     */
    @Override
    public void updateUI() {
        loadLibraries(library);
        loadPreviousDrinks(history);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check drink library initialization here so that the progress dialog
        // is shown properly.
        ensureDrinkLibraryIsInitialized();
    }

    /**
     * One-time initialization of components
     */
    private void initComponents() {
        if (categoryList instanceof DragDropGridView) {
            DragDropGridView catDD = (DragDropGridView) categoryList;
            catDD.setDragListener(new DragDropGridView.DragListener() {
                @Override
                public void drag(int from, int to) {
                    LogUtil.INSTANCE.i(TAG, "Drag from %d to %d", from, to);
                }
            });
            catDD.setDropListener(new DragDropGridView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    LogUtil.INSTANCE.i(TAG, "Drop from %d to %d", from, to);
                }
            });
        }

        categoryList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrinkCategory cat = categoryAdapter.getItem(position);
                selectCategory(cat);
            }
        });
        registerForContextMenu(categoryList);

        prevDrinkList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrinkEvent selection = prevDrinkAdapter.getItem(position);
                selection.setTime(now());
                setResult(RESULT_OK, DrinkActivities.createDrinkSelectionResult(selection, null));
                finish();
            }
        });
        prevDrinkList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position,
                                           long arg3) {
                DrinkEvent selection = prevDrinkAdapter.getItem(position);
                DrinkActivities.startSelectDrinkDetails(SelectDrinkCategoryActivity.this,
                        selection);
                return true;
            }
        });
    }

    private void loadLibraries(DrinkLibrary library) {
        List<DrinkCategory> cats = library.getCategories();
        categoryAdapter = new NamedIconAdapter<>(this, cats,
                DEFAULT_ICON_RES, true, CENTER, null);
        categoryList.setAdapter(categoryAdapter);
        LogUtil.INSTANCE.d(TAG, "Loaded %d categories", cats.size());
    }

    private void loadPreviousDrinks(History history) {
        List<DrinkEvent> prevDrinks = history.getPreviousDrinks(NUM_PREVIOUS_DRINKS);
        prevDrinkAdapter = new NamedIconAdapter<>(this, prevDrinks,
                DEFAULT_ICON_RES, true, CENTER, null);
        prevDrinkList.setAdapter(prevDrinkAdapter);
        LogUtil.INSTANCE.d(TAG, "Loaded %d previous drinks", prevDrinks.size());
    }

    private void ensureDrinkLibraryIsInitialized() {
        if (getPrefs().isDrinkLibraryInitialized())
            return;

        // Library will be initialized if it has never been initialized, or if
        // the database has been recreated
        DrinkActions.resetDrinkLibrary(this, drinkLibraryResetHandler);

        Editor e = getPrefs().edit();
        getPrefs().setDrinkLibraryInitialized(e, true);
        e.commit();
    }

    /*
     * Custom actions
     * ----------------------------------------------------------
     */
    protected void selectCategory(DrinkCategory cat) {
        LogUtil.INSTANCE.d(TAG, "Selected category %s", cat.getName());

        Intent i = new Intent(this, SelectDrinkTypeActivity.class);
        // Prepare the intent for showing the selected category
        SelectDrinkTypeActivity.prepareForCategory(i, cat);
        startActivityForResult(i, Common.ACTIVITY_CODE_SELECT_DRINK_TYPE);
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
        if (view == categoryList) {
            AdapterContextMenuInfo mi = (AdapterContextMenuInfo) info;
            int pos = mi.position;
            if (pos != AdapterView.INVALID_POSITION) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.category_actions, menu);
                DrinkCategory sel = categoryAdapter.getItem(pos);
                menu.setHeaderTitle(sel.getIconText(getResources()));
                return;
            }

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo mi = (AdapterContextMenuInfo) item.getMenuInfo();
        int pos = mi.position;
        if (pos != AdapterView.INVALID_POSITION) {
            final DrinkCategory sel = categoryAdapter.getItem(pos);
            switch (item.getItemId()) {
                case R.id.action_delete:
                    // Confirm deletion
                    LogUtil.INSTANCE.i(TAG, "Request to delete category %s", sel);

                    // Ask the user if they really wish to delete this category
                    Confirmation.showConfirmation(this, R.string.confirm_delete_category,
                            new Runnable() {
                                @Override
                                public void run() {
                                    DrinkActions.deleteDrinkCategory(library, sel,
                                            SelectDrinkCategoryActivity.this);
                                }
                            });
                    return true;
                case R.id.action_modify:
                    // Start modifying the category
                    DrinkActivities.startModifyCategory(this, sel);
                    return true;
            }
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Return from activities
     * --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.INSTANCE.d(TAG, "Result %d for %d", resultCode, requestCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Common.ACTIVITY_CODE_SELECT_DRINK_TYPE:
                case Common.ACTIVITY_CODE_SELECT_DRINK_DETAILS:
                    // A drink is selected; forward OK result back to caller
                    setResult(RESULT_OK, data);
                    finish();
                    break;

                case Common.ACTIVITY_CODE_ADD_CATEGORY: {
                    // Add a new category
                    LogUtil.INSTANCE.d(TAG, "Add category data");
                    DrinkCategory category = DrinkActivities.getCategoryFromResult(data);
                    DrinkActions.createDrinkCategory(library, category, this);
                }
                break;

                case Common.ACTIVITY_CODE_EDIT_CATEGORY: {
                    // Edit a category
                    LogUtil.INSTANCE.d(TAG, "Edit category data");
                    Bundle extras = data.getExtras();
                    long id = extras.getLong(Common.KEY_ORIGINAL);
                    DrinkCategory category = DrinkActivities.getCategoryFromResult(data);
                    DrinkActions.updateDrinkCategory(library, id, category, this);
                }
                break;
            }
        }
    }

    /*
     * Options menu handling
     * --------------------------------------------------
     */

    /**
     * Create the options menu for the category selection activity.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_default_drinks:
                // Ask the user if they really wish to reset the drinks library
                Confirmation.showConfirmation(this, R.string.confirm_default_drinks, new Runnable() {
                    @Override
                    public void run() {
                        DrinkActions.resetDrinkLibrary(SelectDrinkCategoryActivity.this,
                                drinkLibraryResetHandler);
                    }
                });
                return true;

            case R.id.action_add_category:
                DrinkActivities.startAddCategory(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
