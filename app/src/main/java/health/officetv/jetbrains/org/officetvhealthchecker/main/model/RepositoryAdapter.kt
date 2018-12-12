package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.ViewHolder


class RepositoryAdapter(private val mainActivityViewModel: MainActivityViewModel) : BaseAdapter() {

    private val repository = mainActivityViewModel.repository
    private val accessibilityController = mainActivityViewModel.accessibilityController

    @SuppressLint("CheckResult")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data = getItem(position)
        val view = ViewHolder(mainActivityViewModel).build(parent.context)
        val titleView = view.findViewById<TextView>(R.id.text_name)
        val urlView = view.findViewById<TextView>(R.id.text_url)
        val progressView = view.findViewById<ProgressBar>(R.id.progress)
        val resultView = view.findViewById<ImageView>(R.id.result_icon)

        titleView.text = data.name
        urlView.text = data.url

        accessibilityController.observable.subscribe {
            if (it.position != position) return@subscribe
            Handler(Looper.getMainLooper()).post {
                when (it) {
                    is State.Ok -> stateIsOk(parent.context, resultView, progressView)
                    is State.Fail -> stateIsFail(parent.context, resultView, progressView)
                    is State.Refresh -> stateIsRefresh(parent.context, resultView, progressView)
                }
            }
        }
        accessibilityController.requestCheckWithLock(position)
        return view
    }

    private fun stateIsOk(context: Context, resultView: ImageView, progressView: ProgressBar) {
        val drawable = context.getDrawable(R.drawable.ic_check)
        resultView.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP)
        resultView.setImageDrawable(drawable)
        ProgressBarAnimator(progressView).animate()
        ResultViewAnimator(resultView).animate()
    }

    private fun stateIsFail(context: Context, resultView: ImageView, progressView: ProgressBar) {
        val drawable = context.getDrawable(R.drawable.ic_cross)
        resultView.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
        resultView.setImageDrawable(drawable)
        ProgressBarAnimator(progressView).animate()
        ResultViewAnimator(resultView).animate()
    }

    private fun stateIsRefresh(context: Context, resultView: ImageView, progressView: ProgressBar) {
        ProgressBarReverseAnimator(progressView).animate()
        ResultViewReverseAnimator(resultView).animate()
    }

    override fun getItem(position: Int) = repository.getAll()[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = repository.size()
}