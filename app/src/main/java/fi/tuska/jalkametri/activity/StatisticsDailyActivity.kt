package fi.tuska.jalkametri.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.activity.StatisticsDailyActivity.Type.Monthly
import fi.tuska.jalkametri.activity.StatisticsDailyActivity.Type.Weekly
import fi.tuska.jalkametri.activity.StatisticsDailyActivity.Type.Yearly
import fi.tuska.jalkametri.dao.GeneralStatistics
import fi.tuska.jalkametri.data.DailyStatisticsGraph
import fi.tuska.jalkametri.gui.GraphView
import fi.tuska.jalkametri.util.LogUtil
import fi.tuska.jalkametri.util.StringUtil
import fi.tuska.jalkametri.util.TimeUtil
import org.joda.time.LocalDate

class StatisticsDailyActivity : AbstractStatisticsActivity<StatisticsDailyActivity.ViewModel>() {

    private lateinit var type: Type

    enum class Type {
        Yearly, Monthly, Weekly
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        type = intent.getSerializableExtra(TYPE) as Type

        setContentView(R.layout.activity_statistics_daily)
        viewModel?.loadDay(LocalDate(timeUtil.timeZone))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        (savedInstanceState.getSerializable(DAY_STORE_KEY) as LocalDate?)?.let { viewModel?.loadDay(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(DAY_STORE_KEY, viewModel?.day)
    }

    fun showGraph(v: View) {
        viewModel?.showGraph()
    }

    fun onPreviousClick(v: View) {
        viewModel?.gotoNextOrPrevious(-1)
    }

    fun onNextClick(v: View) {
        viewModel?.gotoNextOrPrevious(1)
    }

    fun onTodayClick(v: View) {
        viewModel?.loadDay(timeUtil.getCurrentDrinkingDate(prefs))
    }

    fun onSelectDayClick(v: View) {
        // Select day to show
        LogUtil.d(TAG, "Showing date selection dialog")
        // Show a date picker dialog
        viewModel?.let { vm ->
            timeUtil.pickDate(this, vm.day) { vm.loadDay(it) }
        }
    }

    override fun createViewModel() = ViewModel(this, type)

    class ViewModel(val statisticsActivity: StatisticsDailyActivity, val type: Type) : AbstractStatisticsActivity.ViewModel(statisticsActivity) {
        private val graphView = activity.findViewById(R.id.graph) as GraphView
        private val dateTitle = activity.findViewById(R.id.browser_title) as TextView
        private val dateTitleFormat = timeUtil.timeFormatter(getDateTitlePattern(activity, type))

        lateinit var day: LocalDate
        lateinit var start: LocalDate
        lateinit var end: LocalDate

        init {
            (activity.findViewById(R.id.browser_subtitle) as TextView).visibility = View.GONE
        }

        override fun updateUI(generalStats: GeneralStatistics) {
            super.updateUI(generalStats)
            updateThisUI()
        }

        private fun updateThisUI() {
            dateTitle.text = StringUtil.uppercaseFirstLetter(dateTitleFormat.print(day))
        }

        fun showGraph() = GraphActivity.showGraph(activity, start, end, type)

        fun loadDay(date: LocalDate) {
            calculateDates(date)

            LogUtil.d(TAG, "Loading daily statisticsDB between %s and %s", start, end)
            statisticsActivity.setStatistics(statisticsActivity.statisticsDB.getGeneralStatistics(start, end))
            val dailyStats = statisticsActivity.statisticsDB.getDailyDrinkAmounts(start, end)

            graphView.clear()
            graphView.addGraph(DailyStatisticsGraph.getGraph(type, dailyStats, prefs, activity))

            updateThisUI()
        }

        fun gotoNextOrPrevious(multiplier: Int) {
            // Go to next year/month/week
            when (type) {
                Weekly -> day.plusDays(7 * multiplier)
                Monthly -> day.withDayOfMonth(1).plusMonths(multiplier)
                Yearly -> day.withDayOfMonth(1).withMonthOfYear(1).plusYears(multiplier)
            }.let { loadDay(it) }
        }

        private fun calculateDates(date: LocalDate) {
            this.day = date

            start = when (type) {
                Yearly -> day.withDayOfMonth(1).withMonthOfYear(1)
                Monthly -> day.withDayOfMonth(1)
                Weekly -> timeUtil.getStartOfWeek(day, prefs)
            }

            end = when (type) {
                Yearly -> start.plusYears(1)
                Monthly -> start.plusMonths(1)
                Weekly -> start.plusDays(7)
            }.minusDays(1)
        }
    }

    companion object {

        val TAG = "StatisticsDailyActivity"

        val DAY_STORE_KEY = "daily_statistics_day"
        val TYPE = "daily_statistics_type"

        fun getTitle(context: Context, type: Type, date: LocalDate): String {
            val f = TimeUtil(context).timeFormatter(getDateTitlePattern(
                    context, type))
            return StringUtil.uppercaseFirstLetter(f.print(date))
        }

        protected fun getDateTitlePattern(context: Context, type: Type?): String {
            val res = context.resources
            when (type) {
                Yearly -> return res.getString(R.string.stats_yearly_title_pattern)
                Monthly -> return res.getString(R.string.stats_monthly_title_pattern)
                Weekly -> return res.getString(R.string.stats_weekly_title_pattern)
            }
            LogUtil.w(TAG, "Unknown type: %s", type!!)
            return "???"
        }
    }

}
