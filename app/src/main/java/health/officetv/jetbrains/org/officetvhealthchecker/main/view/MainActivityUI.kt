package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import org.jetbrains.anko.*

class MainActivityUI(private val mainActivityViewModel: MainActivityViewModel) :
    AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        relativeLayout {
            listView {
                adapter = mainActivityViewModel.repositoryAdapter
            }.lparams { alignWithParent }

            val fab = FloatingActionButton(context).apply {
                rotation = 45f
                setImageResource(R.drawable.ic_cross)
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

    private fun onFABclick(fab: View) {
        AlertDialog.Builder(fab.context).apply {
            val view = context.verticalLayout {
                editText {
                    id = R.id.text_name
                    hintResource = R.string.name
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME
                }.lparams(width = matchParent, height = dip(50))
                editText {
                    id = R.id.text_url
                    hintResource = R.string.url
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_URI
                }.lparams(width = matchParent, height = dip(50))
            }
            setView(view)
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            setPositiveButton(R.string.add) { dialog, _ ->
                val name = view.findViewById<TextView>(R.id.text_name).text.toString()
                val url = view.findViewById<TextView>(R.id.text_url).text.toString()
                if (name.isBlank() || url.isBlank()) {
                    return@setPositiveButton
                }
                val data = Data(name, url)
                mainActivityViewModel.repository.set(data.name, data)
                mainActivityViewModel.repositoryAdapter.notifyDataSetChanged()
            }
        }.create().show()
    }
}