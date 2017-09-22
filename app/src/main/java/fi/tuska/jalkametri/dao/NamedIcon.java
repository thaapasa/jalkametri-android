package fi.tuska.jalkametri.dao;

import android.content.res.Resources;

public interface NamedIcon {

    /**
     * Icon text can vary based on the currently selected locale, so resources
     * must be specified.
     *
     * @param res the resources
     * @return the icon text
     */
    String getIconText(Resources res);

    String getIcon();

}
