package fi.tuska.jalkametri.data;

import android.content.Context;
import android.content.res.Resources;
import fi.tuska.jalkametri.R;
import fi.tuska.jalkametri.activity.StatisticsDailyActivity;
import fi.tuska.jalkametri.dao.DailyDrinkStatistics;
import fi.tuska.jalkametri.dao.Preferences;
import fi.tuska.jalkametri.gui.ColorSlider;
import fi.tuska.jalkametri.gui.GraphView.Graph;
import fi.tuska.jalkametri.gui.GraphView.Label;
import fi.tuska.jalkametri.gui.GraphView.Point;
import fi.tuska.jalkametri.util.LogUtil;
import fi.tuska.jalkametri.util.TimeUtil;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;

import static fi.tuska.jalkametri.gui.GraphView.dateToPosition;
import static fi.tuska.jalkametri.gui.GraphView.dateToPositionAtStart;

public abstract class DailyStatisticsGraph implements Graph {

    private static final String TAG = "DailyStatisticsGraph";

    private final List<DailyDrinkStatistics> points;
    protected final LocalDate firstEventDate;
    protected final Preferences prefs;
    private final ColorSlider slider;
    protected final TimeUtil timeUtil;

    private DailyStatisticsGraph(List<DailyDrinkStatistics> points, Preferences prefs,
                                 Context context) {
        this.points = points;
        this.prefs = prefs;
        this.timeUtil = new TimeUtil(context);
        firstEventDate = (points.size() != 0) ? points.get(0).getDay() : null;

        this.slider = createSlider(Preferences.Gender.Male.equals(prefs.getGender()),
                context.getResources());
    }

    protected ColorSlider createSlider(boolean male, Resources res) {
        ColorSlider slider = new ColorSlider();
        slider.addColor(0, res.getColor(R.color.bar_zero));
        if (male) {
            slider.addColor(3, res.getColor(R.color.bar_ok));
            slider.addColor(6, res.getColor(R.color.bar_warn));
            slider.addColor(8, res.getColor(R.color.bar_critical));
        } else {
            slider.addColor(2, res.getColor(R.color.bar_ok));
            slider.addColor(4, res.getColor(R.color.bar_warn));
            slider.addColor(6, res.getColor(R.color.bar_critical));
        }
        return slider;
    }

    @Override
    public List<Point> getPoints() {
        return new ArrayList<Point>(points);
    }

    @Override
    public double getBarWidth() {
        return 1000 * 60 * 60 * 24d;
    }

    @Override
    public int getPointColor(double position, double value) {
        int color = slider.getColor(value);
        return color;
    }

    @Override
    public Double getPreferredMinValue() {
        return 0d;

    }

    @Override
    public Double getPreferredMaxValue() {
        return (double) (Preferences.Gender.Male.equals(prefs.getGender()) ? 8 : 6);
    }

    @Override
    public double validateMaxValue(Double value) {
        if (Preferences.Gender.Male.equals(prefs.getGender())) {
            if (value == null || value <= 8)
                return 8;
        } else {
            if (value == null || value <= 6)
                return 6;
        }
        return Math.ceil(value / 2) * 2;
    }

    @Override
    public List<Label> getValueLabels(Double maxValue) {
        List<Label> l = new ArrayList<>((int) maxValue.doubleValue() / 2 + 1);
        for (int i = 0; i <= maxValue; i += 2) {
            final int pos = i;
            l.add(new Label() {
                @Override
                public double getPosition() {
                    return pos;
                }

                @Override
                public String getLabel() {
                    return String.valueOf(pos);
                }
            });
        }
        return l;
    }

    public static DailyStatisticsGraph getGraph(StatisticsDailyActivity.Type type,
                                                List<DailyDrinkStatistics> stats, Preferences prefs, Context context) {
        switch (type) {
            case Yearly:
                return new YearlyGraph(stats, prefs, context);
            case Monthly:
                return new MonthlyGraph(stats, prefs, context);
            case Weekly:
                return new WeeklyGraph(stats, prefs, context);
        }
        LogUtil.INSTANCE.w(TAG, "Unknown type: %s", type);
        return null;
    }

    private static class YearlyGraph extends DailyStatisticsGraph {

