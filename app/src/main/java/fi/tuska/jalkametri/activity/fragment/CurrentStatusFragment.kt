package fi.tuska.jalkametri.activity.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.DrinkStatus
import fi.tuska.jalkametri.gui.AlcoholLevelView
import fi.tuska.jalkametri.util.TimeUtil

class CurrentStatusFragment : Fragment() {

    private lateinit var timeUtil: TimeUtil
    private lateinit var sobrietyText: TextView
    private lateinit var portionsText: TextView
    private lateinit var drinkDateText: TextView
    private lateinit var alcoholLevel: AlcoholLevelView
    private lateinit var carStatusView: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        timeUtil = TimeUtil(resources)
        return inflater.inflate(R.layout.current_status_fragment, container, false).apply {
            sobrietyText = findViewById(R.id.sober_text) as TextView
            portionsText = findViewById(R.id.portions_text) as TextView
            drinkDateText = findViewById(R.id.drink_date_text) as TextView
            alcoholLevel = findViewById(R.id.status_image) as AlcoholLevelView
            carStatusView = findViewById(R.id.status_car) as ImageView
        }
    }

    fun setDrivingState(state: DrinkStatus.DrivingState) {
        fun set(@DrawableRes img: Int, @ColorInt tint: Int) = carStatusView.apply {
            setImageResource(img)
            drawable.setTint(resources.getColor(tint))
        }
        when (state) {
            DrinkStatus.DrivingState.DrivingOK -> set(R.drawable.ic_check_mark, R.color.status_ok)
            DrinkStatus.DrivingState.DrivingMaybe -> set(R.drawable.ic_warning, R.color.status_maybe)
            DrinkStatus.DrivingState.DrivingNo -> set(R.drawable.ic_denied, R.color.status_no)
        }
    }

    fun setAlcoholLevel(level: Double, drivingState: DrinkStatus.DrivingState) {
        alcoholLevel.setLevel(level, drivingState)
        setDrivingState(drivingState)
    }

    val level: Double
        get() = alcoholLevel.level

    val drivingState: DrinkStatus.DrivingState
        get() = alcoholLevel.drivingState ?: DrinkStatus.DrivingState.DrivingOK

    fun updateSobriety(status: DrinkStatus) {
        val res = activity.resources
        val level = status.alcoholLevel
        if (level <= 0) {
            showSobrietyText(res.getString(R.string.sober))
        } else {
            val hours = status.hoursToSober
            val minutes = (hours - hours.toInt()) * 60
            val sobriety = timeUtil.getTimeAfterHours(hours)
            if (hours > 1) {
                showSobrietyText(String.format("%d%s %d%s (%s)", hours.toInt(), res.getString(R.string.hour),
                        minutes.toInt(), res.getString(R.string.minute), timeUtil.timeFormat.print(sobriety)))
            } else {
                showSobrietyText(String.format("%d %s (%s)", minutes.toInt(), res.getString(R.string.minute),
                        timeUtil.timeFormat.print(sobriety)))
            }
        }
    }

    fun showSobrietyText(text: String) {
        sobrietyText.text = text
    }

    fun showPortions(text: String) {
        portionsText.text = text
    }

    fun showDrinkDate(text: String) {
        drinkDateText.text = text
    }

    fun startAnimation(a: Animation) {
        alcoholLevel.startAnimation(a)
    }

}