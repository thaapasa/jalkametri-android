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
 * <http:></http:>//www.gnu.org/licenses/>.
 */
package fi.tuska.jalkametri

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Transformation
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import android.widget.TextView
import fi.tuska.jalkametri.Common.*
import fi.tuska.jalkametri.activity.GUIActivity
import fi.tuska.jalkametri.activity.JalkametriDBActivity
import fi.tuska.jalkametri.activity.fragment.CurrentStatusFragment
import fi.tuska.jalkametri.dao.DrinkStatus
import fi.tuska.jalkametri.dao.DrinkStatus.DrivingState.*
import fi.tuska.jalkametri.dao.Favourites
import fi.tuska.jalkametri.dao.History
import fi.tuska.jalkametri.dao.HistoryHelper
import fi.tuska.jalkametri.data.DrinkEvent
import fi.tuska.jalkametri.data.DrinkSelection
import fi.tuska.jalkametri.data.DrinkStatusCalc
import fi.tuska.jalkametri.data.PurchaseReminderHandler
import fi.tuska.jalkametri.db.FavouritesDB
import fi.tuska.jalkametri.db.HistoryDB
import fi.tuska.jalkametri.gui.DrinkDetailsDialog
import fi.tuska.jalkametri.gui.NamedIconAdapter
import fi.tuska.jalkametri.task.AlcoholLevelMeter
import fi.tuska.jalkametri.util.*

/**
 * Main activity class: shows status information and links to other activities.
 *
 * @author Tuukka Haapasalo
 */
open class MainActivity : JalkametriDBActivity(R.string.app_name, R.string.help_main), GUIActivity {

    private var state: State? = null

    fun toastAlcoholStatus(v: View) {
        state?.let {
            DrinkActivities.makeDrinkToast(this, it.currentStatus.level, false)
        }
    }
    fun showAddDrink(v: View) {
        CommonActivities.showAddDrink(this)
    }

    fun showDrivingStatus(v: View): Unit {
        state?.showDrivingStatus(v)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        LogUtil.i(TAG, "Creating jAlkaMetri application")
        LogUtil.d(TAG, "Assertions are %s", if (AssertionUtils.isAssertionsEnabled()) "on" else "off")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        state = State(this).apply {
            updateFavourites()
        }
    }

    override fun onStart() {
        super.onStart()
        updateUI()

        if (!prefs.isDisclaimerRead) {
            CommonActivities.showDisclaimer(this)
        }

        // Force widget update, in case widget updating thread is dead
        JalkametriWidget.triggerRecalculate(this, adapter)

        if (isFirstRunAfterCreate) {
            PurchaseReminderHandler.showReminderIfNecessary(this)
        }
    }

    override fun updateUI() {
        state?.updateUI()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        state?.let { outState.putSerializable(KEY_SHOWN_FAVOURITE, it.shownFavourite) }
    }

    override fun onRestoreInstanceState(savedState: Bundle?) {
        super.onRestoreInstanceState(savedState)
        state?.apply {
            shownFavourite = savedState?.get(KEY_SHOWN_FAVOURITE) as DrinkEvent?
        }
    }

