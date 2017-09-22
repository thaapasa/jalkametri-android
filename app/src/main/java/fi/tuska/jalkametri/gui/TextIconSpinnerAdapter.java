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
