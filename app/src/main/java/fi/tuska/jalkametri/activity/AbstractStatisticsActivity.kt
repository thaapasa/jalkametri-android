package fi.tuska.jalkametri.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.GeneralStatistics
import fi.tuska.jalkametri.db.StatisticsDB
import fi.tuska.jalkametri.util.NumberUtil

abstract class AbstractStatisticsActivity<T : AbstractStatisticsActivity.ViewModel> : JalkametriDBActivity(R.string.title_statistics, R.string.help_statistics) {

    protected var generalStats: GeneralStatistics? = null
    protected var viewModel: T? = null
    protected lateinit var statisticsDB: StatisticsDB

    init {
        setShowDefaultHelpMenu(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.statisticsDB = StatisticsDB(adapter, prefs, this)
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        this.viewModel = createViewModel()
    }

    override fun setContentView(view: View) {
        super.setContentView(view)
        this.viewModel = createViewModel()
    }

    abstract fun createViewModel(): T

    open class ViewModel(val activity: JalkametriDBActivity) {

        protected val timeUtil = activity.timeUtil
        protected val prefs = activity.prefs
        protected val dateFormat = timeUtil.dateFormat
        protected val res = activity.resources

        // Drink amount stats
        private val totalDrinks = activity.findViewById(R.id.total_drinks) as TextView
        private val totalPortions = activity.findViewById(R.id.total_portions) as TextView
        private val totalAlcohol = activity.findViewById(R.id.total_alcohol) as TextView

        // Average portions per days
        private val avgPortionsAllDays = activity.findViewById(R.id.avg_portions_all_days) as TextView
        private val avgPortionsDrunkDays = activity.findViewById(R.id.avg_portions_drunk_days) as TextView
        private val avgWeeklyPortions = activity.findViewById(R.id.avg_weekly_portions) as TextView

        open fun updateUI(generalStats: GeneralStatistics) {
            totalDrinks.text = NumberUtil.toString(generalStats.totalDrinks.toDouble(), res)
            totalPortions.text = NumberUtil.toString(generalStats.totalPortions, res)

            val pureAlcoholFormatString = res.getString(R.string.stats_total_alcohol_format)
            totalAlcohol.text = String.format(pureAlcoholFormatString,
                    NumberUtil.toString(generalStats.getTotalPortionsAsPureAlcoholLiters(activity), res))

            avgPortionsAllDays.text = NumberUtil.toString(generalStats.avgPortionsAllDays, res)
            avgPortionsDrunkDays.text = NumberUtil.toString(generalStats.avgPortionsDrunkDays, res)
            avgWeeklyPortions.text = NumberUtil.toString(generalStats.avgWeeklyPortions, res)
        }

    }

    fun setStatistics(stats: GeneralStatistics) {
        this.generalStats = stats
        updateUI()
    }

    override fun updateUI() {
        generalStats?.let { viewModel?.updateUI(it) }
    }

    override fun onStart() {
        super.onStart()
        updateUI()
    }

    companion object {
        val TAG = StatisticsActivity.TAG
    }
}
