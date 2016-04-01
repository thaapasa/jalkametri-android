package fi.tuska.jalkametri.data.drinks;

import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.gui.DrinkIconUtils;

public abstract class AbstractDrinkLibrary implements DefaultDrinkLibrary {

    protected DrinkSize createSize(DrinkSizes sizes, String name, double volume, int resourceId,
        int order) {
        String iconName = DrinkIconUtils.getDrinkIconName(resourceId);
        assert iconName != null;
        DrinkSize size = sizes.createSize(name, volume, iconName, order);
        return size;
    }

}
