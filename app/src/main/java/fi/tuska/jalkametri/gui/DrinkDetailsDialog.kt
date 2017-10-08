package fi.tuska.jalkametri.gui

import android.app.DialogFragment
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import fi.tuska.jalkametri.R
import fi.tuska.jalkametri.data.DrinkSelection
import fi.tuska.jalkametri.util.StringUtil
import fi.tuska.jalkametri.util.TimeUtil
import org.joda.time.format.DateTimeFormatter
import java.util.Locale

class DrinkDetailsDialog : DialogFragment() {

    private lateinit var model: ViewModel

    /*
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    */

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val (selection, showTime) = readArgs(arguments)

        val v = inflater.inflate(R.layout.activity_show_event, container, false)
        model = ViewModel(selection, showTime, resources, v)

        val okButton = v.findViewById(R.id.ok) as Button
        okButton.setOnClickListener { dismiss() }
        return v
    }

    class ViewModel(val selection: DrinkSelection, val showTime: Boolean, val res: Resources, val view: View) {

        private val timeUtil = TimeUtil(res)
        private val dateFormat: DateTimeFormatter = timeUtil.dateFormatFull
        private val locale: Locale = timeUtil.locale

        init {

            val drink = selection.drink
            // Drink name
            setDialogText(R.id.name, drink.name)
            // Drink strength
            setDialogText(R.id.strength,
                    String.format(locale, "%.1f %s", drink.strength, res.getString(R.string.unit_percent)))

            val size = selection.size
            // Drink size name
            setDialogText(R.id.size_name, size.name)
            // Drink size (volume, in liters)
            setDialogText(R.id.size, size.getFormattedSize(res))

            // Portions
            setDialogText(
                    R.id.portions,
                    String.format(locale, "%.1f %s", selection.getPortions(view.context),
                            res.getString(R.string.unit_portions)))

            // Date
            run {
                val drinkTime = view.findViewById(R.id.date) as TextView
                if (showTime) {
                    drinkTime.visibility = View.VISIBLE
                    setDialogText(R.id.date,
                            StringUtil.uppercaseFirstLetter(dateFormat.print(selection.time)))
                } else {
                    drinkTime.visibility = View.GONE
                }
            }

            // Icon
            run {
                val icon = view.findViewById(R.id.icon) as ImageView
                val resID = DrinkIconUtils.getDrinkIconRes(selection.icon)
                icon.setImageResource(if (resID != 0) resID else R.mipmap.ic_launcher)
            }

            // Comment
            run {
                val commentText = view.findViewById(R.id.comment) as TextView
                var comment: String? = drink.comment
                if ("" == comment)
                    comment = null
                if (comment != null) {
                    commentText.text = comment
                }
                commentText.visibility = if (comment != null) View.VISIBLE else View.GONE
            }
        }

        private fun setDialogText(resID: Int, text: String) {
            val t = view.findViewById(resID) as TextView?
            if (t != null) {
                t.text = text
            }
        }
    }

    companion object {
        fun createDialog(selection: DrinkSelection, showTime: Boolean) = DrinkDetailsDialog().apply {
            arguments = writeArgs(Bundle(), selection, showTime)
        }

        fun writeArgs(bundle: Bundle, selection: DrinkSelection, showTime: Boolean) = bundle.apply {
            putSerializable("selection", selection)
            putBoolean("showTime", showTime)
        }

        fun readArgs(bundle: Bundle): Pair<DrinkSelection, Boolean> =
                Pair(bundle.getSerializable("selection") as DrinkSelection,
                        bundle.getBoolean("showTime"))
    }


}
