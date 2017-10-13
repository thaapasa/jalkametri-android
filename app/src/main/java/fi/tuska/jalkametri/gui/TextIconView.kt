package fi.tuska.jalkametri.gui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity.CENTER_HORIZONTAL
import android.view.Gravity.CENTER_VERTICAL
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import fi.tuska.jalkametri.R

class TextIconView : LinearLayout {

    private lateinit var textView: TextView
    private lateinit var iconView: ImageView

    var text: String
        get() = textView.text.toString()
        set(text) {
            textView.text = text
        }

    constructor(context: Context, attrs: AttributeSet, vertical: Boolean) : super(context, attrs) {
        initView(vertical)
    }

    constructor(context: Context, vertical: Boolean) : super(context) {
        initView(vertical)
    }

    private fun initView(vertical: Boolean) {
        val li = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        gravity = CENTER_HORIZONTAL or CENTER_VERTICAL
        li.inflate(layout(vertical), this, true)

        textView = findViewById(R.id.text) as TextView
        iconView = findViewById(R.id.icon) as ImageView
    }

    fun layout(vertical: Boolean) = if (vertical) R.layout.text_icon_vertical else R.layout.text_icon_horizontal

    fun setImageResource(resID: Int) {
        iconView.setImageResource(resID)
    }

    override fun toString(): String = text

}
