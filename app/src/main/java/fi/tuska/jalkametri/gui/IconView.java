package fi.tuska.jalkametri.gui;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import fi.tuska.jalkametri.data.IconName;

import static fi.tuska.jalkametri.Common.DEFAULT_ICON_NAME;
import static fi.tuska.jalkametri.Common.DEFAULT_ICON_RES;

public class IconView extends AppCompatImageView {

    private IconName icon = new IconName(DEFAULT_ICON_NAME);

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
        setImageResource(res != 0 ? res : DEFAULT_ICON_RES);
    }

    public void setIcon(String icon) {
        setIcon(new IconName(icon));
    }

    public IconName getIcon() {
        return icon;
    }

}
