package fi.tuska.jalkametri.data.drinks;

import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkCategory;
import fi.tuska.jalkametri.dao.DrinkLibrary;
import fi.tuska.jalkametri.dao.DrinkSizes;
import fi.tuska.jalkametri.data.DrinkSize;

public class FinnishDrinkLibrary extends AbstractDrinkLibrary {

    @Override
    public void createDefaultDrinks(DrinkLibrary library) {
        // Create drink sizes
        DrinkSizes sizes = library.getDrinkSizes();

        int order = 0;
        @SuppressWarnings("unused")
        DrinkSize sizePint = createSize(sizes, "Pintti", 0.568f, R.drawable.ic_size_pint_pint,
            ++order);
        DrinkSize sizeLargeBeer = createSize(sizes, "Tuoppi", 0.5f, R.drawable.ic_size_pint_large,
            ++order);
        DrinkSize sizeEuro = createSize(sizes, "Eurotuoppi", 0.4f, R.drawable.ic_size_pint_euro,
            ++order);
        DrinkSize sizeHalfPint = createSize(sizes, "Puolikas tuoppi", 0.25f,
            R.drawable.ic_size_pint_half, ++order);
        DrinkSize sizeBottle = createSize(sizes, "Pullo", 0.33f, R.drawable.ic_size_beer_bottle,
            ++order);
        DrinkSize sizeShot = createSize(sizes, "Shotti", 0.04f, R.drawable.ic_size_shot, ++order);
        DrinkSize sizeShotHalf = createSize(sizes, "Puolikas shotti", 0.02f,
            R.drawable.ic_size_shot, ++order);
        DrinkSize sizeShotDouble = createSize(sizes, "Tuplashotti", 0.08f, R.drawable.ic_size_shot,
            ++order);
        DrinkSize sizeDrop = createSize(sizes, "Tilkka", 0.01f, R.drawable.ic_size_shot, ++order);
        DrinkSize sizeGlass = createSize(sizes, "Lasi", 0.25f, R.drawable.ic_size_glass, ++order);
        DrinkSize sizeGlassSmall = createSize(sizes, "Pieni lasi", 0.15f,
            R.drawable.ic_size_glass_small, ++order);
        DrinkSize sizeWineDessert = createSize(sizes, "Jälkiruokaviinilasi", 0.08f,
            R.drawable.ic_size_wine_small, ++order);
        DrinkSize sizeWineSmall = createSize(sizes, "Pieni viinilasi", 0.12f,
            R.drawable.ic_size_wine_small, ++order);
        DrinkSize sizeWineMedium = createSize(sizes, "Viinilasi", 0.16f,
            R.drawable.ic_size_wine_medium, ++order);
        DrinkSize sizeWineLarge = createSize(sizes, "Iso viinilasi", 0.24f,
            R.drawable.ic_size_wine_large, ++order);
        DrinkSize sizeWineBottle = createSize(sizes, "Viinipullo", 0.75f,
            R.drawable.ic_size_wine_bottle, ++order);
        DrinkSize sizeWineChampagne = createSize(sizes, "Samppanjalasi", 0.12f,
            R.drawable.ic_size_champagne, ++order);

        // Kaljat
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Miedot", "cat_beers");
            cat.createDrink("Keppana", 4.6f, "drink_beer_pint", new DrinkSize[] { sizeLargeBeer,
                sizeEuro, sizeBottle, sizeHalfPint }, ++order);
            cat.createDrink("Ykkönen", 2.5f, "drink_beer_bottle", new DrinkSize[] { sizeBottle },
                ++order);
            cat.createDrink("Nelonen", 5.2f, "drink_beer_pint", new DrinkSize[] { sizeLargeBeer,
                sizeEuro, sizeBottle }, ++order);
            cat.createDrink("Pukki", 6.0f, "drink_beer_can", new DrinkSize[] { sizeLargeBeer,
                sizeBottle }, ++order);
            cat.createDrink("Siideri", 4.7f, "drink_cider_bottle", new DrinkSize[] {
                sizeLargeBeer, sizeEuro, sizeBottle }, ++order);
            cat.createDrink("Alkon lonkero", 5.5f, "drink_cider_bottle", new DrinkSize[] {
                sizeLargeBeer, sizeEuro, sizeBottle }, ++order);
            cat.createDrink("Ykköslonkero", 2.6f, "drink_cider_bottle", new DrinkSize[] {
                sizeLargeBeer, sizeBottle }, ++order);
        }
        // Viinit
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Viinit", "cat_wines");
            cat.createDrink("Punaviini", 12.0f, "drink_wine_red", new DrinkSize[] {
                sizeWineSmall, sizeWineMedium, sizeWineLarge, sizeWineBottle }, ++order);
            cat.createDrink("Valkoviini", 12.0f, "drink_wine_white", new DrinkSize[] {
                sizeWineSmall, sizeWineMedium, sizeWineLarge, sizeWineBottle }, ++order);
            cat.createDrink("Vahvempi viini", 14.0f, "drink_wine_red", new DrinkSize[] {
                sizeWineSmall, sizeWineMedium, sizeWineLarge, sizeWineBottle }, ++order);
            cat.createDrink("Jälkiruokaviini", 18.0f, "drink_wine_dessert", new DrinkSize[] {
                sizeWineDessert, sizeWineSmall }, ++order);
            cat.createDrink("Skumppa", 12.0f, "drink_champagne",
                new DrinkSize[] { sizeWineChampagne }, ++order);
        }

        // Pitkät
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Pitkät", "cat_longdrinks");
            cat.createDrink("Gin tonic", 6.08f, "drink_long_gt", new DrinkSize[] { sizeGlass },
                ++order);
            cat.createDrink("Caipirosca", 6.08f, "drink_long_drink",
                new DrinkSize[] { sizeGlass }, ++order);
            cat.createDrink("Mojito", 6.08f, "drink_long_drink", new DrinkSize[] { sizeGlass },
                ++order);
        }

        // Viinat
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Viinat", "cat_spirits");
            cat.createDrink("Kossu", 38f, "drink_shot", new DrinkSize[] { sizeShot, sizeShotHalf,
                sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Viski", 43f, "drink_whisky", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Konjakki", 42f, "drink_whisky", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Punssi", 21f, "drink_wine_dessert", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Baileys", 17f, "drink_shot", new DrinkSize[] { sizeShot,
                sizeShotHalf, sizeShotDouble, sizeDrop }, ++order);
            cat.createDrink("Hot shot", 30f, "drink_shot", new DrinkSize[] { sizeShotHalf },
                ++order);
            cat.createDrink("Irish coffee", 6.08f, "drink_irish_coffee",
                new DrinkSize[] { sizeGlass }, ++order);
        }

        // Boolit
        {
            order = 0;
            DrinkCategory cat = library.createCategory("Boolit", "cat_punches");
            cat.createDrink("Perusbooli", 5f, "drink_punch", new DrinkSize[] { sizeGlass,
                sizeGlassSmall }, ++order);
            cat.createDrink("Tuju booli", 7.5f, "drink_punch", new DrinkSize[] { sizeGlass,
                sizeGlassSmall }, ++order);
            cat.createDrink("Jytky booli", 10f, "drink_punch", new DrinkSize[] { sizeGlass,
                sizeGlassSmall }, ++order);
        }
    }

}
