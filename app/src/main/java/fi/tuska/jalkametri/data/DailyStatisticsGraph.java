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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class DailyStatisticsGraph implements Graph {

    private static final long serialVersionUID = 7189644235567515657L;

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
        return Double.valueOf(0);

    }

    @Override
    public Double getPreferredMaxValue() {
        return Double.valueOf(Preferences.Gender.Male.equals(prefs.getGender()) ? 8 : 6);
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
        List<Label> l = new ArrayList<Label>((int) maxValue.doubleValue() / 2 + 1);
        for (int i = 0; i <= maxValue; i += 2) {
            final int pos = i;
            l.add(new Label() {
                private static final long serialVersionUID = 169443846404063640L;

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
        LogUtil.w(TAG, "Unknown type: %s", type);
        return null;
    }

    private static class YearlyGraph extends DailyStatisticsGraph {

        private final int year;
        private final DateFormat formatter = new SimpleDateFormat("M");

        private YearlyGraph(List<DailyDrinkStatistics> points, Preferences prefs, Context context) {
            super(points, prefs, context);
            if (firstEventDate != null) {
                year = firstEventDate.getYear();
                LogUtil.d(TAG, "Yearly cal first event is %s; year is %d", firstEventDate, year);
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
            Date date = timeUtil.getCalendar(year, 1, 1, 0, 0, 0).getTime();
            LogUtil.d(TAG, "Preferred yearly min: %s", date);
            return (double) date.getTime();
        }

        @Override
        public Double getPreferredMaxPosition() {
            Date date = timeUtil.getCalendar(year, 12, 31, 23, 59, 59).getTime();
            LogUtil.d(TAG, "Preferred yearly max: %s", date);
            return (double) date.getTime();
        }

    }

    private static class MonthlyGraph extends DailyStatisticsGraph {

        private static final long serialVersionUID = 1686662376323346037L;
        private int year = 2000;
        private int month = 1;
        private DateFormat formatter = new SimpleDateFormat("d");

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
            Date date = timeUtil.getCalendar(year, month, 1, 0, 0, 0).getTime();
            LogUtil.d(TAG, "Preferred monthly min: %s", date);
            return (double) date.getTime();
        }

        @Override
        public Double getPreferredMaxPosition() {
            Date date = timeUtil.getCalendar(year, month, timeUtil.getDaysInMonth(month, year),
                    23, 59, 59).getTime();
            LogUtil.d(TAG, "Preferred monthly max: %s", date);
            return (double) date.getTime();
        }
    }

    private static class WeeklyGraph extends DailyStatisticsGraph {

        private static final long serialVersionUID = 3466629440872766941L;
        private Preferences prefs;
        private final DateFormat formatter = new SimpleDateFormat("EE");

        private WeeklyGraph(List<DailyDrinkStatistics> points, Preferences prefs, Context context) {
            super(points, prefs, context);
            this.prefs = prefs;
        }

        private LocalDate getStartOfWeek() {
            return timeUtil.getStartOfWeek(firstEventDate, prefs);
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<Label>(7);
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
            LogUtil.d(TAG, "Preferred weekly min: %s", date);
            return (double) date.toDate().getTime();
        }

        @Override
        public Double getPreferredMaxPosition() {
            LocalDate cal = getStartOfWeek();
            LocalDate max = cal.plusDays(7);
            LogUtil.d(TAG, "Preferred weekly max: %s", max);
            // Subtract one so that this week max < second week min
            return (double) max.toDate().getTime() - 1;
        }
    }

    private static class DateLabel implements Label {

        private LocalDate date;
        private DateFormat formatter;

        private DateLabel(LocalDate date, DateFormat formatter) {
            this.date = date;
            this.formatter = formatter;
        }

        @Override
        public double getPosition() {
            // Used timezone and time values do not matter as long as each date is processed similarly
            return date.toDate().getTime();
        }

        @Override
        public String getLabel() {
            return formatter.format(date);
        }
    }
}
