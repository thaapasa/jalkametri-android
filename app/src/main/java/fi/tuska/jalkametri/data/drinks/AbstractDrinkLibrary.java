package fi.tuska.jalkametri.data.drinks;

import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.gui.DrinkIconUtils;
import fi.tuska.jalkametri.util.AssertionUtils;

public abstract class AbstractDrinkLibrary implements DefaultDrinkLibrary {

    protected DrinkSize createSize(DrinkSizes sizes, String name, double volume, int resourceId,
        int order) {
        String iconName = DrinkIconUtils.getDrinkIconName(resourceId);
        AssertionUtils.INSTANCE.expect(iconName != null);
        DrinkSize size = sizes.createSize(name, volume, iconName, order);
        return size;
    }

}
