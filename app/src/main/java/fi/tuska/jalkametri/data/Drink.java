package fi.tuska.jalkametri.data;

import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.dao.NamedIcon;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.util.NumberUtil;

/**
 * This class models a drink type, such as a specific beer or whisky. Defines
 * the name and strength (percentage) of the drink, but the volume is not
 * fixed.
 *
 * @author tuska
 */
public class Drink extends DBDataObject implements NamedIcon {

    private static final long serialVersionUID = 6424979889656941340L;

    private String name;
    private String icon;
    private double strength;
    private String comment;
    private List<DrinkSize> sizes;
    private final long categoryID;

    public Drink() {
        super();
        name = "";
        icon = Common.DEFAULT_ICON_NAME;
        strength = 5;
        comment = "";
        sizes = new ArrayList<DrinkSize>();
        this.categoryID = DBDataObject.getInvalidID();
    }

    /**
     * Creates a db-backed instance of a drink.
     */
    public Drink(long index, long categoryId, String name, double strength, String icon,
        String comment, List<DrinkSize> sizes) {
        super(index);
        this.categoryID = categoryId;
        this.name = name;
        this.icon = icon;
        this.strength = strength;
        this.comment = comment;
        this.sizes = sizes;
    }

    public Drink(String name, double strength, String icon, String comment, List<DrinkSize> sizes) {
        super();
        this.categoryID = DBDataObject.getInvalidID();
        this.name = name;
        this.icon = icon;
        this.strength = strength;
        this.comment = comment;
        this.sizes = sizes;
    }

    public Drink(Drink drink) {
        super();
        this.categoryID = DBDataObject.getInvalidID();
        this.name = drink.name;
        this.icon = drink.icon;
        this.strength = drink.strength;
        this.comment = drink.comment;
        this.sizes = new ArrayList<DrinkSize>(drink.sizes);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<DrinkSize> getDrinkSizes() {
        return sizes;
    }

    @Override
    public String getIconText(Resources res) {
        return String.format("%s\n%s %%", name, NumberUtil.toString(strength, res));
    }

    @Override
    public String toString() {
        return name + " (" + strength + ")";
    }

    public long getDrinkCategoryIndex() {
        return categoryID;
    }
}
