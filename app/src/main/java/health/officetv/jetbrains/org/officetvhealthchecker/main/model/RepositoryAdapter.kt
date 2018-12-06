package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.ViewHolder


class RepositoryAdapter(private val repository: ApiRepository) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View  {
        val data = getItem(position)
        return buildView(convertView, parent.context).apply {
            findViewById<TextView>(R.id.text_name).text = data.name
            findViewById<TextView>(R.id.text_url).text = data.url
        }
    }

    override fun getItem(position: Int) = repository.getAll()[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = repository.size()

    private fun buildView(convertView: View?, context: Context): View {
        return convertView ?: ViewHolder(context, this@RepositoryAdapter, repository).view
    }

}