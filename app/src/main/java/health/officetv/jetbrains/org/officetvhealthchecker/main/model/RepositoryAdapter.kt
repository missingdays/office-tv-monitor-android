package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PostProcessor
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.ViewHolder
import io.reactivex.subjects.BehaviorSubject
import org.jetbrains.anko.backgroundColor
import android.widget.*
import androidx.core.content.ContextCompat


class RepositoryAdapter(
    private val repository: ApiRepository,
    private val accessibilityController: AccessibilityController
) : BaseAdapter() {

    @SuppressLint("CheckResult")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val data = getItem(position)
        val view = ViewHolder(this, repository).build(parent.context)
        val titleView = view.findViewById<TextView>(R.id.text_name)
        val urlView = view.findViewById<TextView>(R.id.text_url)
        val progressView = view.findViewById<ProgressBar>(R.id.progress)
        val resultView = view.findViewById<ImageView>(R.id.result_icon)

        titleView.text = data.name
        urlView.text = data.url

        var observer = accessibilityController.subjects[position]
        if (observer == null) {
            observer = BehaviorSubject.create()
            accessibilityController.subjects[position] = observer
        }
        accessibilityController.requestCheck(position)

        observer.subscribe {

            Handler(Looper.getMainLooper()).post {
                val drawable = if (it) {
                    resultView.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_ATOP)
                    ContextCompat.getDrawable(parent.context, R.drawable.ic_check)
                } else {
                    resultView.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
                    ContextCompat.getDrawable(parent.context, R.drawable.ic_cross)
                }
                resultView.setImageDrawable(drawable)
                ProgressBarAnimator(progressView).animate()
                ResultViewAnimator(resultView).animate()
            }

        }

        return view
    }


    override fun getItem(position: Int) = repository.getAll()[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = repository.size()

    override fun notifyDataSetChanged() {
        accessibilityController.notifyDataSetChanged()
        super.notifyDataSetChanged()
    }
}