    override fun onSearchRequested(): Boolean {
        if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
            LogUtil.i(TAG, "Showing development menu")
            state?.let { openContextMenu(it.developmentView) }
            return true
        } else {
            return super.onSearchRequested()
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
        val viewId = v.id
        when (viewId) {

            R.id.favourites -> {
                // Show the favourites menu
                LogUtil.d(TAG, "Showing favourites menu")
                val inflater = menuInflater
                inflater.inflate(R.menu.favourite_actions, menu)
            }

            R.id.development_view ->
                // Show the development menu
                if (PrivateData.DEVELOPER_FUNCTIONALITY_ENABLED) {
                    LogUtil.d(TAG, "Showing development menu")
                    val inflater = menuInflater
                    inflater.inflate(R.menu.menu_developer, menu)
                }

            else -> super.onCreateContextMenu(menu, v, menuInfo)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        when (item.itemId) {

            R.id.action_modify -> {
                // Modify the selected favorite
                state?.favouritesAdapter?.getItem(info.position)?.let { fav ->
                    LogUtil.d(TAG, "Modifying %s", fav)
                    DrinkActivities.startModifyDrinkEvent(this, fav, true, true, false, ACTIVITY_CODE_MODIFY_FAVOURITE)
                }
                return true
            }

            R.id.action_show_info -> {
                // Show the drink information
                state?.favouritesAdapter?.getItem(info.position)?.let { fav ->
                    LogUtil.d(TAG, "Showing %s", fav)
                    state?.shownFavourite = fav
                    showDialog(DIALOG_DRINK_DETAILS)
                }
                return true
            }

            R.id.action_delete -> {
                // Delete the selected favorite
                state?.apply {
                    favouritesAdapter?.getItem(info.position)?.let { fav ->
                        LogUtil.d(TAG, "Deleting %s", fav)
                        favourites.deleteFavourite(fav.index)
                        updateFavourites()
                    }
                }
                return true
            }

            R.id.action_drink -> {
                // Drink the selected favorite
                state?.favouritesAdapter?.getItem(info.position)?.let { fav ->
                    LogUtil.d(TAG, "Drinking %s", fav)
                    DrinkActivities.startSelectDrinkDetails(this, fav, ACTIVITY_CODE_SELECT_DRINK)
                }
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onCreateDialog(id: Int): Dialog {
        return if (id == DIALOG_DRINK_DETAILS) DrinkDetailsDialog(this)
        else super.onCreateDialog(id)
    }

    override fun onPrepareDialog(id: Int, dialog: Dialog) {
        when (id) {
            DIALOG_DRINK_DETAILS -> {
                val d = dialog as DrinkDetailsDialog
                state?.let { d.showDrinkSelection(it.shownFavourite, false) }
            }
        }
        super.onPrepareDialog(id, dialog)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            when (requestCode) {
                ACTIVITY_CODE_SELECT_DRINK -> {
                    run {
                        val sel = DrinkActivities.getDrinkSelectionFromResult(data)
                        state?.consumeDrink(sel)
                    }
                    return
                }
                ACTIVITY_CODE_ADD_FAVOURITE -> {
                    run {
                        val sel = extras!!.get(KEY_RESULT) as DrinkSelection
                        state?.apply {
                            favourites?.createFavourite(sel)
                            updateFavourites()
                            LogUtil.d(TAG, "Added %s to favourites", sel)
                        }
                    }
                    return
                }
                ACTIVITY_CODE_MODIFY_FAVOURITE -> {
                    run {
                        val modifications = extras!!.get(KEY_RESULT) as DrinkSelection
                        val originalID = extras.getLong(KEY_ORIGINAL)

                        state?.apply {
                            val event = favourites.getFavourite(originalID)
                            event.drink = modifications.drink
                            event.size = modifications.size
                            favourites.updateFavourite(originalID, event)

                            updateFavourites()
                        }
                    }
                    return
                }
                Common.ACTIVITY_CODE_SHOW_PREFERENCES -> {
                    run {
                        LogUtil.i(TAG, "Updating locale to %s", prefs.locale)
                        LocalizationUtil.setLocale(prefs.locale, baseContext)
                    }
                    return
                }
            }
        }
    }

    /*
     * Options menu handling --------------------------------------------------
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (super.onOptionsItemSelected(item))
            return true

        when (item.itemId) {

            R.id.action_add_favourite -> {
                // Add a favourite
                DrinkActivities.startSelectDrink(this, ACTIVITY_CODE_ADD_FAVOURITE)
                return true
            }

            R.id.action_legal -> {
                // Show legal disclaimer
                CommonActivities.showDisclaimer(this)
                return true
            }

            R.id.action_about -> {
                // Show an about screen
                CommonActivities.showAbout(this)
                return true
            }

            R.id.action_show_preferences -> {
                showPreferences(null)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private class State(val activity: MainActivity) {
        val timeUtil = activity.timeUtil
        val adapter = activity.adapter
        val history: History = HistoryDB(adapter, activity)
        val meter: AlcoholLevelMeter = AlcoholLevelMeter(history, activity)
        val favourites: Favourites = FavouritesDB(activity.adapter, activity)

        val addFavouritesPrompt = activity.findViewById(R.id.add_favourites_prompt) as TextView
        val currentStatus = activity.fragmentManager.findFragmentById(R.id.current_status) as CurrentStatusFragment

        val gaugeAnimation = AlcoholLevelAnimation()

        val favouritesList: GridView = activity.findViewById(R.id.favourites) as GridView
        val developmentView: View = activity.findViewById(R.id.development_view)
        var favouritesAdapter: NamedIconAdapter<DrinkEvent>? = null

        var shownFavourite: DrinkEvent? = null

        init {
            currentStatus.setAlcoholLevel(0.6, DrivingMaybe)
            activity.registerForContextMenu(developmentView)
            favouritesList.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                favouritesAdapter?.getItem(position)?.let { favorite ->
                    val sel = DrinkSelection(favorite.drink, favorite.size, timeUtil
                            .currentTime)
                    consumeDrink(sel)
                }
            }
            activity.registerForContextMenu(favouritesList)
        }

        fun updateFavourites() {
            val favs = favourites.favourites
            LogUtil.d(TAG, "Showing %d favourites", favs.size)
            favouritesAdapter = NamedIconAdapter(activity, favs, true, Common.DEFAULT_ICON_RES)
            favouritesList.adapter = favouritesAdapter
            JalkametriWidget.triggerRecalculate(activity, adapter)
            LogUtil.d(TAG, "Recalculated widget")

            // If favourites list is empty, show the prompt; otherwise, hide it.
            addFavouritesPrompt.visibility = if (favs.isEmpty()) View.VISIBLE else View.GONE
        }

        fun updateUI() {
            val status = meter.drinkStatus
            val drivingState = status.getDrivingState(activity.prefs)
            currentStatus.setAlcoholLevel(status.alcoholLevel, drivingState)
            updateDrinkDateText()
            currentStatus.updateSobriety(status)
            updatePortionsText(status)
        }

        private fun updatePortionsText(status: DrinkStatus) {
            val shownDay = timeUtil.getCurrentDrinkingDate(activity.prefs)
            val todayPortions = HistoryHelper.countDayPortions(history, shownDay, activity)
            val weekPortions = HistoryHelper.countWeekPortions(history, shownDay, activity)
            val totalPortions = history.countTotalPortions()
            currentStatus.showPortions(String.format(PORTIONS_FORMAT, todayPortions, weekPortions, totalPortions))
        }

        private fun updateDrinkDateText() {
            val today = timeUtil.getCurrentDrinkingDate(activity.prefs)
            currentStatus.showDrinkDate(StringUtil.uppercaseFirstLetter(timeUtil.dateFormatWDay.format(today)))
        }

        fun showDrivingStatus(v: View) {
            val res = activity.resources
            val status = meter.drinkStatus
            when (status.getDrivingState(activity.prefs)) {
                DrivingOK -> DialogUtil.showMessage(activity, R.string.drive_status_ok, R.string.title_drive_status)
                DrivingMaybe -> {
                    val messagePat = res.getString(R.string.drive_status_maybe)
                    val toSober = status.hoursToSober
                    val soberTime = getTimeAfterHours(toSober)
                    val toSoberTime = getHoursMsg(toSober)
                    val message = String.format(messagePat, toSoberTime, soberTime)
                    DialogUtil.showMessage(activity, message, R.string.title_drive_status)
                }
                DrivingNo -> {
                    val messagePat = res.getString(R.string.drive_status_no)
                    val toDrive = status.getHoursToAlcoholLevel(activity.prefs.drivingAlcoholLimit)
                    val toSober = status.hoursToSober
                    val toDriveTime = getHoursMsg(toDrive)
                    val toSoberTime = getHoursMsg(toSober)
                    val driveTime = getTimeAfterHours(toDrive)
                    val soberTime = getTimeAfterHours(toSober)
                    val message = String.format(messagePat, toDriveTime, driveTime, toSoberTime, soberTime)
                    DialogUtil.showMessage(activity, message, R.string.title_drive_status)
                }
                null -> {
                }
            }
        }

        fun getTimeAfterHours(afterHours: Double): String {
            return timeUtil.timeFormat.format(timeUtil.getTimeAfterHours(afterHours))
        }

        fun getHoursMsg(hours: Double): String {
            val res = activity.resources
            if (hours >= 1) {
                val fullHours = hours.toInt()
                return String.format(res.getString(R.string.hourmin_pat), fullHours, ((hours - fullHours) * 60).toInt())
            } else {
                return String.format(res.getString(R.string.min_pat), (hours * 60).toInt())
            }
        }

        fun consumeDrink(selection: DrinkSelection?) {
            // Get original alcohol level
            val orgLevel = currentStatus.level
            val orgState = currentStatus.drivingState
            // Add drink
            history.createDrink(selection)
            JalkametriWidget.triggerRecalculate(activity, adapter)
            // Make toast
            DrinkActivities.makeDrinkToast(activity, orgLevel, true)
            updateUI()
            val newLevel = currentStatus.level
            // Fall back to old values
            currentStatus.setAlcoholLevel(orgLevel, orgState)
            // Start animation
            gaugeAnimation.showAnimation(orgLevel, newLevel, 0.8)
        }

        private inner class AlcoholLevelAnimation : Animation() {
            private var startAlcoholLevel = 0.0
            private var endAlcoholLevel = 0.0

            fun showAnimation(oldLevel: Double, newLevel: Double, duration: Double) {
                startAlcoholLevel = oldLevel
                endAlcoholLevel = newLevel
                LogUtil.i(TAG, "Starting animation")
                setDuration((duration * 1000L).toLong())
                startTime = AnimationUtils.currentAnimationTimeMillis()
                repeatCount = 0
                currentStatus.startAnimation(this)
            }

            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                super.applyTransformation(interpolatedTime, t)
                val showLevel = (endAlcoholLevel - startAlcoholLevel) * interpolatedTime + startAlcoholLevel
                val showState = DrinkStatusCalc.getDrivingState(activity.prefs, showLevel)
                LogUtil.i(TAG, "Animating at time %.2f: level %.2f", interpolatedTime, showLevel)
                currentStatus.setAlcoholLevel(showLevel, showState)
            }

        }

    }

    companion object {

        private val TAG = "MainActivity"

        private val KEY_SHOWN_FAVOURITE = "shown_favourite"
        private val PORTIONS_FORMAT = "%.1f / %.1f / %.1f"
        private val DIALOG_DRINK_DETAILS = 1
    }

}
