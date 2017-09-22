package fi.tuska.jalkametri.dao;

import java.util.List;

import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;

public interface DrinkCategory extends NamedIcon, DataObject {

    /**
     * C: create
     */
    Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes);
    Drink createDrink(String name, double strength, String icon, DrinkSize[] sizes, int order);

    /**
     * R: read
     */
    Drink getDrink(long index);

    /**
     * U: update
     */
    boolean updateDrink(long index, Drink drinkInfo);

    /**
     * D: delete
     */
    boolean deleteDrink(long index);

    List<Drink> getDrinks();

    void setName(String name);

    void setIcon(String icon);

}
