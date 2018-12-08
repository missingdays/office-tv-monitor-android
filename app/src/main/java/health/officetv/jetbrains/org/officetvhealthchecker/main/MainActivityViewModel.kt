package health.officetv.jetbrains.org.officetvhealthchecker.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.AccessibilityController
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ApiRepository
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.RepositoryAdapter
import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient

class MainActivityViewModel(
    @JvmField val repository: ApiRepository,
    @JvmField val client: HttpClient
) : ViewModel() {

    val accessibilityController =
        AccessibilityController(repository, client)
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

