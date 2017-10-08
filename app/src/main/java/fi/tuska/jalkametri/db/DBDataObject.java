package fi.tuska.jalkametri.db;

import java.io.Serializable;

import fi.tuska.jalkametri.dao.DataObject;
import fi.tuska.jalkametri.util.AssertionUtils;

public abstract class DBDataObject implements DataObject, Serializable {

    private static final long serialVersionUID = 7481036354555634797L;

    private static final long INVALID_ID = -1;

    private final long index;

    protected DBDataObject() {
        index = INVALID_ID;
    }

    protected DBDataObject(long index) {
        this.index = index;
        AssertionUtils.INSTANCE.expect(index != 0);
        enforceBackedObject(index);
    }

    @Override
    public final long getIndex() {
        return index;
    }

    /**
     * @return true if this data object is backed to a back-end storage (e.g.,
     * into a database.
     */
    @Override
    public boolean isBacked() {
        return index != INVALID_ID;
    }

    public static long getInvalidID() {
        return INVALID_ID;
    }

    public static boolean isValidID(long index) {
        return index != INVALID_ID;
    }

    /**
     * Checks that the given index is valid for an object that is backed to
     * back-end storage.
     *
     * @param index the object index
     * @throws IllegalArgumentException if the index does not belong to a
     * backed object
     */
    public static void enforceBackedObject(long index) {
        if (index == INVALID_ID)
            throw new IllegalArgumentException(
                "Object identifier is invalid; object is not backed to database");
    }

    /**
     * Checks that the given object is backed to back-end storage.
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is not backed
     */
    public static void enforceBackedObject(DataObject object) {
        if (!object.isBacked())
            throw new IllegalArgumentException(
                "Object identifier is invalid; object is not backed to database");
    }

}
