package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.widget.BaseAdapter
import androidx.annotation.IdRes
import health.officetv.jetbrains.org.officetvhealthchecker.R

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
            else -> throw IllegalArgumentException("wtf")
        }
    }
}

interface Action {
    fun perform(data: Data): Boolean
}

class RemoveAction(
    private val adapter: BaseAdapter,
    private val repository: ApiRepository
) : Action {
    override fun perform(data: Data): Boolean {
        repository.remove(data.name)
        adapter.notifyDataSetChanged()
        return true
    }
}