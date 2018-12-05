package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.content.Context
import org.jetbrains.anko.dip
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.textView
import org.jetbrains.anko.verticalLayout
import health.officetv.jetbrains.org.officetvhealthchecker.R

class ViewHolder(context: Context) {

    @JvmField
    val view = context.verticalLayout {
        textView {
            id = R.id.text_name
        }.lparams(width = matchParent, height = dip(20))

        textView {
            id = R.id.text_url
        }.lparams(width = matchParent, height = dip(20))
    }
}