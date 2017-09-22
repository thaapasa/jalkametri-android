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
