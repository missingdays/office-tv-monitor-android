package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.BaseAdapter
import androidx.annotation.IdRes
import health.officetv.jetbrains.org.officetvhealthchecker.R
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.view.DataInputView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class MenuItemAction {
    abstract fun buildFactory(@IdRes id: Int): ActionFactory
}

class ViewHolderMenuItemAction : MenuItemAction() {

    override fun buildFactory(id: Int): ActionFactory {
        return when (id) {
            R.id.product_remove -> RemoveActionFactory()
            R.id.product_modify -> ModifyActionFactory()
            R.id.product_update -> UpdateActionFactory()
            else -> throw IllegalArgumentException("wtf")
        }
    }
}

interface Action {
    fun perform(context: Context, data: Data): Boolean
}

interface ActionFactory {
    fun buildAction(mainActivityViewModel: MainActivityViewModel): Action
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

class RemoveActionFactory : ActionFactory {
    override fun buildAction(mainActivityViewModel: MainActivityViewModel): Action {
        return RemoveAction(mainActivityViewModel.repositoryAdapter, mainActivityViewModel.repository)
    }
}


class ModifyAction(
    private val adapter: BaseAdapter,
    private val repository: ApiRepository
) : Action {

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

class ModifyActionFactory : ActionFactory {
    override fun buildAction(mainActivityViewModel: MainActivityViewModel): Action {
        return ModifyAction(mainActivityViewModel.repositoryAdapter, mainActivityViewModel.repository)
    }
}


class UpdateAction(
    private val mainActivityViewModel: MainActivityViewModel
) : Action {

    override fun perform(context: Context, data: Data): Boolean {
        val pos = mainActivityViewModel.repository.getAll().indexOf(data)
        GlobalScope.launch {
            val result = mainActivityViewModel.accessibilityController.requestCheckResult(pos)
            Handler(Looper.getMainLooper()).post {
                mainActivityViewModel.accessibilityController.resultObservable.onNext(result)
            }
        }
        return true
    }
}

class UpdateActionFactory : ActionFactory {
    override fun buildAction(mainActivityViewModel: MainActivityViewModel): Action {
        return UpdateAction(mainActivityViewModel)
    }
}
