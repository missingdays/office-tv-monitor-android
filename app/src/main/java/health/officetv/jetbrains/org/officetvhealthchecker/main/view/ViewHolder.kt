package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ViewHolderMenuItemAction
import org.jetbrains.anko.*

class ViewHolder(private val mainActivityViewModel: MainActivityViewModel) {

    fun build(context: Context): View {
        return context.relativeLayout {
            setPadding(dip(8), dip(4), dip(8), dip(4))
            setRippleBackground()
            lparams(width = matchParent)
            relativeLayout {
                id = R.id.test
                createProgressView()
                createResultIcon()
            }.lparams(dip(36), dip(36)) {
                addRule(RelativeLayout.ALIGN_PARENT_END)
                addRule(RelativeLayout.CENTER_VERTICAL)
            }
            createTitleView()
            createUrlView()
            setOnLongClickListener {
                onLongClick(it, context)
            }
        }
    }

    private fun _RelativeLayout.setRippleBackground() {
        val attrs = intArrayOf(android.R.attr.selectableItemBackground)
        val typedArray = context.obtainStyledAttributes(attrs)
        backgroundResource = typedArray.getResourceId(0, 0)
        typedArray.recycle()
    }

    private fun _RelativeLayout.createTitleView() {
        textView {
            id = R.id.text_name
            textSize = 16f
            textColor = Color.BLACK
            singleLine = true
        }.lparams(width = matchParent, height = dip(30)) {
            alignParentTop()
            leftOf(R.id.test)
        }
    }

    private fun _RelativeLayout.createUrlView() {
        textView {
            id = R.id.text_url
            setPadding(dip(8), 0, 0, 0)
            singleLine = true
        }.lparams(width = matchParent, height = dip(24)) {
            below(R.id.text_name)
            leftOf(R.id.test)
        }
    }

    private fun _RelativeLayout.createProgressView() {
        progressBar {
            id = R.id.progress
            isIndeterminate = true
        }.lparams {
            centerInParent()
        }
    }

    private fun _RelativeLayout.createResultIcon() {
        imageView {
            id = R.id.result_icon
            scaleX = 0f
            scaleY = 0f
            visibility = View.GONE
        }.lparams {
            centerInParent()
        }
    }

    private fun onLongClick(view: View, context: Context): Boolean {
        val title = view.findViewById<TextView>(R.id.text_name).text.toString()
        val menuItemAction = ViewHolderMenuItemAction()
        PopupMenu(context, view).apply {
            inflate(R.menu.product_menu)
            setOnMenuItemClickListener {
                menuItemAction
                    .buildFactory(it.itemId)
                    .buildAction(mainActivityViewModel)
                    .perform(context, mainActivityViewModel.repository.get(title))
            }
            show()
        }
        return true
    }
}