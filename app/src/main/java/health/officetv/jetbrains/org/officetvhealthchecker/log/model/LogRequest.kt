package health.officetv.jetbrains.org.officetvhealthchecker.log.model

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import health.officetv.jetbrains.org.officetvhealthchecker.main.model.Data
import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jetbrains.anko.getStackTraceString
import java.lang.Exception
import java.lang.StringBuilder

class LogRequest(
    private val client: HttpClient,
    private val data: Data
) {

    private var logs = BehaviorSubject.create<String>()

    val hasValue: Boolean
        get() = logs.hasValue()
    val value: String?
        get() = logs.value

    @SuppressLint("CheckResult")
    fun subscribe(action: (String) -> Unit) {
        logs = BehaviorSubject.create()
        Handler(Looper.getMainLooper()).post {
            logs.subscribe(action)
        }
    }

    fun updateLogs() = GlobalScope.launch {
        try {
            val result = client.get(StringBuilder(data.url).append("/log").toString())
            logs.onNext(result.body())
        } catch (e: Exception) {
            logs.onNext(e.getStackTraceString())
        }
    }
}