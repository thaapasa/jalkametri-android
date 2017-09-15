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
package fi.tuska.jalkametri.dao;

public interface DrinkStatus {

    enum DrivingState {
        DrivingOK, DrivingMaybe, DrivingNo
    };

    /**
     * @return the weight (in grams) of alcohol in a standard drink (i.e., one portion). In Finland, this is 12.0 = 12
     * grams.
     */
    double getStandardDrinkAlcoholWeight();

    /**
     * @return the alcohol level, in promilles. E.g, 1.0 means 1.0 promilles. In Finland, 0.5 promilles is the car
     * driving limit.
     */
    double getAlcoholLevel();

    double getMaxAlcoholAmount();

    double getMaxAlcoholLevel();

    double getTotalAlcoholPortions();

    /**
     * Returns the time (in hours) to sober state (all alcohol burned).
     * 
     * @return the time (in hours) remaining until the user is sober
     */
    double getHoursToSober();

    /**
     * Returns the time (in hours) to an accepted alcohol level.
     * 
     * @param acceptedLevel the accepted level of alcohol in user (in promilles)
     * @return the time (in hours) remaining until the user is at accepted alcohol level
     */
    double getHoursToAlcoholLevel(final double acceptedLevel);

    /** @return the amount of alcohol currently in the user (in grams) */
    double getAlcoholAmount();

    /** @return the driving state of the user */
    DrivingState getDrivingState(Preferences prefs);
}