        private final int year;
        private final DateTimeFormatter formatter = timeUtil.timeFormatter("M");

        private YearlyGraph(List<DailyDrinkStatistics> points, Preferences prefs, Context context) {
            super(points, prefs, context);
            if (firstEventDate != null) {
                year = firstEventDate.getYear();
                LogUtil.INSTANCE.d(TAG, "Yearly cal first event is %s; year is %d", firstEventDate, year);
            } else {
                year = 2000;
            }
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<Label>(12);
            for (int m = 1; m <= 12; ++m)
                l.add(new DateLabel(new LocalDate(year, m, 1), formatter));
            return l;
        }

        @Override
        public Double getPreferredMinPosition() {
            LocalDate date = new LocalDate(year, 1, 1);
            LogUtil.INSTANCE.d(TAG, "Preferred yearly min: %s", date);
            return dateToPositionAtStart(date);
        }

        @Override
        public Double getPreferredMaxPosition() {
            LocalDate date = new LocalDate(year, 12, 31).plusDays(1);
            LogUtil.INSTANCE.d(TAG, "Preferred yearly max: %s", date);
            return dateToPositionAtStart(date);
        }

    }

    private static class MonthlyGraph extends DailyStatisticsGraph {

        private int year = 2000;
        private int month = 1;
        private final DateTimeFormatter formatter = timeUtil.timeFormatter("d");

        private MonthlyGraph(List<DailyDrinkStatistics> points, Preferences prefs, Context context) {
            super(points, prefs, context);
            if (firstEventDate != null) {
                year = firstEventDate.getYear();
                month = firstEventDate.getMonthOfYear();
            }
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<Label>(12);
            int days = timeUtil.getDaysInMonth(month, year);

            LocalDate cur = new LocalDate(year, month, 1);
            for (int d = 1; d <= days; d += 3) {
                l.add(new DateLabel(cur, formatter));
                cur = cur.plusDays(3);
            }
            return l;
        }

        @Override
        public Double getPreferredMinPosition() {
            LocalDate date = new LocalDate(year, month, 1);
            LogUtil.INSTANCE.d(TAG, "Preferred monthly min: %s", date);
            return dateToPositionAtStart(date);
        }

        @Override
        public Double getPreferredMaxPosition() {
            LocalDate date = new LocalDate(year, month, timeUtil.getDaysInMonth(month, year)).plusDays(1);
            LogUtil.INSTANCE.d(TAG, "Preferred monthly max: %s", date);
            return dateToPositionAtStart(date);
        }
    }

    private static class WeeklyGraph extends DailyStatisticsGraph {

        private Preferences prefs;
        private final DateTimeFormatter formatter = timeUtil.timeFormatter("EE");

        private WeeklyGraph(List<DailyDrinkStatistics> points, Preferences prefs, Context context) {
            super(points, prefs, context);
            this.prefs = prefs;
        }

        private LocalDate getStartOfWeek() {
            return timeUtil.getStartOfWeek(firstEventDate, prefs);
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<>(7);
            LocalDate cur = getStartOfWeek();
            for (int d = 1; d <= 7; ++d) {
                l.add(new DateLabel(cur, formatter));
                cur = cur.plusDays(1);
            }
            return l;
        }

        @Override
        public Double getPreferredMinPosition() {
            LocalDate date = getStartOfWeek();
            LogUtil.INSTANCE.d(TAG, "Preferred weekly min: %s", date);
            return dateToPositionAtStart(date);
        }

        @Override
        public Double getPreferredMaxPosition() {
            LocalDate cal = getStartOfWeek();
            LocalDate max = cal.plusDays(7);
            LogUtil.INSTANCE.d(TAG, "Preferred weekly max: %s", max);
            return dateToPositionAtStart(max);
        }
    }

    private static class DateLabel implements Label {

        private LocalDate date;
        private DateTimeFormatter formatter;

        private DateLabel(LocalDate date, DateTimeFormatter formatter) {
            this.date = date;
            this.formatter = formatter;
        }

        @Override
        public double getPosition() {
            return dateToPosition(date);
        }

        @Override
        public String getLabel() {
            return formatter.print(date);
        }
    }
}
