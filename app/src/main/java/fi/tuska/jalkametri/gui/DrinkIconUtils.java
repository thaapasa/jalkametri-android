package fi.tuska.jalkametri.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.IconName;

public class DrinkIconUtils {

    private static final Map<String, Integer> DRINK_ICONS = new HashMap<String, Integer>();
    private static final Map<Integer, String> REVERSE_DRINK_ICONS = new HashMap<Integer, String>();
    private static final List<IconName> ICON_LIST = new ArrayList<IconName>();

    static {
        // General
        addIcon("drink_generic", R.drawable.ic_drink_generic);
        addIcon("drink_fancy", R.drawable.ic_drink_cocktail_red);
        addIcon("drink_water", R.drawable.ic_drink_water);

        // Beers
        addIcon("cat_beers", R.drawable.ic_cat_beers);
        addIcon("drink_beer_bottle", R.drawable.ic_drink_beer_bottle_brown);
        addIcon("drink_beer_can", R.drawable.ic_drink_beer_can);
        addIcon("drink_beer_pint", R.drawable.ic_drink_beer_pint);
        addIcon("drink_beer_glass", R.drawable.ic_drink_beer_glass);
        addIcon("drink_gin_bottle", R.drawable.ic_drink_beer_bottle_blue);
        addIcon("drink_cider_bottle", R.drawable.ic_drink_beer_bottle_green);

        // Wines
        addIcon("cat_wines", R.drawable.ic_cat_wines);
        addIcon("drink_wine_dessert", R.drawable.ic_drink_wine_dessert);
        addIcon("drink_wine_red", R.drawable.ic_drink_wine_glass_bottle_red);
        addIcon("drink_wine_white", R.drawable.ic_drink_wine_glass_bottle_white);
        addIcon("drink_champagne", R.drawable.ic_drink_champagne);
        addIcon("drink_champagne_glasses", R.drawable.ic_drink_champagne_glasses);

        // Long drinks
        addIcon("cat_longdrinks", R.drawable.ic_cat_longdrinks);
        addIcon("cat_cocktails", R.drawable.ic_cat_cocktails);
        addIcon("drink_caipirosca", R.drawable.ic_drink_caipirosca);
        addIcon("drink_long_drink", R.drawable.ic_drink_cocktail_tall);
        addIcon("drink_cocktail_red", R.drawable.ic_drink_cocktail_red);
        addIcon("drink_cocktail_blue", R.drawable.ic_drink_cocktail_blue);
        addIcon("drink_long_gt", R.drawable.ic_drink_lemonade);
        addIcon("drink_irish_coffee", R.drawable.ic_drink_irish_coffee);
        addIcon("drink_martini", R.drawable.ic_drink_martini);

        // Spirits
        addIcon("cat_spirits", R.drawable.ic_cat_spirits);
        addIcon("drink_shot", R.drawable.ic_drink_tequila);
        addIcon("drink_whisky", R.drawable.ic_drink_whisky);
        addIcon("drink_whisky_bottle", R.drawable.ic_drink_whisky_bottle);
        addIcon("drink_vodka", R.drawable.ic_drink_vodka);
        addIcon("drink_gin", R.drawable.ic_drink_gin);

        // Punches
        addIcon("cat_punches", R.drawable.ic_cat_punches);
        addIcon("drink_punch", R.drawable.ic_drink_punch_bowl);

        // Sizes
        addIcon("size_beer_bottle", R.drawable.ic_size_beer_bottle);
        addIcon("size_beer_can", R.drawable.size_beer_can);
        addIcon("size_pint_euro", R.drawable.size_pint_euro);
        addIcon("size_pint_half", R.drawable.size_pint_half);
        addIcon("size_pint_large", R.drawable.size_pint_large);
        addIcon("size_pint_pint", R.drawable.size_pint_pint);
        addIcon("size_wine_large", R.drawable.size_wine_large);
        addIcon("size_wine_medium", R.drawable.size_wine_medium);
        addIcon("size_wine_small", R.drawable.size_wine_small);
        addIcon("size_wine_bottle", R.drawable.size_wine_bottle);
        addIcon("size_champagne", R.drawable.size_champagne);
        addIcon("size_cocktail", R.drawable.size_cocktail);
        addIcon("size_glass", R.drawable.size_glass);
        addIcon("size_glass_small", R.drawable.size_glass_small);
        addIcon("size_shot", R.drawable.size_shot);
    }

    private static void addIcon(String iconName, int resID) {
        DRINK_ICONS.put(iconName, resID);
        REVERSE_DRINK_ICONS.put(resID, iconName);
        ICON_LIST.add(new IconName(iconName));
    }

    /**
     * @return the resource that is represented by the image name; or 0, if
     * drink not found
     */
    public static int getDrinkIconRes(String icon) {
        Integer res = DRINK_ICONS.get(icon);
        return res != null ? res : 0;
    }

    /**
     * @return the image name represented by the resource; or null, if the
     * resource is not found
     */
    public static String getDrinkIconName(int resource) {
        String res = REVERSE_DRINK_ICONS.get(resource);
        return res;
    }

    public static List<IconName> getAsList() {
        return ICON_LIST;
    }
}
