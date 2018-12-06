package health.officetv.jetbrains.org.officetvhealthchecker.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ApiRepository
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.RepositoryAdapter
import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.Exception
import java.text.FieldPosition

class MainActivityViewModel(
    @JvmField val repository: ApiRepository,
    @JvmField val client: HttpClient
) : ViewModel() {

    private val accessibilityController = AccessibilityController(repository, client)
    val repositoryAdapter = RepositoryAdapter(repository, accessibilityController)

    class Factory(
        private val sharedPreferences: SharedPreferences,
        private val httpClient: HttpClient
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass == MainActivityViewModel::class.java) {
                val repository = ApiRepository(sharedPreferences)
                MainActivityViewModel(repository, httpClient) as T
            } else super.create(modelClass)
        }
    }
}

class AccessibilityController(
    private val apiRepository: ApiRepository,
    private val client: HttpClient
) {

    var subjects = Array<BehaviorSubject<Boolean>?>(apiRepository.size()) { null }
        private set

    fun notifyDataSetChanged() {
        subjects = Array(apiRepository.size()) {
            subjects.getOrNull(it)
        }
    }

    fun requestCheck(position: Int) = GlobalScope.launch {
        val data = apiRepository.getAll()[position]
        try {
            withTimeout(15000) {
                if (client.get(data.url).code() == 200) {
                    subjects[position]!!.onNext(true)
                } else throw Exception()
            }
        } catch (e: Exception) {
            subjects[position]!!.onNext(false)
        }
    }

}