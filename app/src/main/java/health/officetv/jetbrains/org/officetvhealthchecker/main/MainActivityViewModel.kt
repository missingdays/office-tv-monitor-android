package health.officetv.jetbrains.org.officetvhealthchecker.main

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.ApiRepository
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.RepositoryAdapter

class MainActivityViewModel(@JvmField val repository: ApiRepository) : ViewModel() {
    val repositoryAdapter = RepositoryAdapter(repository)

    class Factory(private val sharedPreferences: SharedPreferences) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass == MainActivityViewModel::class.java) {
                val repository =
                    ApiRepository(sharedPreferences)
                MainActivityViewModel(repository) as T
            } else super.create(modelClass)
        }
    }
}