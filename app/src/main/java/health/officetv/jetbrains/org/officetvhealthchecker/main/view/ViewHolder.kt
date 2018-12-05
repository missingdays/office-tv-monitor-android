package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.content.Context
import android.graphics.Color
import health.officetv.jetbrains.org.officetvhealthchecker.R
import org.jetbrains.anko.*

class ViewHolder(context: Context) {

    @JvmField
    val view = context.relativeLayout {
        setPadding(dip(8), dip(4), dip(8), dip(4))
        lparams(width = matchParent)
        textView {
            id = R.id.text_name
            textSize = 16f
            textColor = Color.BLACK
        }.lparams(width = matchParent, height = dip(30)) {
            alignParentTop()
        }

        textView {
            id = R.id.text_url
            setPadding(dip(8), 0, 0, 0)
            singleLine = true
        }.lparams(width = matchParent, height = dip(20)) {
            below(R.id.text_name)
        }
    }
}