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

import java.io.Serializable;

import android.content.Context;

import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.Preferences;

public final class DrinkStrengthCalculator implements Serializable {

    private static final long serialVersionUID = 3002883005231574357L;

    /** Volume, in liters */
    private double volume;
    /** Amount of alcohol, in grams */
    private double alcoholWeight;

    public DrinkStrengthCalculator() {
        clear();
    }

    public double getVolume() {
        return volume;
    }

    public double getStrength() {
        if (volume == 0)
            return 0;
        return (alcoholWeight * 100.0d) / (Common.ALCOHOL_LITER_WEIGHT * volume);
    }

    public double getPortions(Context context) {
        Preferences prefs = new PreferencesImpl(context);
        return alcoholWeight / prefs.getStandardDrinkAlcoholWeight();
    }

    public double getAlcoholWeight() {
        return alcoholWeight;
    }

    public void addComponent(double componentVolume, double componentStrength) {
        volume += componentVolume;
        alcoholWeight += componentStrength * componentVolume * Common.ALCOHOL_LITER_WEIGHT / 100.0d;
    }

    public final void clear() {
        volume = 0;
        alcoholWeight = 0;
    }

}
