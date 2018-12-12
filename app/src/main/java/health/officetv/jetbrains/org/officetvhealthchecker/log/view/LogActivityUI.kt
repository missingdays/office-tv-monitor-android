package health.officetv.jetbrains.org.officetvhealthchecker.log.view

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.log.LogActivityViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.support.v4.nestedScrollView
import org.jetbrains.anko.support.v4.onRefresh
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class LogActivityUI(
    private val logActivityViewModel: LogActivityViewModel
) : AnkoComponent<LogActivity> {

    override fun createView(ui: AnkoContext<LogActivity>) = with(ui) {
        swipeRefreshLayout {
            layoutParams = ViewGroup.LayoutParams(matchParent, matchParent)
            ui.owner.supportActionBar?.apply {
                title = logActivityViewModel.data.name
                subtitle = context.getString(R.string.logs)
            }

            val logRequest = logActivityViewModel.logRequest

            onRefresh {
                logActivityViewModel.uiController.onNext(LogActivityViewModel.State.Loading)
                logRequest.updateLogs()
            }

            relativeLayout {
                horizontalProgressBar {
                    isIndeterminate = true

                    logActivityViewModel.uiController.subscribe {
                        Handler(Looper.getMainLooper()).post {
                            visibility = when (it) {
                                is LogActivityViewModel.State.Loading -> {
                                    View.VISIBLE
                                }
                                is LogActivityViewModel.State.Loaded -> {
                                    View.GONE
                                }
                            }
                        }
                    }

                }.lparams(width = matchParent, height = dip(8)) {
                    alignParentTop()
                }

                nestedScrollView {
                    textView {
                        if (logRequest.hasValue) {
                            text = logRequest.value
                        } else {
                            logActivityViewModel.uiController.onNext(LogActivityViewModel.State.Loading)
                            logRequest.updateLogs()
                        }

                        logRequest.subscribe {
                            Handler(Looper.getMainLooper()).post {
                                logActivityViewModel.uiController.onNext(LogActivityViewModel.State.Loaded)
                                text = it
                                this@swipeRefreshLayout.isRefreshing = false
                            }
                        }
                    }.lparams(width = matchParent) {
                        setMargins(dip(16), dip(10), dip(16), 0)
                    }

                }.lparams(matchParent, matchParent) {
                    alignParentTop()
                }
            }
        }
    }
}