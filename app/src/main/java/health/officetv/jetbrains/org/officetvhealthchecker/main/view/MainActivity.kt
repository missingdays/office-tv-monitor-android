package health.officetv.jetbrains.org.officetvhealthchecker.main.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import health.officetv.jetbrains.org.officetvhealthchecker.main.MainActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.network.FuelHttpClient
import org.jetbrains.anko.*


class MainActivity : AppCompatActivity() {

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val vmFactory = MainActivityViewModel.Factory(
            getSharedPreferences("API", Context.MODE_PRIVATE),
            FuelHttpClient())
        mainActivityViewModel = ViewModelProviders
            .of(this, vmFactory)[MainActivityViewModel::class.java]

        super.onCreate(savedInstanceState)

        MainActivityUI(mainActivityViewModel).setContentView(this)

    }
}


