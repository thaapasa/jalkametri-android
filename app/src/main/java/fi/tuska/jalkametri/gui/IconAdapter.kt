package fi.tuska.jalkametri.gui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.dao.NamedIcon
import fi.tuska.jalkametri.gui.DrinkIconUtils.getDrinkIconRes

class IconAdapter<out T : NamedIcon>(c: Context, icons: List<T>, defaultIconRes: Int) :
        NamedIconAdapter<T>(c, icons, defaultIconRes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: LinearLayout = convertView as LinearLayout? ?: (inflater().inflate(R.layout.icon_area, null) as LinearLayout).apply {
            val size = resources.getDimension(R.dimen.icon_area_size).toInt()
            layoutParams = ViewGroup.LayoutParams(size, size)
        }
        val imageView = view.findViewById(R.id.icon) as ImageView
        val icon = getItem(position)
        val res = getDrinkIconRes(icon.icon)

        imageView.setImageResource(if (res != 0) res else defaultIconRes)
        return view
    }

}
