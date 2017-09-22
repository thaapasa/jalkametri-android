package fi.tuska.jalkametri.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;

public class DrinkSupport {

    public static final Drink getBeer() {
        List<DrinkSize> sizes = new ArrayList<DrinkSize>();
        sizes.add(getPint());
        Drink beer = new Drink("Beer", 4.6d, "drink_beer_pint", "Beer is good", sizes);
        return beer;
    }

    public static final DrinkSize getPint() {
        DrinkSize pint = new DrinkSize("Pint", 0.568d, "size_pint_pint");
        return pint;
    }

    public static final DrinkSelection getBeerSelection(Date time) {
        DrinkSelection sel = new DrinkSelection(getBeer(), getPint(), time);
        return sel;
    }

}
