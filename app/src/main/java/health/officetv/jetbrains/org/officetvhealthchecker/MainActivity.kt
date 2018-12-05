package health.officetv.jetbrains.org.officetvhealthchecker

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val vmFactory = MainActivityViewModel.Factory(getSharedPreferences("API", Context.MODE_PRIVATE))
        mainActivityViewModel = ViewModelProviders.of(this, vmFactory)[MainActivityViewModel::class.java]
        super.onCreate(savedInstanceState)

        MainActivityUI(mainActivityViewModel).setContentView(this)

    }
}

class MainActivityViewModel(@JvmField val repository: ApiRepository) : ViewModel() {
    val repositoryAdapter = RepositoryAdapter(repository)

    class Factory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass == MainActivityViewModel::class.java) {
                val repository = ApiRepository(sharedPreferences)
                MainActivityViewModel(repository) as T
            } else super.create(modelClass)
        }
    }
}

class MainActivityUI(private val mainActivityViewModel: MainActivityViewModel) : AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        relativeLayout {
            listView {
                adapter = mainActivityViewModel.repositoryAdapter
            }.lparams { alignWithParent }

            val fab = FloatingActionButton(context).apply {
                val drawable = context.getDrawable(android.R.drawable.ic_input_add)!!
                drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
                setImageResource(android.R.drawable.ic_input_add)
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

    private fun onFABclick(view: View) {
        AlertDialog.Builder(view.context).apply {
            val view = context.verticalLayout {
                editText {
                    id = R.id.text_name
                    hintResource = R.string.name
                }.lparams(width = matchParent, height = dip(40))
                editText {
                    id = R.id.text_url
                    hintResource = R.string.url
                }.lparams(width = matchParent, height = dip(40))
            }
            setView(view)
        }.create().show()
    }
}


interface Repository<K, V> {

    fun get(param: K): V
    fun set(param: K, value: V)
    fun getAll(): List<V>
    fun size() = getAll().size
}

data class Data(val name: String, val url: String) {
    companion object {
        fun fromSharedString(str: String): Data {
            val (name, url) = str.split(",")
            return Data(name, url)
        }
    }

    fun toSharedString(): String {
        return "$name,$url"
    }
}

class ApiRepository(private val sharedPreferences: SharedPreferences) : Repository<String, Data> {

    override fun set(param: String, value: Data) {
        sharedPreferences.edit().putString(param, value.toSharedString()).apply()
    }

    override fun get(param: String): Data {
        val res = sharedPreferences.getString(param, "") ?: return Data("not found", "not found")
        return Data.fromSharedString(res)
    }

    override fun getAll(): List<Data> {
        return sharedPreferences.all.values.map {
            Data.fromSharedString(it as String)
        }.sortedBy { it.name }
    }
}

class RepositoryAdapter(private val repository: ApiRepository) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = with(parent.context) {
        val data = getItem(position)
        val view = convertView ?: ViewHolder(this).view
        view.findViewById<TextView>(R.id.text_name).text = data.name
        view.findViewById<TextView>(R.id.text_url).text = data.url
        return@with view
    }

    override fun getItem(position: Int) = repository.getAll()[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = repository.size()

}

class ViewHolder(context: Context) {

    @JvmField
    val view = context.verticalLayout {
        textView {
            id = R.id.text_name
        }.lparams(width = matchParent, height = dip(20))

        textView {
            id = R.id.text_url
        }.lparams(width = matchParent, height = dip(20))
    }
}

