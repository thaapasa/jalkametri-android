package fi.tuska.jalkametri.data.drinks;

import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.DrinkSize;

public class EnglishDrinkLibrary extends AbstractDrinkLibrary {

    @Override
    public void createDefaultDrinks(DrinkLibrary library) {
        // Create drink sizes
        DrinkSizes sizes = library.getDrinkSizes();

        int order = 0;
        DrinkSize sizePint = createSize(sizes, "Pint", 0.568f, R.drawable.size_pint_pint, ++order);
        DrinkSize sizeLargeBeer = createSize(sizes, "Large mug", 0.5f,
            R.drawable.size_pint_large, ++order);
        DrinkSize sizeHalfPint = createSize(sizes, "Half a pint", 0.284f,
            R.drawable.size_pint_half, ++order);
        DrinkSize sizeBottle = createSize(sizes, "Bottle", 0.33f, R.drawable.size_beer_bottle,
            ++order);
        DrinkSize sizeShot = createSize(sizes, "Shot", 0.04f, R.drawable.size_shot, ++order);
        DrinkSize sizeShotHalf = createSize(sizes, "Half a shot", 0.02f, R.drawable.size_shot,
            ++order);
        DrinkSize sizeShotDouble = createSize(sizes, "Double shot", 0.08f, R.drawable.size_shot,
            ++order);
        DrinkSize sizeDrop = createSize(sizes, "Drop", 0.01f, R.drawable.size_shot, ++order);
        DrinkSize sizeGlass = createSize(sizes, "Glass", 0.25f, R.drawable.size_glass, ++order);
        DrinkSize sizeGlassSmall = createSize(sizes, "Small glass", 0.15f,
            R.drawable.size_glass_small, ++order);
        DrinkSize sizeWineDessert = createSize(sizes, "Dessert wine glass", 0.08f,
            R.drawable.size_wine_small, ++order);
        DrinkSize sizeWineSmall = createSize(sizes, "Small wine glass", 0.12f,
            R.drawable.size_wine_small, ++order);
        DrinkSize sizeWineMedium = createSize(sizes, "Wine glass", 0.16f,
            R.drawable.size_wine_medium, ++order);
        DrinkSize sizeWineLarge = createSize(sizes, "Large wine glass", 0.24f,
            R.drawable.size_wine_large, ++order);
        DrinkSize sizeWineBottle = createSize(sizes, "Wine bottle", 0.75f,
            R.drawable.size_wine_bottle, ++order);
        DrinkSize sizeWineChampagne = createSize(sizes, "Champagne glass", 0.12f,
            R.drawable.size_champagne, ++order);

        // Beers
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Beers", "cat_beers");
            cat.createDrink("Regular beer", 4.5f, "drink_beer_pint", new DrinkSize[] { sizePint,
                sizeLargeBeer, sizeBottle, sizeHalfPint }, ++order);
            cat.createDrink("Premium", 5.2f, "drink_beer_pint", new DrinkSize[] { sizePint,
                sizeLargeBeer, sizeBottle }, ++order);
            cat.createDrink("Cider", 4.5f, "drink_cider_bottle", new DrinkSize[] { sizePint,
                sizeLargeBeer, sizeBottle }, ++order);

        }
        // Wines
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Wines", "cat_wines");
            cat.createDrink("Red wine", 12.0f, "drink_wine_red", new DrinkSize[] { sizeWineSmall,
                sizeWineMedium, sizeWineLarge, sizeWineBottle }, ++order);
            cat.createDrink("White wine", 12.0f, "drink_wine_white", new DrinkSize[] {
                sizeWineSmall, sizeWineMedium, sizeWineLarge, sizeWineBottle }, ++order);
            cat.createDrink("Dessert wine", 18.0f, "drink_wine_dessert", new DrinkSize[] {
                sizeWineDessert, sizeWineSmall }, ++order);
            cat.createDrink("Sparkling wine", 12.0f, "drink_champagne",
                new DrinkSize[] { sizeWineChampagne }, ++order);
        }

        // Long drinks
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Long drinks", "cat_longdrinks");
            cat.createDrink("Gin tonic", 6.08f, "drink_long_gt", new DrinkSize[] { sizeGlass },
                ++order);
            cat.createDrink("Caipirosca", 6.08f, "drink_long_drink",
                new DrinkSize[] { sizeGlass }, ++order);
            cat.createDrink("Mojito", 6.08f, "drink_long_drink", new DrinkSize[] { sizeGlass },
                ++order);
        }

        // Spirits
        {
            DrinkCategory cat = library.createCategory("Spirits", "cat_spirits");
            cat.createDrink("Booze", 38f, "drink_shot", new DrinkSize[] { sizeShot, sizeShotHalf,
                sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Whisky", 43f, "drink_whisky", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Cognac", 42f, "drink_whisky", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Swedish punch", 21f, "drink_wine_dessert", new DrinkSize[] {
                sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Baileys", 17f, "drink_shot", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Hot shot", 30f, "drink_shot", new DrinkSize[] { sizeShotHalf },
                ++order);
            cat.createDrink("Irish coffee", 6.08f, "drink_irish_coffee",
                new DrinkSize[] { sizeGlass }, ++order);
        }

        // Punches
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Punches", "cat_punches");
            cat.createDrink("Punch", 5f, "drink_punch", new DrinkSize[] { sizeGlass,
                sizeGlassSmall }, ++order);
            cat.createDrink("Strong punch", 7.5f, "drink_punch", new DrinkSize[] { sizeGlass,
                sizeGlassSmall }, ++order);
            cat.createDrink("Powerful punch", 10f, "drink_punch", new DrinkSize[] { sizeGlass,
                sizeGlassSmall }, ++order);
        }
    }

}
