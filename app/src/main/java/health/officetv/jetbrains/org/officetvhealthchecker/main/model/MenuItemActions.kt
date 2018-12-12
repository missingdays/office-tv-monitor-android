package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.content.Context
import android.widget.BaseAdapter
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.DataInputView

abstract class MenuItemAction {
    abstract fun build(@IdRes id: Int): Action
}

class ViewHolderMenuItemAction(
    private val adapter: BaseAdapter,
    private val repository: ApiRepository
) : MenuItemAction() {

    override fun build(id: Int): Action {
        return when (id) {
            R.id.product_remove -> RemoveAction(adapter, repository)
            R.id.product_modify -> ModifyAction(adapter, repository)
            else -> throw IllegalArgumentException("wtf")
        }
    }
}

interface Action {
    fun perform(context: Context, data: Data): Boolean
}

class RemoveAction(
    private val adapter: BaseAdapter,
    private val repository: ApiRepository
) : Action {
    override fun perform(context: Context, data: Data): Boolean {
        repository.remove(data.name)
        adapter.notifyDataSetChanged()
        return true
    }
}

class ModifyAction(
    private val adapter: BaseAdapter,
    private val repository: ApiRepository
): Action {

    override fun perform(context: Context, data: Data): Boolean {
        val dataInputView = DataInputView(context, repository, adapter)
        val builder = dataInputView.builder

        dataInputView.urlTextView.setText(data.url)
        dataInputView.titleTextView.setText(data.name)

        builder.setPositiveButton(R.string.apply) { _, _ ->
            val title = dataInputView.titleTextView.text.toString()
            val url = dataInputView.urlTextView.text.toString()
            val newData = Data(title, url)
            repository.remove(data.name)
            repository.set(title, newData)
            adapter.notifyDataSetChanged()
        }

        builder.create().show()
        return true
    }
}


