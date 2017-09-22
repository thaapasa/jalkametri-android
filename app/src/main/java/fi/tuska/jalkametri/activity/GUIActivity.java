package fi.tuska.jalkametri.activity;

import android.content.Context;

/**
 * An interface for activities that have a GUI that can be updated.
 *
 * @author Tuukka Haapasalo
 */
public interface GUIActivity {

    /**
     * Updates the UI by loading the data (usually from database). This may be
     * called many times for an activity.
     */
    void updateUI();

    Context getContext();

}
