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

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import fi.tuska.jalkametri.Common;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.IconName;
import fi.tuska.jalkametri.util.ObjectCallback;

public class IconPickerDialog extends Dialog {

    private IconAdapter<IconName> adapter;
    private ObjectCallback<IconName> callback;

    public IconPickerDialog(Context context, ObjectCallback<IconName> callback) {
        super(context);
        this.callback = callback;
        setContentView(R.layout.select_icon);
        GridView list = (GridView) findViewById(R.id.list);

        List<IconName> icons = DrinkIconUtils.getAsList();
        adapter = new IconAdapter<IconName>(getContext(), icons, Common.DEFAULT_ICON_RES);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long arg3) {
                selectIcon(adapter.getItem(position));
            }
        });
    }

    public void selectIcon(IconName icon) {
        if (callback != null)
            callback.objectSelected(icon);
        
        dismiss();
    }

}
