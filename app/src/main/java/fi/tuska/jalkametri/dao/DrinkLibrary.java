package fi.tuska.jalkametri.dao;

import java.util.List;

public interface DrinkLibrary extends DataLibrary {

    /**
     * C: create
     *
     * Adds a new category to the drink library.
     *
     * @return the new category; or null, if an error occurred
     */
    DrinkCategory createCategory(String name, String icon);

    /**
     * R: read
     */
    DrinkCategory getCategory(long index);

    /**
     * U: update
     */
    boolean updateCategory(long id, DrinkCategory category);

    /**
     * D: delete
     */
    boolean deleteCategory(long id);

    /**
     * @return the drink categories (e.g., a category might be for beers,
     * spirits, wines, etc.)
     */
    List<DrinkCategory> getCategories();

    DrinkSizes getDrinkSizes();

    void clearDrinksSizesCategories();

}
