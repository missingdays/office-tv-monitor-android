package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.log.view.LogActivity
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ViewHolderMenuItemAction
import io.reactivex.subjects.BehaviorSubject
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

                        mainActivityViewModel.accessibilityController.observable = BehaviorSubject.create()
                        mainActivityViewModel.accessibilityController.resultObservable = BehaviorSubject.create()

                        mainActivityViewModel.accessibilityController.resultObservable.subscribe {
                            val reqCompCode = context.getString(R.string.request_completed_with_code)
                            val additional = if (it.responseMessage().isNotBlank()) {
                                "\n${it.responseMessage()}"
                            } else ""
                            val message = "$reqCompCode ${it.code()} $additional"
                            Snackbar.make(this, message, Snackbar.LENGTH_LONG).show()
                        }

                        setOnItemClickListener { _, _, position, _ ->
                            println(position)
                            val data = mainActivityViewModel.repository.getAll()[position]
                            startActivity<LogActivity>(
                                Data::class.java.simpleName to data
                            )
                        }

                        setOnItemLongClickListener { _, view, _, _ ->
                            val title = view.findViewById<TextView>(R.id.text_name).text.toString()
                            val menuItemAction = ViewHolderMenuItemAction()
                            PopupMenu(context, view).apply {
                                inflate(R.menu.product_menu)
                                setOnMenuItemClickListener {
                                    menuItemAction
                                        .buildFactory(it.itemId)
                                        .buildAction(mainActivityViewModel)
                                        .perform(context, mainActivityViewModel.repository.get(title))
                                }
                                show()
                            }
                            true
                        }
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