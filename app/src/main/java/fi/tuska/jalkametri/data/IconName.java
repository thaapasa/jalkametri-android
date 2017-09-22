package fi.tuska.jalkametri.data;

import android.content.res.Resources;
import fi.tuska.jalkametri.dao.NamedIcon;

public class IconName implements NamedIcon {

    private String icon;

    public IconName(String icon) {
        this.icon = icon;
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getIconText(Resources res) {
        return icon;
    }

}
