package fi.tuska.jalkametri;

import fi.tuska.jalkametri.db.DBAdapter;

/**
 * An interface for activities that have a GUI that can be updated.
 *
 * @author Tuukka Haapasalo
 */
public interface DBActivity {

    /**
     * Updates the UI by loading the data (usually from database). This may be
     * called many times for an activity.
     */
    DBAdapter getDBAdapter();

}
