package fi.tuska.jalkametri.gui

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import fi.tuska.jalkametri.R

class TextIconView(context: Context, vertical: Boolean, gravity: Int) : LinearLayout(context) {

    private lateinit var textView: TextView
    private lateinit var iconView: ImageView

    init {
        this.gravity = gravity
    }

    var text: String
        get() = textView.text.toString()
        set(text) {
            textView.text = text
        }

    private fun initView(vertical: Boolean) {
        val li = context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        li.inflate(layout(vertical), this, true)

        textView = findViewById(R.id.text) as TextView
        iconView = findViewById(R.id.icon) as ImageView
    }

    fun layout(vertical: Boolean) = if (vertical) R.layout.text_icon_vertical else R.layout.text_icon_horizontal

    fun setImageResource(resID: Int) {
        iconView.setImageResource(resID)
    }

    override fun toString(): String = text

    init {
        initView(vertical)
    }

}
