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

    private var timeUtil: TimeUtil? = null
    private var sobrietyText: TextView? = null
    private var portionsText: TextView? = null
    private var drinkDateText: TextView? = null
    private var alcoholLevel: AlcoholLevelView? = null
    private var carStatusView: ImageView? = null

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
        fun set(@DrawableRes img: Int, @ColorInt tint: Int) = carStatusView?.apply {
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
        alcoholLevel?.setLevel(level, drivingState)
        setDrivingState(drivingState)
    }

    val level: Double
        get() = alcoholLevel?.level ?: 0.0

    val drivingState: DrinkStatus.DrivingState
        get() = alcoholLevel?.drivingState ?: DrinkStatus.DrivingState.DrivingOK

    fun updateSobriety(status: DrinkStatus) {
        val res = activity.resources
        val level = status.alcoholLevel
        if (level <= 0) {
            showSobrietyText(res.getString(R.string.sober))
        } else {
            val hours = status.hoursToSober
            val minutes = (hours - hours.toInt()) * 60
            val sobriety = timeUtil!!.getTimeAfterHours(hours)
            if (hours > 1) {
                showSobrietyText(String.format("%d%s %d%s (%s)", hours.toInt(), res.getString(R.string.hour),
                        minutes.toInt(), res.getString(R.string.minute), timeUtil!!.timeFormat.print(sobriety)))
            } else {
                showSobrietyText(String.format("%d %s (%s)", minutes.toInt(), res.getString(R.string.minute),
                        timeUtil!!.timeFormat.print(sobriety)))
            }
        }
    }

    fun showSobrietyText(text: String) = sobrietyText?.let { it.text = text }
    fun showPortions(text: String) = portionsText?.let { it.text = text }
    fun showDrinkDate(text: String) = drinkDateText?.let { it.text = text }
    fun startAnimation(a: Animation) = alcoholLevel?.startAnimation(a)

}