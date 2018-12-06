package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.ViewHolder


class RepositoryAdapter(private val repository: ApiRepository) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = with(parent.context) {
        val data = getItem(position)
        val view = convertView ?: ViewHolder(this, this@RepositoryAdapter, repository).view
        view.findViewById<TextView>(R.id.text_name).text = data.name
        view.findViewById<TextView>(R.id.text_url).text = data.url
        return@with view
    }

    override fun getItem(position: Int) = repository.getAll()[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = repository.size()

}