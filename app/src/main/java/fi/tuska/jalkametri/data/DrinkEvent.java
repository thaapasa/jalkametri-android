package fi.tuska.jalkametri.data;

import fi.tuska.jalkametri.dao.DataObject;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.util.AssertionUtils;
import org.joda.time.Instant;

public class DrinkEvent extends DrinkSelection implements DataObject {

    private static final long serialVersionUID = -9146835260562696879L;

    private final long index;

    public DrinkEvent(long index, Drink drink, DrinkSize size, Instant time) {
        super(drink, size, time);
        this.index = index;
        AssertionUtils.INSTANCE.expect(index != 0);
        DBDataObject.enforceBackedObject(index);
    }

    public DrinkEvent(Drink drink, DrinkSize size, Instant time) {
        super(drink, size, time);
        this.index = DBDataObject.getInvalidID();
    }

    @Override
    public long getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return getIconText(null);
    }

    @Override
    public boolean isBacked() {
        return DBDataObject.isValidID(index);
    }

}
