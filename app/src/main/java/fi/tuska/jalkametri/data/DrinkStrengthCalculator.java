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
