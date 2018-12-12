package health.officetv.jetbrains.org.officetvhealthchecker.log.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import health.officetv.jetbrains.org.officetvhealthchecker.log.LogActivityViewModel
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import health.officetv.jetbrains.org.officetvhealthchecker.network.FuelHttpClient
import org.jetbrains.anko.setContentView

class LogActivity: AppCompatActivity() {

    private lateinit var logActivityViewModel: LogActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val data = intent.getSerializableExtra(Data::class.java.simpleName) as Data
        val factory = LogActivityViewModel.Factory(FuelHttpClient(), data)
        logActivityViewModel = ViewModelProviders.of(this, factory)[LogActivityViewModel::class.java]
        super.onCreate(savedInstanceState)

        LogActivityUI(logActivityViewModel).setContentView(this)
    }

}