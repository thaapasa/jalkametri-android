package fi.tuska.jalkametri.data;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

public class DrinkSupport {

    public static Drink getBeer() {
        List<DrinkSize> sizes = new ArrayList<DrinkSize>();
        sizes.add(getPint());
        Drink beer = new Drink("Beer", 4.6d, "drink_beer_pint", "Beer is good", sizes);
        return beer;
    }

    public static DrinkSize getPint() {
        DrinkSize pint = new DrinkSize("Pint", 0.568d, "size_pint_pint");
        return pint;
    }

    public static DrinkSelection getBeerSelection(Instant time) {
        DrinkSelection sel = new DrinkSelection(getBeer(), getPint(), time);
        return sel;
    }

}
