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

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.WindowManager;
import android.widget.ImageView;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.dao.DrinkStatus;
import fi.tuska.jalkametri.dao.DrinkStatus.DrivingState;
import fi.tuska.jalkametri.util.MathUtil;

/**
 * Draws an alcohol level gauge.
 * 
 * @author Tuukka Haapasalo
 */
public final class AlcoholLevelView extends AppCompatImageView {

    private Paint textPaint;
    private Paint gaugePaint;
    private Paint gaugeShadowPaint;

    private static final double GAUGE_CENTER_X = 0.5d;
    private static final double GAUGE_CENTER_Y = 0.596d;
    private static final double GAUGE_START_POS_X = 0.0666d;
    private static final double GAUGE_END_POS_X = 0.35d;

    private static final double TEXT_CENTER_X = 0.5d;
    private static final double TEXT_CENTER_Y = 0.8d;

    private static final double TEXT_BG_CENTER_X = 0.5d;
    private static final double TEXT_BG_CENTER_Y = 0.8d;

    /** 25 degrees = 0.436332 radians */
    private static final double ANGLE_OVERPOS = 0.436332d;
    private static final double START_ANGLE = Math.PI + ANGLE_OVERPOS;
    private static final double END_ANGLE = 0 - ANGLE_OVERPOS;

    private static final double MAX_ALCOHOL_LEVEL = 2;
    private double level = 0;
    private DrinkStatus.DrivingState drivingState = DrivingState.DrivingOK;

    private Drawable meterBg1;
    private Drawable meterBg2;
    private Drawable meterBg3;

    public AlcoholLevelView(Context ctx) {
        super(ctx);
        initView();
    }

    public AlcoholLevelView(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        initView();
    }

    public AlcoholLevelView(Context ctx, AttributeSet attr, int defaultStyle) {
        super(ctx, attr, defaultStyle);
        initView();
    }

    protected void initView() {
        setFocusable(false);

        Resources res = getResources();

        meterBg1 = res.getDrawable(R.drawable.meter_bg_1);
        meterBg2 = res.getDrawable(R.drawable.meter_bg_2);
        meterBg3 = res.getDrawable(R.drawable.meter_bg_3);
        assert meterBg1 != null;
        assert meterBg2 != null;
        assert meterBg3 != null;
        meterBg1.setBounds(0, 0, meterBg1.getIntrinsicWidth(), meterBg1.getIntrinsicHeight());
        meterBg2.setBounds(0, 0, meterBg2.getIntrinsicWidth(), meterBg2.getIntrinsicHeight());
        meterBg3.setBounds(0, 0, meterBg3.getIntrinsicWidth(), meterBg3.getIntrinsicHeight());

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(res.getColor(R.color.alcohol_text_color));
        // textPaint.setShadowLayer(3, 0, 0,
        // res.getColor(R.color.alcohol_text_shadow));
        textPaint.setTextSize(getGaugeTextSize());

        textPaint.setFakeBoldText(true);

        gaugePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gaugePaint.setStrokeWidth(2);
        gaugePaint.setStyle(Paint.Style.STROKE);
        gaugePaint.setColor(res.getColor(R.color.alcohol_gauge_color));
        gaugePaint.setStrokeMiter(2);

        gaugeShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gaugeShadowPaint.setStrokeWidth(4);
        gaugeShadowPaint.setStyle(Paint.Style.STROKE);
        gaugeShadowPaint.setColor(res.getColor(R.color.alcohol_gauge_shadow_color));
        gaugeShadowPaint.setStrokeMiter(5);
    }

    private int getGaugeTextSize() {
        WindowManager mgr = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mgr.getDefaultDisplay().getMetrics(metrics);
        return (int) (metrics.density * 27);
    }

    public void setLevel(double level, DrinkStatus.DrivingState drivingState) {
        this.level = level;
        this.drivingState = drivingState;

        invalidate();
    }

    public double getLevel() {
        return level;
    }

    public DrinkStatus.DrivingState getDrivingState() {
        return drivingState;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the gauge meter
        int cx = (int) (getWidth() * GAUGE_CENTER_X);
        int cy = (int) (getHeight() * GAUGE_CENTER_Y);

        // Draw gauge background
        drawMeterBackground(canvas);
        // Draw the alcohol level text
        drawAlcoholLevelText(canvas);

        double pos = Math.min(MathUtil.getPosition(level, 0, MAX_ALCOHOL_LEVEL), 1d);
        double angle = pos * (END_ANGLE - START_ANGLE) + START_ANGLE;

        double gaugeStartPos = getWidth() * GAUGE_START_POS_X;
        double gaugeEndPos = getWidth() * GAUGE_END_POS_X;

        Pair<Double, Double> startCoords = MathUtil.polarToRectangular(gaugeStartPos, angle);
        Pair<Double, Double> endCoords = MathUtil.polarToRectangular(gaugeEndPos, angle);

        // Draw the gauge meter line
        drawLine(canvas, cx, cy, startCoords, endCoords, gaugeShadowPaint);
        drawLine(canvas, cx, cy, startCoords, endCoords, gaugePaint);
    }

    private void drawMeterBackground(Canvas canvas) {
        switch (drivingState) {
        case DrivingOK:
        case DrivingMaybe:
            drawMeterBackground(canvas, meterBg1);
            break;
        case DrivingNo:
            if (level < MAX_ALCOHOL_LEVEL) {
                drawMeterBackground(canvas, meterBg2);
            } else {
                drawMeterBackground(canvas, meterBg3);
            }
            break;
        }
    }

    private void drawMeterBackground(Canvas canvas, Drawable drawable) {
        canvas.save();
        int xp = (int) (TEXT_BG_CENTER_X * getWidth() - drawable.getIntrinsicWidth() / 2.0f);
        int yp = (int) (TEXT_BG_CENTER_Y * getHeight() - drawable.getIntrinsicHeight() / 2.0f);
        canvas.translate(xp, yp);
        drawable.draw(canvas);
        canvas.restore();
    }

    private void drawAlcoholLevelText(Canvas canvas) {
        String text = String.format("%.2f", level);
        Rect textBounds = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        // textPaint.setColor(gaugeColor.getColor(pos));

        int tcx = (int) (getWidth() * TEXT_CENTER_X);
        int tcy = (int) (getHeight() * TEXT_CENTER_Y);

        canvas.drawText(text, tcx - textBounds.width() / 2, tcy + textBounds.height() / 2,
            textPaint);
    }

    private void drawLine(Canvas canvas, int cx, int cy, Pair<Double, Double> startPos,
        Pair<Double, Double> endPos, Paint paint) {
        canvas.drawLine((float) (cx + startPos.first), (float) (cy - startPos.second),
            (float) (cx + endPos.first), (float) (cy - endPos.second), paint);
    }
}
