package fi.tuska.jalkametri.dao;

import java.util.List;

import fi.tuska.jalkametri.data.DrinkEvent;
import fi.tuska.jalkametri.data.DrinkSelection;

public interface Favourites {

    /**
     * C: create
     */
    void createFavourite(DrinkSelection fav);

    /**
     * R: read
     */
    DrinkEvent getFavourite(long index);

    /**
     * U: update
     */
    boolean updateFavourite(long index, DrinkEvent fav);

    /**
     * D: delete
     */
    boolean deleteFavourite(long index);

    List<DrinkEvent> getFavourites();

    List<DrinkEvent> getFavourites(int limit);

}
