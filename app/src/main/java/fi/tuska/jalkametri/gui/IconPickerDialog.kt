package fi.tuska.jalkametri.gui

import android.app.DialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.GridView
import fi.tuska.jalkametri.Common
import fi.tuska.jalkametri.Common.DEFAULT_ICON_RES
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.data.IconName
import java.io.Serializable

class IconPickerDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.select_icon, container, false)

        val callback = readArgs(arguments)
        fun selectIcon(icon: IconName) {
            callback(icon)
            this.dismiss()
        }

        val adapter = IconAdapter(v.context, DrinkIconUtils.getAsList(), DEFAULT_ICON_RES)
        val list = v.findViewById(R.id.list) as GridView
        list.adapter = adapter
        list.onItemClickListener = OnItemClickListener { _, _, position, _ -> selectIcon(adapter.getItem(position)) }

        return v
    }

    companion object {
        fun createDialog(callback: (IconName) -> Unit): IconPickerDialog = IconPickerDialog().apply {
            arguments = writeArgs(Bundle(), callback)
        }

        fun writeArgs(bundle: Bundle, callback: (IconName) -> Unit) = bundle.apply {
            putSerializable("callback", callback as Serializable)
        }

        @Suppress("UNCHECKED_CAST")
        fun readArgs(bundle: Bundle): (IconName) -> Unit =
                bundle.getSerializable("callback") as (IconName) -> Unit
    }

}
