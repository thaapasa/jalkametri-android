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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.widget.SimpleAdapter;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.util.Converter;

public class TextIconSpinnerAdapter<T> extends SimpleAdapter {

    private static final String TEXT_KEY = "text";
    private static final String ANDROID_TEXT_KEY = "text1";
    private static final String ICON_KEY = "icon";
    private final List<T> itemList;

    public TextIconSpinnerAdapter(Context context, List<T> valueList,
        Converter<T, String> textConverter, Converter<T, String> iconConverter) {
        super(context, createDataList(valueList, textConverter, iconConverter),
            R.layout.spinner_texticon, new String[] { TEXT_KEY, ANDROID_TEXT_KEY, ICON_KEY },
            new int[] { R.id.text, android.R.id.text1, R.id.icon });
        this.itemList = valueList;

        // setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setDropDownViewResource(R.layout.spinner_texticon);
    }

    private static <T> List<? extends Map<String, ?>> createDataList(List<T> valueList,
        Converter<T, String> textConverter, Converter<T, String> iconConverter) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (T item : valueList) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(TEXT_KEY, textConverter.convert(item));
            map.put(ANDROID_TEXT_KEY, textConverter.convert(item));
            String iconName = iconConverter.convert(item);
            map.put(ICON_KEY, DrinkIconUtils.getDrinkIconRes(iconName));
            list.add(map);
        }

        return list;
    }

    public void addItem(T item) {
        itemList.add(item);
    }

    @Override
    public T getItem(int position) {
        return itemList.get(position);
    }

    public int getItemCount() {
        return itemList.size();
    }

    /**
     * Tries to find a given item from the list.
     * 
     * @param soughtItem the item to search for
     * @return the item position; or -1, if item not found
     */
    public int findItem(T soughtItem) {
        if (soughtItem == null)
            return -1;
        int pos = 0;
        for (T item : itemList) {
            if (soughtItem.equals(item))
                return pos;
            ++pos;
        }
        return -1;
    }

}
