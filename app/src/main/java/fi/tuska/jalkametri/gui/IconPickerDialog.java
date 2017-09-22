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
