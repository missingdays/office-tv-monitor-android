package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import java.lang.Exception


enum class STATE {
    REFRESHING,
    OK,
    FAILED
}

class AccessibilityController(
    private val apiRepository: ApiRepository,
    private val client: HttpClient
) {

    var subjects = Array<BehaviorSubject<STATE>?>(apiRepository.size()) { null }
        private set

    private val lockedPositions = HashSet<Int>()

    fun notifyDataSetChanged() {
        subjects = Array(apiRepository.size()) {
            subjects.getOrNull(it)
        }
    }

    fun requestCheck(position: Int) = GlobalScope.launch {
        if (lockedPositions.contains(position)) return@launch

        val subject = subjects[position]

        try {
            subject?.onNext(STATE.REFRESHING)

            lockedPositions.add(position)
            val data = apiRepository.getAll()[position]

            val health = getHealth(data.url)

            val state = if (health) STATE.OK else STATE.FAILED

            subjects[position]?.onNext(state)

        } finally {
            delay(1000)
            lockedPositions.remove(position)
        }
    }

    private suspend fun getHealth(url: String) : Boolean {
        delay(200)
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