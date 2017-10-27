package fi.tuska.jalkametri.activity

import android.os.Bundle
import android.widget.TextView
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.GeneralStatistics
import fi.tuska.jalkametri.util.NumberUtil

class StatisticsSummaryActivity : AbstractStatisticsActivity<StatisticsSummaryActivity.ViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatistics(statisticsDB.generalStatistics)
        setContentView(R.layout.activity_statistics_summary)
    }

    override fun createViewModel() = ViewModel(this)

    class ViewModel(activity: StatisticsSummaryActivity) : AbstractStatisticsActivity.ViewModel(activity) {
        private val firstDay = activity.findViewById(R.id.first_day) as TextView

        override fun updateUI(generalStats: GeneralStatistics) {
            super.updateUI(generalStats)
            generalStats.firstDay.let { date ->
                firstDay.text = if (date != null) dateFormat.print(date) else res.getString(R.string.stats_no_days)
            }
        }
    }

    companion object {
        val TAG = "StatisticsSummaryActivity"
    }

}
