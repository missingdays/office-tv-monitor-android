package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.ViewHolder
import io.reactivex.subjects.BehaviorSubject
import org.jetbrains.anko.backgroundColor
import android.widget.*


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
            val drawable = if (it) {
                parent.context.getDrawable(R.drawable.ic_check)!!.apply {
                    setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
                }
            } else {
                parent.context.getDrawable(R.drawable.ic_cross)!!.apply {
                    setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP)
                }
            }

            Handler(Looper.getMainLooper()).post {
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