package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.*
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel


class MainActivityUI(private val mainActivityViewModel: MainActivityViewModel) :
    AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        relativeLayout {
            listView {
                adapter = mainActivityViewModel.repositoryAdapter
            }.lparams { alignWithParent }

            val fab = FloatingActionButton(context).apply {
                val drawable = context.getDrawable(android.R.drawable.ic_input_add)!!
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                setImageResource(android.R.drawable.ic_input_add)
                setOnClickListener {
                    onFABclick(it)
                }
            }.lparams {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                addRule(RelativeLayout.ALIGN_PARENT_END)
                setMargins(0, 0, dip(16), dip(16))
            }
            addView(fab)
        }
    }

    private fun onFABclick(view: View) {
        AlertDialog.Builder(view.context).apply {
            val view = context.verticalLayout {
                editText {
                    id = R.id.text_name
                    hintResource = R.string.name
                }.lparams(width = matchParent, height = dip(40))
                editText {
                    id = R.id.text_url
                    hintResource = R.string.url
                }.lparams(width = matchParent, height = dip(40))
            }
            setView(view)
        }.create().show()
    }
}