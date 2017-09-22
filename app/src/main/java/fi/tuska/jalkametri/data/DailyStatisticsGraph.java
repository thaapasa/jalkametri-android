package fi.tuska.jalkametri.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

public abstract class DailyStatisticsGraph implements Graph {

    private static final long serialVersionUID = 7189644235567515657L;

    private static final String TAG = "DailyStatisticsGraph";

    private final List<DailyDrinkStatistics> points;
    protected final Date firstEventTime;
    protected final Preferences prefs;
    private final ColorSlider slider;
    protected final TimeUtil timeUtil;

    private DailyStatisticsGraph(List<DailyDrinkStatistics> points, Preferences prefs,
        Context context) {
        this.points = points;
        this.prefs = prefs;
        this.timeUtil = new TimeUtil(context);
        firstEventTime = (points.size() != 0) ? points.get(0).getDay() : null;

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

        private static final long serialVersionUID = 2018952185796943321L;
        private final int year;
        private final DateFormat formatter = new SimpleDateFormat("M");

        private YearlyGraph(List<DailyDrinkStatistics> points, Preferences prefs, Context context) {
            super(points, prefs, context);
            if (firstEventTime != null) {
                Calendar cal = timeUtil.getCalendar(firstEventTime);
                year = cal.get(Calendar.YEAR);
                LogUtil.d(TAG, "Yearly cal first event is %s; year is %d", firstEventTime, year);
            } else {
                year = 2000;
            }
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<Label>(12);
            for (int m = 1; m <= 12; ++m)
                l.add(new DateLabel(timeUtil.getCalendar(year, m, 1, 12, 0, 0).getTime(),
                    formatter));
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
            if (firstEventTime != null) {
                Calendar cal = timeUtil.getCalendar(firstEventTime);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH) + 1;
            }
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<Label>(12);
            int days = timeUtil.getDaysInMonth(month, year);

            for (int d = 1; d <= days; d += 3)
                l.add(new DateLabel(timeUtil.getCalendar(year, month, d, 12, 0, 0).getTime(),
                    formatter));
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

        private Calendar getStartOfWeek() {
            Calendar cal = timeUtil.getCalendar(firstEventTime);
            cal = timeUtil.getStartOfWeek(cal.getTime(), prefs);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }

        @Override
        public List<Label> getPositionLabels() {
            List<Label> l = new ArrayList<Label>(7);

            Calendar cal = getStartOfWeek();
            cal.set(Calendar.HOUR_OF_DAY, 12);
            for (int d = 1; d <= 7; ++d) {
                l.add(new DateLabel(cal.getTime(), formatter));
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
            return l;
        }

        @Override
        public Double getPreferredMinPosition() {
            Date date = getStartOfWeek().getTime();
            LogUtil.d(TAG, "Preferred weekly min: %s", date);
            return (double) date.getTime();
        }

        @Override
        public Double getPreferredMaxPosition() {
            Calendar cal = getStartOfWeek();
            cal.add(Calendar.DAY_OF_WEEK, 7);
            cal.add(Calendar.MILLISECOND, -1);
            Date date = cal.getTime();
            LogUtil.d(TAG, "Preferred weekly max: %s", date);
            return (double) date.getTime();
        }
    }

    private static class DateLabel implements Label {

        private static final long serialVersionUID = 8324952185886988979L;
        private Date date;
        private DateFormat formatter;

        private DateLabel(Date date, DateFormat formatter) {
            this.date = date;
            this.formatter = formatter;
        }

        @Override
        public double getPosition() {
            return date.getTime();
        }

        @Override
        public String getLabel() {
            return formatter.format(date);
        }
    }
}
