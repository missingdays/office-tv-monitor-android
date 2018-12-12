package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.view.View
import android.widget.RelativeLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import org.jetbrains.anko.*

class MainActivityUI(private val mainActivityViewModel: MainActivityViewModel) :
    AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        frameLayout {
            val swipeRefreshLayout = SwipeRefreshLayout(context).apply {

                setOnRefreshListener {
                    (0 until mainActivityViewModel.repository.getAll().size).forEach {
                        mainActivityViewModel.accessibilityController.requestCheckWithLock(it)
                    }
                    isRefreshing = false
                }
                relativeLayout {
                    listView {
                        adapter = mainActivityViewModel.repositoryAdapter
                    }.lparams(matchParent, matchParent) {
                        alignWithParent
                    }

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
        DataInputView(fab.context, mainActivityViewModel).builder.create().show()
    }
}