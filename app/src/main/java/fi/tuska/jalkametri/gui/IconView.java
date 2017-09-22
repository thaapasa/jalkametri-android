package fi.tuska.jalkametri.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.data.IconName;

public class IconView extends ImageView {

    private IconName icon = new IconName(Common.DEFAULT_ICON_NAME);

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public IconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconView(Context context) {
        super(context);
    }

    public void setIcon(IconName icon) {
        this.icon = icon;
        int res = DrinkIconUtils.getDrinkIconRes(icon.getIcon());
        setImageResource(res != 0 ? res : Common.DEFAULT_ICON_RES);
    }

    public void setIcon(String icon) {
        setIcon(new IconName(icon));
    }

    public IconName getIcon() {
        return icon;
    }

}
