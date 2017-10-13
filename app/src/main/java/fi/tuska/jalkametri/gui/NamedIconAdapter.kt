package fi.tuska.jalkametri.gui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import fi.tuska.jalkametri.dao.NamedIcon
import fi.tuska.jalkametri.gui.DrinkIconUtils.getDrinkIconRes
import fi.tuska.jalkametri.util.Converter

open class NamedIconAdapter<T : NamedIcon>(
        protected val context: Context,
        private val icons: List<T>,
        private val vertical: Boolean,
        private val textFormatter: Converter<NamedIcon, String>?,
        protected val defaultIconRes: Int) : BaseAdapter() {

    constructor(c: Context, icons: List<T>, vertical: Boolean, defaultIconRes: Int) : this(c, icons, vertical, null, defaultIconRes)

    override fun getCount(): Int = icons.size

    override fun getItem(position: Int): T = icons[position]

    override fun getItemId(position: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView as TextIconView? ?: TextIconView(context, vertical)
        val icon = getItem(position)

        val res = getDrinkIconRes(icon.icon)
        view.setImageResource(if (res != 0) res else defaultIconRes)

        view.text = textFormatter?.convert(icon) ?: icon.getIconText(context.resources)
        return view
    }

}
