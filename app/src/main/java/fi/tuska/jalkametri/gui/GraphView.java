package fi.tuska.jalkametri.gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.Instant;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a customizable graph.
 *
 * @author Tuukka Haapasalo
 */
public final class GraphView extends BufferedImageView {

    private static final String TAG = "GraphDrawer";

    public interface Point {
        double getPosition();

        double getValue();
    }

    ;

    public interface Label {
        double getPosition();

        String getLabel();
    }

    public interface Graph {
        List<Point> getPoints();

        List<Label> getPositionLabels();

        List<Label> getValueLabels(Double maxValue);

        Double getPreferredMinPosition();

        Double getPreferredMaxPosition();

        Double getPreferredMinValue();

        Double getPreferredMaxValue();

        double validateMaxValue(Double value);

        double getBarWidth();

        int getPointColor(double position, double value);
    }

    ;

    private Paint bgPaint;
    private Paint gridPaint;
    private Paint borderPaint;
    private Paint barPaint;
    private int defaultBarColor;
    private Paint labelPaint;

    private List<Graph> graphs = new ArrayList<GraphView.Graph>();

    public GraphView(Context ctx) {
        super(ctx);
        initView();
    }

    public GraphView(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        initView();
    }

    public GraphView(Context ctx, AttributeSet attr, int defaultStyle) {
        super(ctx, attr, defaultStyle);
        initView();
    }

    protected void initView() {
        setFocusable(false);
        Resources res = getResources();

        bgPaint = new Paint();
        bgPaint.setColor(res.getColor(R.color.graph_bg_color));
        bgPaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint();
        gridPaint.setColor(res.getColor(R.color.graph_grid_color));
        gridPaint.setStyle(Paint.Style.STROKE);

        borderPaint = new Paint();
        borderPaint.setColor(res.getColor(R.color.graph_border_color));
        borderPaint.setStyle(Paint.Style.STROKE);

        labelPaint = new Paint();
        labelPaint.setColor(res.getColor(R.color.graph_label_color));
        labelPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        labelPaint.setTextSize(16);

        barPaint = new Paint();
        defaultBarColor = res.getColor(R.color.graph_line_color);
        barPaint.setColor(defaultBarColor);
        barPaint.setStyle(Paint.Style.FILL);
    }

    public void clear() {
        graphs.clear();
        invalidateBuffer();
    }

    public void addGraph(Graph graph) {
        graphs.add(graph);
        invalidateBuffer();
    }

    private static long DATE_OFFSET = Instant.now().toDate().getTime();

    public static final double dateToPosition(LocalDate date) {
        // Used timezone and time values do not matter as long as each date is processed similarly
        // Subtract a "relatively close-by offset" (determined at application startup)
        // to make double values closer to zero
        return date.toDate().getTime() - DATE_OFFSET;
    }

    public static double dateToPositionAtStart(LocalDate date) {
        return dateToPosition(date) - HALF_DAY;
    }

    public static final long HALF_DAY = TimeUtil.Companion.getDAY_MS() / 2;

    @Override
    protected void createImage(Canvas canvas) {
        // Draw background
        canvas.drawRect(0, 0, getWidth(), getHeight(), bgPaint);

        LogUtil.INSTANCE.d(TAG, "Graph drawer dimensions are %d x %d = %d x %d", getWidth(), getHeight(),
                canvas.getWidth(), canvas.getHeight());

        int labelHeight = (int) (labelPaint.getTextSize() + 6);

        int graphPadLeft = 20;
        int graphPadRight = 0;
        int graphPadTop = (int) -labelPaint.ascent() / 2;
        int graphPadBottom = 3 + labelHeight;

        for (Graph g : graphs) {
            drawGraph(canvas, g, graphPadLeft + 1, graphPadTop + 1, canvas.getWidth()
                    - (graphPadLeft + graphPadRight + 2), canvas.getHeight()
                    - (graphPadTop + graphPadBottom + 2));
        }

        // Draw borders
        canvas.drawRect(graphPadLeft, graphPadTop, canvas.getWidth() - graphPadRight - 1,
                canvas.getHeight() - graphPadBottom - 1, borderPaint);

    }

