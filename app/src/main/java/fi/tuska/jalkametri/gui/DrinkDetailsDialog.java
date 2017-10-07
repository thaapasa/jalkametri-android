package fi.tuska.jalkametri.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.data.Drink;
import fi.tuska.jalkametri.data.DrinkSelection;
import fi.tuska.jalkametri.data.DrinkSize;
import fi.tuska.jalkametri.util.StringUtil;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class DrinkDetailsDialog extends Dialog {

    private DateTimeFormatter dateFormat;
    private Locale locale;

    public DrinkDetailsDialog(Context context) {
        super(context);
        TimeUtil t = new TimeUtil(context);
        dateFormat = t.getDateFormatFull();
        locale = t.getLocale();
        setContentView(R.layout.activity_show_event);

        Button okButton = (Button) findViewById(R.id.ok);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void showDrinkSelection(DrinkSelection selection, boolean showTime) {
        Resources res = getContext().getResources();
        setTitle(selection.getIconText(res));

        Drink drink = selection.getDrink();
        // Drink name
        setDialogText(R.id.name, drink.getName());
        // Drink strength
        setDialogText(R.id.strength,
                String.format(locale, "%.1f %s", drink.getStrength(), res.getString(R.string.unit_percent)));

        DrinkSize size = selection.getSize();
        // Drink size name
        setDialogText(R.id.size_name, size.getName());
        // Drink size (volume, in liters)
        setDialogText(R.id.size, size.getFormattedSize(getContext()));

        // Portions
        setDialogText(
                R.id.portions,
                String.format(locale, "%.1f %s", selection.getPortions(getContext()),
                        res.getString(R.string.unit_portions)));

        // Date
        {
            TextView drinkTime = (TextView) findViewById(R.id.date);
            if (showTime) {
                drinkTime.setVisibility(View.VISIBLE);
                setDialogText(R.id.date,
                        StringUtil.INSTANCE.uppercaseFirstLetter(dateFormat.print(selection.getTime())));
            } else {
                drinkTime.setVisibility(View.GONE);
            }
        }

        // Icon
        {
            ImageView icon = (ImageView) findViewById(R.id.icon);
            int resID = DrinkIconUtils.getDrinkIconRes(selection.getIcon());
            icon.setImageResource(resID != 0 ? resID : R.mipmap.ic_launcher);
        }

        // Comment
        {
            TextView commentText = (TextView) findViewById(R.id.comment);
            String comment = drink.getComment();
            if ("".equals(comment))
                comment = null;
            if (comment != null) {
                commentText.setText(comment);
            }
            commentText.setVisibility(comment != null ? View.VISIBLE : View.GONE);
        }
    }

    private void setDialogText(int resID, String text) {
        TextView t = (TextView) findViewById(resID);
        if (t != null) {
            t.setText(text);
        }
    }

}
