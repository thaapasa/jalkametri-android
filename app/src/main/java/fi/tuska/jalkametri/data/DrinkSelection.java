package fi.tuska.jalkametri.data;

import java.io.Serializable;
import java.util.Date;

import android.content.Context;
import android.content.res.Resources;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.NamedIcon;
import fi.tuska.jalkametri.dao.Preferences;

public class DrinkSelection implements Serializable, NamedIcon {

    private static final long serialVersionUID = -5868323399663745796L;

    private Drink drink;
    private DrinkSize size;
    private Date time;

    public DrinkSelection(Drink drink) {
        setDrink(drink);
    }

    public DrinkSelection(Drink drink, DrinkSize size) {
        setDrink(drink);
        setSize(size);
    }

    public DrinkSelection(Drink drink, DrinkSize size, Date time) {
        setDrink(drink);
        setSize(size);
        setTime(time);
    }

    public double getPortions(Context context) {
        Preferences prefs = new PreferencesImpl(context);
        return getAlcoholAmount() / prefs.getStandardDrinkAlcoholWeight();
    }

    /**
     * Returns the amount of pure alcohol in the drink (in liters).
     *
     * @return the amount of pure alcohol in the drink (in liters)
     */
    public double getAlcoholVolume() {
        return size.getVolume() * drink.getStrength() / 100;
    }

    /**
     * Returns the amount of alcohol in the drink (in grams).
     *
     * @return the amount of alcohol in the drink (in grams)
     */
    public double getAlcoholAmount() {
        return getAlcoholVolume() * Common.ALCOHOL_LITER_WEIGHT;
    }

    public DrinkSelection(DrinkSelection sel) {
        if (sel.drink != null)
            setDrink(sel.drink);
        if (sel.size != null)
            setSize(sel.size);
        if (sel.time != null)
            setTime(sel.time);
    }

    public Drink getDrink() {
        return drink;
    }

    public final void setDrink(Drink drink) {
        this.drink = new Drink(drink);
    }

    public DrinkSize getSize() {
        return size;
    }

    public final void setSize(DrinkSize size) {
        this.size = new DrinkSize(size);
    }

    public Date getTime() {
        return time;
    }

    public final void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return size + " of " + drink + "; time: " + time;
    }

    @Override
    public String getIcon() {
        return drink.getIcon();
    }

    /**
     * Resources is not used for this method; can be null. DrinkEvent.getName() calls this method with a null parameter.
     */
    @Override
    public String getIconText(Resources res) {
        return String.format("%s, %s", drink.getName(), size.getName());
    }

}
