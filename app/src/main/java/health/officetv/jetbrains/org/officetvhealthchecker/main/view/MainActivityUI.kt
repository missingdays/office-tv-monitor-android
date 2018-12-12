package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import org.jetbrains.anko.*

class MainActivityUI(private val mainActivityViewModel: MainActivityViewModel) :
    AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        frameLayout {
            val swipeRefreshLayout = SwipeRefreshLayout(context).apply {

                setOnRefreshListener {
                    (0 until mainActivityViewModel.repository.getAll().size).forEach {
                        mainActivityViewModel.accessibilityController.requestCheck(it)
                    }
                    isRefreshing = false
                }
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
            addView(swipeRefreshLayout)
        }
    }

    private fun onFABclick(fab: View) {
        DataInputView(fab.context).apply {
            builder.apply {
                setNegativeButton()
                setPositiveButton(titleTextView, urlTextView)
            }.create().show()
        }
    }

    private fun AlertDialog.Builder.setNegativeButton() {
        setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }
    }

    private fun AlertDialog.Builder.setPositiveButton(
        titleTextView: TextView,
        urlTextView: TextView
    ) {
        setPositiveButton(R.string.add) { _, _ ->
            val name = titleTextView.text.toString()
            val url = urlTextView.text.toString()
            if (name.isBlank() || url.isBlank()) return@setPositiveButton
            val data = Data(name, url)
            mainActivityViewModel.repository.set(data.name, data)
            mainActivityViewModel.repositoryAdapter.notifyDataSetChanged()
        }
    }
}