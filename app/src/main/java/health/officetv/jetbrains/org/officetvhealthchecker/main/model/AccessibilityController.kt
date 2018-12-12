package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

sealed class State(val position: Int) {
    class Refresh(position: Int): State(position)
    class Fail(position: Int): State(position)
    class Ok(position: Int): State(position)
}

class AccessibilityController(
    private val apiRepository: ApiRepository,
    private val client: HttpClient
) {

    val observable = BehaviorSubject.create<State>()

    private val lockedPositions = HashSet<Int>()

    fun requestCheck(position: Int) = GlobalScope.launch {
        if (lockedPositions.contains(position)) return@launch

        try {
            observable.onNext(State.Refresh(position))

            lockedPositions.add(position)
            val data = apiRepository.getAll()[position]

            val health = getHealth(data.url)

            val state = if (health) State.Ok(position) else State.Fail(position)
            observable.onNext(state)

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
            e.printStackTrace()
            false
        }
    }

}