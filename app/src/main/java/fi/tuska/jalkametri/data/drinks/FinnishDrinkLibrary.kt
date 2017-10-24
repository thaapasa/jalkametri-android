package fi.tuska.jalkametri.data.drinks

import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.DrinkCategory
import fi.tuska.jalkametri.dao.DrinkLibrary
import fi.tuska.jalkametri.dao.DrinkSizes
import fi.tuska.jalkametri.data.DrinkSize

class FinnishDrinkLibrary : AbstractDrinkLibrary() {

    override fun createDefaultDrinks(library: DrinkLibrary) {
        // Create drink sizes
        val sizes = library.drinkSizes

        var order = 0
        val sizePint = createSize(sizes, "Pintti", 0.568, R.drawable.ic_size_pint_pint,
                ++order)
        val sizeLargeBeer = createSize(sizes, "Tuoppi", 0.5, R.drawable.ic_size_pint_large,
                ++order)
        val sizeEuro = createSize(sizes, "Eurotuoppi", 0.4, R.drawable.ic_size_pint_euro,
                ++order)
        val sizeHalfPint = createSize(sizes, "Puolikas tuoppi", 0.25,
                R.drawable.ic_size_pint_half, ++order)
        val sizeBottle = createSize(sizes, "Pullo", 0.33, R.drawable.ic_size_beer_bottle,
                ++order)
        val sizeShot = createSize(sizes, "Shotti", 0.04, R.drawable.ic_size_shot, ++order)
        val sizeShotDouble = createSize(sizes, "Tuplashotti", 0.08, R.drawable.ic_size_shot,
                ++order)
        val sizeShotHalf = createSize(sizes, "Puolikas shotti", 0.02,
                R.drawable.ic_size_shot, ++order)
        val sizeDrop = createSize(sizes, "Tilkka", 0.01, R.drawable.ic_size_shot, ++order)
        val sizeGlass = createSize(sizes, "Lasi", 0.25, R.drawable.ic_size_glass, ++order)
        val sizeGlassSmall = createSize(sizes, "Pieni lasi", 0.15,
                R.drawable.ic_size_glass_small, ++order)
        val sizeWineDessert = createSize(sizes, "Jälkiruokaviinilasi", 0.08,
                R.drawable.ic_size_wine_small, ++order)
        val sizeWineSmall = createSize(sizes, "Pieni viinilasi", 0.12,
                R.drawable.ic_size_wine_small, ++order)
        val sizeWineMedium = createSize(sizes, "Viinilasi", 0.16,
                R.drawable.ic_size_wine_medium, ++order)
        val sizeWineSmallBottle = createSize(sizes, "Pikkupullo", 0.1875,
                R.drawable.ic_size_wine_bottle, ++order)
        val sizeWineLarge = createSize(sizes, "Iso viinilasi", 0.24,
                R.drawable.ic_size_wine_large, ++order)
        val sizeWineBottle = createSize(sizes, "Viinipullo", 0.75,
                R.drawable.ic_size_wine_bottle, ++order)
        val sizeWineHalfBottle = createSize(sizes, "Puolikas pullo", 0.375,
                R.drawable.ic_size_wine_bottle, ++order)
        val sizeWineChampagne = createSize(sizes, "Samppanjalasi", 0.12,
                R.drawable.ic_size_champagne, ++order)

        // Kaljat
        run {
            order = 0
            val beerSizes = arrayOf(sizeLargeBeer, sizeEuro, sizeBottle, sizeHalfPint)
            val cat = library.createCategory("Miedot", "cat_beers")
            cat.createDrink("Keppana", 4.6, "drink_beer_pint", beerSizes, ++order)
            cat.createDrink("Ykkönen", 2.5, "drink_beer_bottle", arrayOf(sizeBottle), ++order)
            cat.createDrink("Nelonen", 5.2, "drink_beer_pint", beerSizes, ++order)
            cat.createDrink("Pukki", 6.0, "drink_beer_can", beerSizes, ++order)
            cat.createDrink("Siideri", 4.7, "drink_cider_bottle", beerSizes, ++order)
            cat.createDrink("Vahva lonkero", 5.5, "drink_cider_bottle", beerSizes, ++order)
            cat.createDrink("Ykköslonkero", 2.6, "drink_cider_bottle", beerSizes, ++order)
        }
        // Viinit
        run {
            order = 0
            val wineSizes = arrayOf(sizeWineSmall, sizeWineMedium, sizeWineLarge, sizeWineSmallBottle, sizeWineBottle, sizeWineHalfBottle)
            val cat = library.createCategory("Viinit", "cat_wines")
            cat.createDrink("Punaviini", 12.0, "drink_wine_red", wineSizes, ++order)
            cat.createDrink("Valkoviini", 12.0, "drink_wine_white", wineSizes, ++order)
            cat.createDrink("Vahvempi viini", 14.0, "drink_wine_red", wineSizes, ++order)
            cat.createDrink("Jälkiruokaviini", 18.0, "drink_wine_dessert", wineSizes, ++order)
            cat.createDrink("Skumppa", 12.0, "drink_champagne", arrayOf(sizeWineChampagne), ++order)
        }

        // Pitkät
        run {
            order = 0
            val cat = library.createCategory("Pitkät", "cat_longdrinks")
            cat.createDrink("Gin tonic", 6.08, "drink_long_gt", arrayOf(sizeGlass),
                    ++order)
            cat.createDrink("Caipirosca", 6.08, "drink_long_drink",
                    arrayOf(sizeGlass), ++order)
            cat.createDrink("Mojito", 6.08, "drink_long_drink", arrayOf(sizeGlass),
                    ++order)
        }

        // Viinat
        run {
            order = 0
            val cat = library.createCategory("Viinat", "cat_spirits")
            cat.createDrink("Kossu", 38.0, "drink_vodka", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Vodka", 40.0, "drink_vodka2", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Viski", 43.0, "drink_whisky", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Konjakki", 42.0, "drink_whisky_bottle2", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Punssi", 21.0, "drink_wine_dessert", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Baileys", 17.0, "drink_shot", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Vana tallinn", 40.0, "drink_rum", arrayOf(sizeShot, sizeShotHalf, sizeShotDouble, sizeDrop), ++order)
            cat.createDrink("Hot shot", 30.0, "drink_irish_coffee", arrayOf(sizeShotHalf),
                    ++order)
            cat.createDrink("Irish coffee", 6.08, "drink_irish_coffee",
                    arrayOf(sizeGlass), ++order)
        }

        // Boolit
        run {
            order = 0
            val cat = library.createCategory("Boolit", "cat_punches")
            cat.createDrink("Perusbooli", 5.0, "drink_punch", arrayOf(sizeGlass, sizeGlassSmall), ++order)
            cat.createDrink("Tuju booli", 7.5, "drink_punch", arrayOf(sizeGlass, sizeGlassSmall), ++order)
            cat.createDrink("Jytky booli", 10.0, "drink_punch", arrayOf(sizeGlass, sizeGlassSmall), ++order)
        }
    }

}
