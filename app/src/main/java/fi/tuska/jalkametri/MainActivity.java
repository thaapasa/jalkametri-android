/**
 * Copyright 2006-2011 Tuukka Haapasalo
 *
 * This file is part of jAlkaMetri.
 *
 * jAlkaMetri is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with jAlkaMetri (LICENSE.txt). If not, see
 * <http://www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri;

import static fi.tuska.jalkametri.Common.KEY_ORIGINAL;
import static fi.tuska.jalkametri.Common.KEY_RESULT;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import fi.tuska.jalkametri.activity.GUIActivity;
import fi.tuska.jalkametri.activity.JalkametriDBActivity;
import fi.tuska.jalkametri.dao.DrinkStatus;
import fi.tuska.jalkametri.dao.Favourites;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.HistoryHelper;
import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkStatusCalc;
import fi.tuska.jalkametri.data.PurchaseReminderHandler;
import fi.tuska.jalkametri.db.FavouritesDB;
import fi.tuska.jalkametri.db.HistoryDB;
import fi.tuska.jalkametri.gui.AlcoholLevelView;
import fi.tuska.jalkametri.gui.DrinkDetailsDialog;
import fi.tuska.jalkametri.gui.NamedIconAdapter;
import fi.tuska.jalkametri.task.AlcoholLevelMeter;
import fi.tuska.jalkametri.util.AssertionUtils;
import fi.tuska.jalkametri.util.DialogUtil;
import fi.tuska.jalkametri.util.LocalizationUtil;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.StringUtil;

/**
 * Main activity class: shows status information and links to other activities.
 *
 * @author Tuukka Haapasalo
 */
public class MainActivity extends JalkametriDBActivity implements GUIActivity {

    private static final String TAG = "MainActivity";

    private static final String KEY_SHOWN_FAVOURITE = "shown_favourite";
    private static final String PORTIONS_FORMAT = "%.1f / %.1f / %.1f";
    private static final int DIALOG_DRINK_DETAILS = 1;

    private History history;
    private Favourites favourites;

    private AlcoholLevelMeter meter;
    private AlcoholLevelView alcoholLevel;
    private AlcoholLevelAnimation gaugeAnimation = new AlcoholLevelAnimation();

    private TextView sobrietyText;
    private TextView portionsText;
    private TextView drinkDateText;
    private TextView addFavouritesPrompt;
    private ImageView carStatusView;

    private GridView favouritesList;
    private View developmentView;
    private NamedIconAdapter<DrinkEvent> favouritesAdapter;

    private DrinkEvent shownFavourite;
    private DateFormat wdayFormat;
    private DateFormat timeFormat;

    public MainActivity() {
        super(R.string.app_name, R.string.help_main);
    }

