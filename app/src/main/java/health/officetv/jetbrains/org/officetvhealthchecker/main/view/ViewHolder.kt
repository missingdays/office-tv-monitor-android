package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.BaseAdapter
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.IdRes
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ApiRepository
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import org.jetbrains.anko.*

class ViewHolder(
    context: Context,
    private val adapter: BaseAdapter,
    private val repository: ApiRepository
) {

    @JvmField
    val view = context.relativeLayout {
        setPadding(dip(8), dip(4), dip(8), dip(4))
        isLongClickable = true
        lparams(width = matchParent)
        createTitleView()
        createUrlView()
        setOnLongClickListener {
            onLongClick(it, context)
        }
    }

    private fun _RelativeLayout.createTitleView() {
        textView {
            id = R.id.text_name
            textSize = 16f
            textColor = Color.BLACK
            singleLine = true
        }.lparams(width = matchParent, height = dip(30)) {
            alignParentTop()
        }
    }

    private fun _RelativeLayout.createUrlView() {
        textView {
            id = R.id.text_url
            setPadding(dip(8), 0, 0, 0)
            singleLine = true
        }.lparams(width = matchParent, height = dip(20)) {
            below(R.id.text_name)
        }
    }

    private fun onLongClick(view: View, context: Context): Boolean {
        val title = view.findViewById<TextView>(R.id.text_name).text.toString()
        val menuItemAction = ViewHolderMenuItemAction(adapter, repository)
        PopupMenu(context, view).apply {
            inflate(R.menu.product_menu)
            setOnMenuItemClickListener {
                menuItemAction.build(it.itemId).perform(repository.get(title))
            }
            show()
        }
        return true
    }

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
}