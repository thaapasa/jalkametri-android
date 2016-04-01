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

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import fi.tuska.jalkametri.dao.NamedIcon;

public class IconAdapter<T extends NamedIcon> extends NamedIconAdapter<T> {

    public IconAdapter(Context c, List<T> icons, int defaultIconRes) {
        super(c, icons, true, defaultIconRes);
    }

    // create a new ImageView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view;
        if (convertView == null) {
            // If it's not recycled, create it
            view = new ImageView(context);
        } else {
            view = (ImageView) convertView;
        }

        T icon = getItem(position);
        // Set image
        int res = DrinkIconUtils.getDrinkIconRes(icon.getIcon());
        view.setImageResource(res != 0 ? res : defaultIconRes);
        return view;
    }

}