    /*
     * Standard activity functions --------------------------------------------
     */

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.main);
        super.onCreate(savedInstanceState);

        LogUtil.i(TAG, "Creating jAlkaMetri application");
        LogUtil.d(TAG, "Assertions are %s", (AssertionUtils.isAssertionsEnabled() ? "on" : "off"));

        this.history = new HistoryDB(adapter, this);
        this.meter = new AlcoholLevelMeter(history, this);
        this.favourites = new FavouritesDB(adapter, this);
        this.wdayFormat = timeUtil.getDateFormatWDay();
        this.timeFormat = timeUtil.getTimeFormat();

        this.alcoholLevel = (AlcoholLevelView) findViewById(R.id.status_image);
        alcoholLevel.setLevel(0.6f, DrinkStatus.DrivingState.DrivingMaybe);

        this.developmentView = findViewById(R.id.development_view);
        registerForContextMenu(developmentView);

        this.carStatusView = (ImageView) findViewById(R.id.status_car);
        this.sobrietyText = (TextView) findViewById(R.id.sober_text);
        this.portionsText = (TextView) findViewById(R.id.portions_text);
        this.drinkDateText = (TextView) findViewById(R.id.drink_date_text);
        this.addFavouritesPrompt = (TextView) findViewById(R.id.add_favourites_prompt);

        this.favouritesList = (GridView) findViewById(R.id.favourites);
        favouritesList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DrinkSelection favorite = favouritesAdapter.getItem(position);
                DrinkSelection sel = new DrinkSelection(favorite.getDrink(), favorite.getSize(), timeUtil
                    .getCurrentTime());
                consumeDrink(sel);
            }
        });
        registerForContextMenu(favouritesList);

        updateFavourites();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI();

        if (!prefs.isDisclaimerRead()) {
            CommonActivities.showDisclaimer(this);
        }

        // Force widget update, in case widget updating thread is dead
        JalkametriWidget.triggerRecalculate(this, adapter);

        if (isFirstRunAfterCreate()) {
            PurchaseReminderHandler.showReminderIfNecessary(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_SHOWN_FAVOURITE, shownFavourite);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        shownFavourite = (DrinkEvent) state.get(KEY_SHOWN_FAVOURITE);
    }

    /*
     * Activity item initialization --------------------------------------------
     */
    @Override
    public void updateUI() {
        DrinkStatus status = meter.getDrinkStatus();
        DrinkStatus.DrivingState drivingState = status.getDrivingState(prefs);
        alcoholLevel.setLevel(status.getAlcoholLevel(), drivingState);
        setCarImage(drivingState);
        updateDrinkDateText();
        updateSobriety(status);
        updatePortionsText(status, history);
    }

    private void updatePortionsText(DrinkStatus status, History history) {
        Date shownDay = timeUtil.getCurrentDrinkingDate(prefs);
        double todayPortions = HistoryHelper.countDayPortions(history, shownDay, this);
        double weekPortions = HistoryHelper.countWeekPortions(history, shownDay, this);
        double totalPortions = history.countTotalPortions();
        portionsText.setText(String.format(PORTIONS_FORMAT, todayPortions, weekPortions, totalPortions));
    }

    private void updateDrinkDateText() {
        Date today = timeUtil.getCurrentDrinkingDate(prefs);
        drinkDateText.setText(StringUtil.uppercaseFirstLetter(wdayFormat.format(today)));
    }

    private void updateSobriety(DrinkStatus status) {
        Resources res = getResources();
        double level = status.getAlcoholLevel();
        if (level <= 0) {
            sobrietyText.setText(res.getString(R.string.sober));
        } else {
            double hours = status.getHoursToSober();
            double minutes = (hours - (int) hours) * 60;
            Date soberity = timeUtil.getTimeAfterHours(hours);
            if (hours > 1) {
                sobrietyText.setText(String.format("%d%s %d%s (%s)", (int) hours, res.getString(R.string.hour),
                    (int) minutes, res.getString(R.string.minute), timeFormat.format(soberity)));
            } else {
                sobrietyText.setText(String.format("%d %s (%s)", (int) minutes, res.getString(R.string.minute),
                    timeFormat.format(soberity)));
            }
        }
    }

    private void setCarImage(DrinkStatus.DrivingState state) {
        switch (state) {
        case DrivingOK:
            carStatusView.setImageResource(R.drawable.car_ok);
            break;
        case DrivingMaybe:
            carStatusView.setImageResource(R.drawable.car_maybe);
            break;
        case DrivingNo:
            carStatusView.setImageResource(R.drawable.car_no);
            break;
        }
    }

    protected void updateFavourites() {
        List<DrinkEvent> favs = favourites.getFavourites();
        LogUtil.d(TAG, "Showing %d favourites", favs.size());
        favouritesAdapter = new NamedIconAdapter<DrinkEvent>(this, favs, true, Common.DEFAULT_ICON_RES);
        favouritesList.setAdapter(favouritesAdapter);
        JalkametriWidget.triggerRecalculate(this, adapter);
        LogUtil.d(TAG, "Recalculated widget");

        // If favourites list is empty, show the prompt; otherwise, hide it.
        addFavouritesPrompt.setVisibility(favs.isEmpty() ? View.VISIBLE : View.GONE);
    }

    /*
     * Custom actions ----------------------------------------------------------
     */
    /** Add a drink. */
    public void showAddDrink(View v) {
        CommonActivities.showAddDrink(this);
    }

    public void toastAlcoholStatus(View v) {
        // Called when the alcohol meter is clicked
        // Make toast
        DrinkActivities.makeDrinkToast(this, alcoholLevel.getLevel(), false);
    }

    public void showDrivingStatus(View v) {
        Resources res = getResources();
        DrinkStatus status = meter.getDrinkStatus();
        switch (status.getDrivingState(prefs)) {
        case DrivingOK:
            DialogUtil.showMessage(this, R.string.drive_status_ok, R.string.title_drive_status);
            break;
        case DrivingMaybe: {
            String messagePat = res.getString(R.string.drive_status_maybe);
            double toSober = status.getHoursToSober();
            String soberTime = getTimeAfterHours(toSober);
            String toSoberTime = getHoursMsg(toSober);
            String message = String.format(messagePat, toSoberTime, soberTime);
            DialogUtil.showMessage(this, message, R.string.title_drive_status);
        }
            break;
        case DrivingNo: {
            String messagePat = res.getString(R.string.drive_status_no);
            double toDrive = status.getHoursToAlcoholLevel(prefs.getDrivingAlcoholLimit());
            double toSober = status.getHoursToSober();
            String toDriveTime = getHoursMsg(toDrive);
            String toSoberTime = getHoursMsg(toSober);
            String driveTime = getTimeAfterHours(toDrive);
            String soberTime = getTimeAfterHours(toSober);
            String message = String.format(messagePat, toDriveTime, driveTime, toSoberTime, soberTime);
            DialogUtil.showMessage(this, message, R.string.title_drive_status);
        }
            break;
        }
    }

    private final String getTimeAfterHours(double afterHours) {
        return timeUtil.getTimeFormat().format(timeUtil.getTimeAfterHours(afterHours));
    }

    private final String getHoursMsg(double hours) {
        Resources res = getResources();
        if (hours >= 1) {
            int fullHours = (int) hours;
            return String.format(res.getString(R.string.hourmin_pat), fullHours, (int) ((hours - fullHours) * 60));
        } else {
            return String.format(res.getString(R.string.min_pat), (int) (hours * 60));
        }
    }

    private void consumeDrink(DrinkSelection selection) {
        // Get original alcohol level
        double orgLevel = alcoholLevel.getLevel();
        DrinkStatus.DrivingState orgState = alcoholLevel.getDrivingState();
        // Add drink
        history.createDrink(selection);
        JalkametriWidget.triggerRecalculate(this, adapter);
        // Make toast
        DrinkActivities.makeDrinkToast(this, orgLevel, true);
        updateUI();
        double newLevel = alcoholLevel.getLevel();
        // Fall back to old values
        alcoholLevel.setLevel(orgLevel, orgState);
        setCarImage(orgState);
        // Start animation
        gaugeAnimation.showAnimation(orgLevel, newLevel, 0.8);
    }

    /*
     * Context menu handling ----------------------------------------------------------
     */

    @Override
    public boolean onSearchRequested() {
        if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
            LogUtil.i(TAG, "Showing development menu");
            openContextMenu(developmentView);
            return true;
        } else {
            return super.onSearchRequested();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        int viewId = v.getId();
        switch (viewId) {

        case R.id.favourites: {
            // Show the favourites menu
            LogUtil.d(TAG, "Showing favourites menu");
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.favourite_actions, menu);
        }
            break;

        case R.id.development_view:
            // Show the development menu
            if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
                LogUtil.d(TAG, "Showing development menu");
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_developer, menu);
            }
            break;

        default:
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {

        case R.id.action_modify: {
            // Modify the selected favorite
            DrinkEvent fav = favouritesAdapter.getItem(info.position);
            LogUtil.d(TAG, "Modifying %s", fav);
            DrinkActivities.startModifyDrinkEvent(this, fav, true, true, false, Common.ACTIVITY_CODE_MODIFY_FAVOURITE);
            return true;
        }

        case R.id.action_show_info: {
            // Show the drink information
            DrinkEvent fav = favouritesAdapter.getItem(info.position);
            LogUtil.d(TAG, "Showing %s", fav);
            this.shownFavourite = fav;
            showDialog(DIALOG_DRINK_DETAILS);
            return true;
        }

        case R.id.action_delete: {
            // Delete the selected favorite
            DrinkEvent fav = favouritesAdapter.getItem(info.position);
            LogUtil.d(TAG, "Deleting %s", fav);
            favourites.deleteFavourite(fav.getIndex());
            updateFavourites();
            return true;
        }

        case R.id.action_drink: {
            // Drink the selected favorite
            DrinkEvent fav = favouritesAdapter.getItem(info.position);
            LogUtil.d(TAG, "Drinking %s", fav);
            DrinkActivities.startSelectDrinkDetails(this, fav, Common.ACTIVITY_CODE_SELECT_DRINK);
            return true;
        }
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Dialog handling -------------------------------------------------------------
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case DIALOG_DRINK_DETAILS:
            dialog = new DrinkDetailsDialog(this);
            return dialog;
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
        case DIALOG_DRINK_DETAILS: {
            DrinkDetailsDialog d = (DrinkDetailsDialog) dialog;
            d.showDrinkSelection(shownFavourite, false);
        }
        }
        super.onPrepareDialog(id, dialog);
    }

    /*
     * Return from activities --------------------------------------------------
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle extras = data != null ? data.getExtras() : null;
            switch (requestCode) {
            case Common.ACTIVITY_CODE_SELECT_DRINK: {
                DrinkSelection sel = DrinkActivities.getDrinkSelectionFromResult(data);
                consumeDrink(sel);
            }
                return;
            case Common.ACTIVITY_CODE_ADD_FAVOURITE: {
                DrinkSelection sel = (DrinkSelection) extras.get(KEY_RESULT);
                favourites.createFavourite(sel);
                updateFavourites();
                LogUtil.d(TAG, "Added %s to favourites", sel);
            }
                return;
            case Common.ACTIVITY_CODE_MODIFY_FAVOURITE: {
                DrinkSelection modifications = (DrinkSelection) extras.get(KEY_RESULT);
                long originalID = extras.getLong(KEY_ORIGINAL);

                DrinkEvent event = favourites.getFavourite(originalID);
                event.setDrink(modifications.getDrink());
                event.setSize(modifications.getSize());
                favourites.updateFavourite(originalID, event);

                updateFavourites();
            }
                return;
            case Common.ACTIVITY_CODE_SHOW_PREFERENCES: {
                LogUtil.i(TAG, "Updating locale to %s", prefs.getLocale());
                LocalizationUtil.setLocale(prefs.getLocale(), getBaseContext());
            }
                return;
            }
        }
    }

    /*
     * Options menu handling --------------------------------------------------
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item))
            return true;

        switch (item.getItemId()) {

        case R.id.action_add_favourite:
            // Add a favourite
            DrinkActivities.startSelectDrink(this, Common.ACTIVITY_CODE_ADD_FAVOURITE);
            return true;

        case R.id.action_legal:
            // Show legal disclaimer
            CommonActivities.showDisclaimer(this);
            return true;

        case R.id.action_about:
            // Show an about screen
            CommonActivities.showAbout(this);
            return true;

        case R.id.action_show_preferences:
            showPreferences(null);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private class AlcoholLevelAnimation extends Animation {
        private double startAlcoholLevel = 0;
        private double endAlcoholLevel = 0;

        public void showAnimation(double oldLevel, double newLevel, double duration) {
            startAlcoholLevel = oldLevel;
            endAlcoholLevel = newLevel;
            LogUtil.i(TAG, "Starting animation");
            setDuration((long) (duration * 1000l));
            setStartTime(AnimationUtils.currentAnimationTimeMillis());
            setRepeatCount(0);
            alcoholLevel.startAnimation(this);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            double showLevel = (endAlcoholLevel - startAlcoholLevel) * interpolatedTime + startAlcoholLevel;
            DrinkStatus.DrivingState showState = DrinkStatusCalc.getDrivingState(prefs, showLevel);
            LogUtil.i(TAG, "Animating at time %.2f: level %.2f", interpolatedTime, showLevel);
            alcoholLevel.setLevel(showLevel, showState);
            setCarImage(showState);
        }

    }

}
