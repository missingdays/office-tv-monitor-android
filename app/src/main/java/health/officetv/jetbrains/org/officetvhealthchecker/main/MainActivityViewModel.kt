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
    @JvmField val accessibilityController: AccessibilityController
) : ViewModel() {

    lateinit var repositoryAdapter: RepositoryAdapter
        private set

    class Factory(
        private val sharedPreferences: SharedPreferences,
        private val httpClient: HttpClient
    ) : ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass == MainActivityViewModel::class.java) {
                val repository = ApiRepository(sharedPreferences)
                val accessibilityController = AccessibilityController(repository, httpClient)
                val viewModel = MainActivityViewModel(repository, accessibilityController)
                viewModel.repositoryAdapter = RepositoryAdapter(viewModel)
                return viewModel as T
            } else super.create(modelClass)
        }
    }
}

