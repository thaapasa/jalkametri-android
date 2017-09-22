package fi.tuska.jalkametri.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TimePicker;

public class TimePicker24 extends TimePicker {

    public TimePicker24(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize();
    }

    public TimePicker24(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public TimePicker24(Context context) {
        super(context);
        initialize();
    }

    public void initialize() {
        setIs24HourView(true);
    }

}
