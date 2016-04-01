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
import android.widget.BaseAdapter;
import fi.tuska.jalkametri.dao.NamedIcon;
import fi.tuska.jalkametri.util.Converter;

public class NamedIconAdapter<T extends NamedIcon> extends BaseAdapter {

    protected final Context context;
    protected final int defaultIconRes;
    private final List<T> icons;
    private final boolean vertical;
    private final Converter<NamedIcon, String> textFormatter;

    public NamedIconAdapter(Context c, List<T> icons, boolean vertical, int defaultIconRes) {
        this.context = c;
        this.icons = icons;
        this.vertical = vertical;
        this.textFormatter = null;
        this.defaultIconRes = defaultIconRes;
    }

    public NamedIconAdapter(Context c, List<T> icons, boolean vertical,
        Converter<NamedIcon, String> textFormatter, int defaultIconRes) {
        this.context = c;
        this.icons = icons;
        this.vertical = vertical;
        this.textFormatter = textFormatter;
        this.defaultIconRes = defaultIconRes;
    }

    @Override
    public int getCount() {
        return icons.size();
    }

    @Override
    public T getItem(int position) {
        return icons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // create a new TextIconView for each item referenced by the Adapter
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextIconView view;
        if (convertView == null) {
            // If it's not recycled, create it
            view = new TextIconView(context, vertical);
        } else {
            view = (TextIconView) convertView;
        }

        T icon = getItem(position);

        // Set image
        int res = DrinkIconUtils.getDrinkIconRes(icon.getIcon());
        view.setImageResource(res != 0 ? res : defaultIconRes);
        // Set text
        String text = textFormatter != null ? textFormatter.convert(icon) : icon
            .getIconText(context.getResources());
        view.setText(text);
        return view;
    }

}
