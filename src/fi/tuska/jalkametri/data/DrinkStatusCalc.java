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
package fi.tuska.jalkametri.data;

import java.util.Date;
import java.util.List;

import android.content.Context;
import fi.tuska.jalkametri.dao.DrinkStatus;
import fi.tuska.jalkametri.dao.History;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.dao.Preferences.Gender;
import fi.tuska.jalkametri.util.TimeUtil;

public final class DrinkStatusCalc implements DrinkStatus {

    private static final double FEMALE_MODIFIER = 0.66d;
    private static final double MALE_MODIFIER = 0.75d;

    private double alcoholAtUpdate;
    private int lastUpdateIndex;
    private double maxAlcoholAmount;

    /** The total amount of alcohol, in grams. */
    private double totalAlcoholAmount;

    private final List<DrinkEvent> drinks;

    private final double weight;
    private final boolean isMale;

    private final TimeUtil timeUtil;

    private final Context context;

    public DrinkStatusCalc(History history, Context context) {
        this.context = context;
        this.timeUtil = new TimeUtil(context);
        this.alcoholAtUpdate = 0;
        this.lastUpdateIndex = -1;
        this.maxAlcoholAmount = 0;
        this.totalAlcoholAmount = 0;

        Preferences prefs = new PreferencesImpl(context);
        weight = prefs.getWeight();
        isMale = prefs.getGender().equals(Gender.Male);

        this.drinks = history.getDrinks(timeUtil.getCurrentDrinkingDate(prefs), true);
        updateAlcoholLevel();
    }

    public DrinkStatusCalc(History history, Context context, Date start, Date end) {
        this.context = context;
        this.timeUtil = new TimeUtil(context);
        this.alcoholAtUpdate = 0;
        this.lastUpdateIndex = -1;
        this.maxAlcoholAmount = 0;
        this.totalAlcoholAmount = 0;

        Preferences prefs = new PreferencesImpl(context);
        weight = prefs.getWeight();
        isMale = prefs.getGender().equals(Gender.Male);

        this.drinks = history.getDrinks(start, end, true);
        updateAlcoholLevel();
    }

    /**
     * Returns the amount of alcohol burned (grams / hour).
     * 
     * @return the amount of alcohol burned (grams / hour).
     */
    public double getAlcoholBurningRate() {
        return weight / 10f;
    }

    /**
     * Returns the amount of alcohol currently in the user (in grams).
     * 
     * @return the amount of alcohol currently in the user (in grams)
     */
    @Override
    public double getAlcoholAmount() {
        return alcoholAtUpdate - getAlcoholBurnedSinceLastDrink();
    }

    /**
     * Returns the time (in hours) to an accepted alcohol level.
     * 
     * @param acceptedLevel the accepted level of alcohol in user (in promilles)
     * @return the time (in hours) remaining until the user is at accepted alcohol level
     */
    @Override
    public double getHoursToAlcoholLevel(final double acceptedLevel) {
        final double acceptedAmount = getAlcoholAmount(acceptedLevel);
        final double current = getAlcoholAmount();
        if (current < acceptedAmount) {
            return 0;
        }
        return (current - acceptedAmount) / getAlcoholBurningRate();
    }

    /**
     * Returns the time (in hours) to sober state (all alcohol burned).
     * 
     * @return the time (in hours) remaining until the user is sober
     */
    @Override
    public double getHoursToSober() {
        return getHoursToAlcoholLevel(0);
    }

    public double getLevelModifier() {
        return isMale ? MALE_MODIFIER : FEMALE_MODIFIER;
    }

    public double getAlcoholAmount(final double level) {
        return level * weight * getLevelModifier();
    }

    public double getAlcoholLevel(final double amount) {
        return (amount / weight) / getLevelModifier();
    }

    public int getDrinkCount() {
        return drinks.size();
    }

    /**
     * Returns the current alcohol level (in promilles). The level is calculated based on the drinks that have been
     * consumed.
     * 
     * @return the current alcohol level (in promilles).
     */
    @Override
    public double getAlcoholLevel() {
        return getAlcoholLevel(getAlcoholAmount());
    }

