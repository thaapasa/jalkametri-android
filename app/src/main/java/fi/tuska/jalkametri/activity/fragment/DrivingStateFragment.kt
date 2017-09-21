package fi.tuska.jalkametri.activity.fragment

import android.app.Fragment
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.DrinkStatus.DrivingState

class DrivingStateFragment : Fragment() {

    private var carStatusView: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.driving_state, container, false).apply {
            carStatusView = findViewById(R.id.status_car) as ImageView
        }
    }

    fun setDrivingState(state: DrivingState) {
        fun set(@DrawableRes img: Int, @ColorInt tint: Int) = carStatusView?.apply {
            setImageResource(img)
            drawable.setTint(resources.getColor(tint))
        }
        when (state) {
            DrivingState.DrivingOK -> set(R.drawable.ic_check_black_24dp, R.color.status_ok)
            DrivingState.DrivingMaybe -> set(R.drawable.ic_warning_black_24dp, R.color.status_maybe)
            DrivingState.DrivingNo -> set(R.drawable.ic_not_interested_black_24dp, R.color.status_no)
        }
    }
}
