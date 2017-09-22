package fi.tuska.jalkametri.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.tuska.jalkametri.R;

public class TextIconView extends LinearLayout {

    private TextView textView;
    private ImageView iconView;

    public TextIconView(Context context, AttributeSet attrs, boolean vertical) {
        super(context, attrs);
        initView(vertical);
    }

    public TextIconView(Context context, boolean vertical) {
        super(context);
        initView(vertical);
    }

    private void initView(boolean vertical) {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(
            Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(vertical ? R.layout.text_icon_vertical : R.layout.text_icon_horizontal, this,
            true);

        textView = (TextView) findViewById(R.id.text);
        iconView = (ImageView) findViewById(R.id.icon);
        assert textView != null;
        assert iconView != null;
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public String getText() {
        return textView.getText().toString();
    }

    public void setImageResource(int resID) {
        iconView.setImageResource(resID);
    }

    @Override
    public String toString() {
        return getText();
    }

}
