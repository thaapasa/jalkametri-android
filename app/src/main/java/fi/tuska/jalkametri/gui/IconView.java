/**
 * Copyright 2006-2011 Tuukka Haapasalo
 * 
 * This file is part of jAlkaMetri.
 * 
 * jAlkaMetri is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * jAlkaMetri is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with jAlkaMetri (LICENSE.txt). If not, see <http://www.gnu.org/licenses/>.
 */
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