    @Override
    public double getMaxAlcoholAmount() {
        return maxAlcoholAmount;
    }

    @Override
    public double getMaxAlcoholLevel() {
        return getAlcoholLevel(maxAlcoholAmount);
    }

    public double getBurnedAlcoholAmount(Date date1, Date date2) {
        return getAlcoholBurningRate() * timeUtil.getHourDifference(date1, date2);
    }

    /**
     * Returns the amount of alcohol burned since last drink (in grams). This is at most the amount of alcohol in blood
     * after the last drink, so after a certain point, this method will always return the same value.
     * 
     * @return the amount of alcohol burned since last drink (in grams)
     */
    public double getAlcoholBurnedSinceLastDrink() {
        if (getLastDrinkEvent() == null) {
            return 0;
        }
        return Math.min(getBurnedAlcoholAmount(getLastDrinkEvent().getTime(), timeUtil.getCurrentTime()),
            alcoholAtUpdate);
    }

    public DrinkEvent getLastDrinkEvent() {
        if (drinks.size() < 1) {
            return null;
        }
        return drinks.get(drinks.size() - 1);
    }

    public DrinkEvent getFirstDrinkEvent() {
        if (drinks.size() < 1) {
            return null;
        }
        return drinks.get(0);
    }

    public DrinkEvent getDrinkEventAtLastUpdate() {
        if (lastUpdateIndex < 0) {
            return null;
        }
        return drinks.get(lastUpdateIndex);
    }

    public double getTotalAlcoholAmount() {
        return totalAlcoholAmount;
    }

    @Override
    public double getTotalAlcoholPortions() {
        return totalAlcoholAmount / getStandardDrinkAlcoholWeight();
    }

    public void recalculateAlcoholLevel() {
        alcoholAtUpdate = 0;
        lastUpdateIndex = -1;
        maxAlcoholAmount = 0;
        totalAlcoholAmount = 0;
        updateAlcoholLevel();
    }

    /**
     * Calculates the updated alcohol level.
     */
    public void updateAlcoholLevel() {
        if (drinks.size() < 1) {
            lastUpdateIndex = -1;
            alcoholAtUpdate = 0;
            totalAlcoholAmount = 0;
            return;
        }
        if (lastUpdateIndex < 0) {
            alcoholAtUpdate = getFirstDrinkEvent().getAlcoholAmount();
            maxAlcoholAmount = alcoholAtUpdate;
            totalAlcoholAmount = alcoholAtUpdate;
            lastUpdateIndex = 0;
        }
        DrinkEvent lastEvent = getDrinkEventAtLastUpdate();
        for (int i = lastUpdateIndex + 1; i < drinks.size(); i++) {
            final DrinkEvent event = drinks.get(i);
            if (lastEvent != null) {
                alcoholAtUpdate -= getBurnedAlcoholAmount(lastEvent.getTime(), event.getTime());
                if (alcoholAtUpdate < 0) {
                    alcoholAtUpdate = 0;
                }
            }
            alcoholAtUpdate += event.getAlcoholAmount();
            totalAlcoholAmount += event.getAlcoholAmount();
            if (alcoholAtUpdate > maxAlcoholAmount) {
                maxAlcoholAmount = alcoholAtUpdate;
            }
            lastEvent = event;
            lastUpdateIndex = i;
        }
    }

    @Override
    public DrivingState getDrivingState(Preferences prefs) {
        double level = getAlcoholLevel();
        double max = prefs.getDrivingAlcoholLimit();
        if (level <= 0)
            return DrivingState.DrivingOK;
        else if (level < max)
            return DrivingState.DrivingMaybe;
        else
            return DrivingState.DrivingNo;
    }

    public static DrivingState getDrivingState(Preferences prefs, double level) {
        double max = prefs.getDrivingAlcoholLimit();
        if (level <= 0)
            return DrivingState.DrivingOK;
        else if (level < max)
            return DrivingState.DrivingMaybe;
        else
            return DrivingState.DrivingNo;
    }

    @Override
    public double getStandardDrinkAlcoholWeight() {
        Preferences prefs = new PreferencesImpl(context);
        return prefs.getStandardDrinkAlcoholWeight();
    }

}