    protected void drawGraph(Canvas canvas, Graph graph, int padX, int padY, int width, int height) {
        GraphInfo info = new GraphInfo(graph, width, height);

        // Draw labels
        for (Label l : graph.getPositionLabels()) {
            drawGraphPositionLabel(canvas, l, info, padX, padY);
        }
        for (Label l : graph.getValueLabels(info.maxValue)) {
            drawGraphInfoLabel(canvas, l, info, padX, padY);
        }

        LogUtil.INSTANCE.d(TAG, "Graph area dimensions are %d x %d, padding %d x %d", width, height, padX,
                padY);

        Point last = null;
        for (Point p : graph.getPoints()) {
            drawGraphPoint(canvas, graph, p, last, info, padX, padY);
            last = p;
        }

    }

    protected void drawGraphPositionLabel(Canvas canvas, Label l, GraphInfo info, int padX,
                                          int padY) {
        float gx = info.getBarX1(l.getPosition()) + padX;
        float gy = canvas.getHeight() - labelPaint.descent();
        canvas.drawLine(gx, padY, gx, canvas.getHeight(), gridPaint);

        float lx = gx + 2;
        float ly = gy;
        canvas.drawText(l.getLabel(), lx, ly, labelPaint);
    }

    protected void drawGraphInfoLabel(Canvas canvas, Label l, GraphInfo info, int padX, int padY) {
        float gx = padX;
        float gy = info.height - info.getY(l.getPosition()) + padY;
        canvas.drawLine(gx, gy, gx + info.width, gy, gridPaint);

        float lx = 0;
        float ly = gy - labelPaint.ascent() / 2f;
        canvas.drawText(l.getLabel(), lx, ly, labelPaint);
    }

    protected void drawGraphPoint(Canvas canvas, Graph graph, Point cur, Point last,
                                  GraphInfo info, int padX, int padY) {

        double pos = cur.getPosition();
        double val = cur.getValue();

        int color = graph.getPointColor(pos, val);
        barPaint.setColor(color);

        float x1 = info.getBarX1(pos) + padX;
        float x2 = info.getBarX2(pos) + padX;
        float y = info.height - (info.getY(val)) + padY;
        canvas.drawRect(x1, y, x2, info.height + padY, barPaint);
    }

    private class GraphInfo {
        private Double minPosition;
        private Double maxPosition;
        private Double minValue;
        private Double maxValue;
        private int width;
        private int height;
        private float halfBarWidth;

        private GraphInfo(Graph graph, int width, int height) {
            this.width = width;
            this.height = height;
            this.halfBarWidth = (float) (graph.getBarWidth() / 2d);
            List<Point> points = graph.getPoints();
            minPosition = graph.getPreferredMinPosition();
            maxPosition = graph.getPreferredMaxPosition();
            minValue = graph.getPreferredMinValue();
            maxValue = graph.getPreferredMaxValue();
            LogUtil.INSTANCE.d(TAG, "Graph preferred pos range %.2f-%.2f", minPosition, maxPosition);
            for (Point p : points) {
                if (minPosition == null || p.getPosition() < minPosition)
                    minPosition = p.getPosition();
                if (maxPosition == null || p.getPosition() > maxPosition)
                    maxPosition = p.getPosition();
                if (minValue == null || p.getValue() < minValue)
                    minValue = p.getValue();
                if (maxValue == null || p.getValue() > maxValue)
                    maxValue = p.getValue();
            }
            maxValue = graph.validateMaxValue(maxValue);
            LogUtil.INSTANCE
                    .d(TAG, "Graph pos range after point scan %.2f-%.2f", minPosition, maxPosition);
        }

        public float getX(double position) {
            return (float) ((position - minPosition) * width / (maxPosition - minPosition));
        }

        public float getY(double value) {
            return (float) ((value - minValue) * height / (maxValue - minValue));
        }

        public float getBarX1(double position) {
            return getX(position - halfBarWidth);
        }

        public float getBarX2(double position) {
            return getX(position + halfBarWidth);
        }

    }

}
