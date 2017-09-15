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
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Draws a pretty ugly color-sliding alcohol level gauge. This is not actually
 * used in the GUI, this was just used to create the initial gauge picture
 * which was then screen-captured and stored as an image.
 * 
 * This file is left here as an example of how to make a custom, very slow,
 * color-sliding thingy.
 * 
 * @author Tuukka Haapasalo
 */
public abstract class BufferedImageView extends ImageView {

    private Bitmap buffer = null;
    private Canvas bufferCanvas = null;
    private boolean bufferValid = false;

    private int bgColor = Color.TRANSPARENT;
    private Paint bgPaint;

    public BufferedImageView(Context ctx) {
        super(ctx);
        init();
    }

    public BufferedImageView(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        init();
    }

    public BufferedImageView(Context ctx, AttributeSet attr, int defaultStyle) {
        super(ctx, attr, defaultStyle);
        init();
    }

    private void init() {
        bgPaint = new Paint();
        bgPaint.setStyle(Style.FILL);
        bgPaint.setColor(bgColor);
    }

    protected void validateBufferedImage() {
        if (buffer == null || buffer.getWidth() != getWidth()
            || buffer.getHeight() != getHeight()) {
            buffer = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            bufferCanvas = new Canvas(buffer);
            bufferValid = false;
        }
        if (!bufferValid) {
            // Clear the image
            bufferCanvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);
            createImage(bufferCanvas);
            bufferValid = true;
        }
    }

    public void invalidateBuffer() {
        bufferValid = false;
        postInvalidate();
    }

    protected abstract void createImage(Canvas canvas);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        validateBufferedImage();

        canvas.drawBitmap(buffer, 0, 0, null);
    }

}
