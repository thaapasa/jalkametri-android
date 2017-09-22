package fi.tuska.jalkametri.dao;

import java.util.List;

import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSize;

public interface DrinkSizes extends DataLibrary {

    List<DrinkSize> getAllSizes();

    /**
     * C: create
     */
    DrinkSize createSize(String name, double size, String icon);
    DrinkSize createSize(String name, double size, String icon, int order);

    /**
     * R: read
     */
    DrinkSize getSize(long index);

    /**
     * U: update
     */
    boolean updateSize(long index, DrinkSize newSize);

    /**
     * D: delete
     */
    boolean deleteSize(long index);

    /**
     * Tries to find the given size from the list of drink sizes; return it if
     * found, null otherwise.
     */
    DrinkSize findSize(DrinkSize size);

    DrinkSize getDefaultSize();

    List<DrinkSize> getSizes(Drink drink);

}
