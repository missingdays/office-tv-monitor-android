package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.content.Context
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.BaseAdapter
import android.widget.EditText
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ApiRepository
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

class DataInputView(
    context: Context,
    repository: ApiRepository,
    repositoryAdapter: BaseAdapter
) {

    constructor(context: Context, mainActivityViewModel: MainActivityViewModel) :
            this(context, mainActivityViewModel.repository, mainActivityViewModel.repositoryAdapter)

    @JvmField
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    @JvmField
    val titleTextView = context.editText {
        id = titleTextViewId
        hintResource = R.string.name
        inputType = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME
        layoutParams = ViewGroup.LayoutParams(matchParent, dip(50))
    }

    @JvmField
    val urlTextView = context.editText {
        id = urlTextViewId
        hintResource = R.string.url
        inputType = EditorInfo.TYPE_TEXT_VARIATION_URI
        layoutParams = ViewGroup.LayoutParams(matchParent, dip(50))
    }

    init {
        val view = context.verticalLayout {
            addView(titleTextView)
            addView(urlTextView)
        }
        builder.setView(view)

        builder.setNegativeButton(R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        builder.setPositiveButton(R.string.add) { _, _ ->
            val name = titleTextView.text.toString()
            val url = urlTextView.text.toString()
            if (name.isBlank() || url.isBlank()) return@setPositiveButton
            val data = Data(name, url)
            repository.set(data.name, data)
            repositoryAdapter.notifyDataSetChanged()
        }
    }

    companion object {
        @IdRes
        const val titleTextViewId = R.id.text_name

        @IdRes
        const val urlTextViewId = R.id.text_url
    }
}

inline fun Context.editText(init: (@AnkoViewDslMarker android.widget.EditText).() -> Unit): android.widget.EditText {
    return ankoView({ EditText(it) }, 0, init)
}