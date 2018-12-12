package health.officetv.jetbrains.org.officetvhealthchecker.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import health.officetv.jetbrains.org.officetvhealthchecker.log.model.LogRequest
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject

class LogActivityViewModel(
    @JvmField val data: Data,
    @JvmField val logRequest: LogRequest
) : ViewModel() {

    val uiController = BehaviorSubject.create<State>()

    sealed class State {
        object Loading: State()
        object Loaded: State()
    }

    class Factory(
        private val httpClient: HttpClient,
        private val data: Data
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass == LogActivityViewModel::class.java) {
                val logRequest = LogRequest(httpClient, data)
                return LogActivityViewModel(data, logRequest) as T
            } else super.create(modelClass)
        }
    }
}