package fi.tuska.jalkametri.dao;

public interface DataObject {

    long getIndex();

    String getName();

    /**
     * @return true if this data object is backed to a back-end storage (e.g.,
     * into a database.
     */
    boolean isBacked();

}
