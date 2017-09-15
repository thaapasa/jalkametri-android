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
package fi.tuska.jalkametri.data;

import java.io.Serializable;

import android.content.Context;
import android.content.res.Resources;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.NamedIcon;
import fi.tuska.jalkametri.db.DBDataObject;
import fi.tuska.jalkametri.util.CommonUtil;
import fi.tuska.jalkametri.util.NumberUtil;

public class DrinkSize extends DBDataObject implements NamedIcon, Serializable {

    private static final long serialVersionUID = 190656210067388289L;

    private static DrinkSize defaultSize;

    private String name;
    private double volume;
    private String icon;

    public DrinkSize() {
        super();
        name = "";
        volume = 0.5f;
        icon = Common.DEFAULT_ICON_NAME;
    }

    public DrinkSize(long id, String name, double volume, String icon) {
        super(id);
        this.name = name;
        this.volume = volume;
        this.icon = icon;
    }

    public DrinkSize(String name, double volume, String icon) {
        this.name = name;
        this.volume = volume;
        this.icon = icon;
    }

    public DrinkSize(DrinkSize size) {
        super();
        this.name = size.name;
        this.volume = size.volume;
        this.icon = size.icon;
    }

    public static DrinkSize getDefaultSize() {
        if (defaultSize == null) {
            defaultSize = new DrinkSize("", 0.1f, Common.DEFAULT_SIZE_ICON_NAME);
        }
        return defaultSize;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double size) {
        this.volume = size;
    }

    public String getFormattedSize(Context context) {
        Resources res = context.getResources();
        if (volume < 0.1) {
            return String.format("%d %s", (int) (volume * 100),
                res.getString(R.string.unit_centiliter));
        } else if (volume < 1) {
            return String.format("%.1f %s", volume * 10, res.getString(R.string.unit_deciliter));
        } else {
            return String.format("%.1f %s", volume, res.getString(R.string.unit_liter));
        }
    }

    @Override
    public String toString() {
        return name + " (" + volume + ")";
    }

    @Override
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getIconText(Resources res) {
        return String.format("%s\n%s l", name, NumberUtil.toString(volume, res));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DrinkSize))
            return false;
        DrinkSize size = (DrinkSize) o;
        return CommonUtil.nullOrEquals(name, size.name) && volume == size.volume;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : (int) (volume * 100);
    }

}
