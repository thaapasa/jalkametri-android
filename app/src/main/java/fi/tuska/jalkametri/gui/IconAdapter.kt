package fi.tuska.jalkametri.gui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import fi.tuska.jalkametri.dao.NamedIcon
import fi.tuska.jalkametri.gui.DrinkIconUtils.getDrinkIconRes

class IconAdapter<out T : NamedIcon>(c: Context, icons: List<T>, defaultIconRes: Int) :
        NamedIconAdapter<T>(c, icons, defaultIconRes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: ImageView = convertView as ImageView? ?: ImageView(context)
        val icon = getItem(position)
        val res = getDrinkIconRes(icon.icon)

        view.setImageResource(if (res != 0) res else defaultIconRes)
        return view
    }

}
