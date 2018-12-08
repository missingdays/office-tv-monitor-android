package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.Exception

class AccessibilityController(
    private val apiRepository: ApiRepository,
    private val client: HttpClient
) {

    var subjects = Array<BehaviorSubject<Boolean>?>(apiRepository.size()) { null }
        private set

    private val lockedPositions = HashSet<Int>()

    fun notifyDataSetChanged() {
        subjects = Array(apiRepository.size()) {
            subjects.getOrNull(it)
        }
    }

    fun requestCheck(position: Int) = GlobalScope.launch {
        if (lockedPositions.contains(position)) return@launch
        try {
            lockedPositions.add(position)
            val data = apiRepository.getAll()[position]
            val health = getHealth(data.url)

            subjects[position]?.onNext(health)

        } finally {
            delay(1000)
            lockedPositions.remove(position)
        }
    }

    private suspend fun getHealth(url: String) : Boolean {
        return try {
            val result = withTimeout(5000) {
                client.get("$url/health")
            }

            result.code() == 200
        } catch (e: Exception) {
            println(e)

            false
        }
    }

}