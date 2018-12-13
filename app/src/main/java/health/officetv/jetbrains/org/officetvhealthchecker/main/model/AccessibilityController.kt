package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import health.officetv.jetbrains.org.officetvhealthchecker.network.HttpClient
import io.reactivex.subjects.BehaviorSubject
import kotlinx.coroutines.*

class AccessibilityController(
    private val apiRepository: ApiRepository,
    private val client: HttpClient
) {

    lateinit var observable: BehaviorSubject<State>

    lateinit var resultObservable: BehaviorSubject<HttpClient.HttpGet>

    private val lockedPositions = HashSet<Int>()

    fun requestCheck(position: Int) = GlobalScope.launch {
        observable.onNext(State.Refresh(position))
        val data = apiRepository.getAll()[position]
        val health = getHealth(data.url)
        val state = if (health) State.Ok(position) else State.Fail(position)
        observable.onNext(state)
    }

    suspend fun requestCheckResult(position: Int): HttpClient.HttpGet {
        val data = apiRepository.getAll()[position]
        return getHealthResult(data.url)
    }

    fun requestCheckWithLock(position: Int) = GlobalScope.launch {
        if (lockedPositions.contains(position)) return@launch
        try {
            lockedPositions.add(position)
            requestCheck(position)
        } finally {
            delay(1000)
            lockedPositions.remove(position)
        }
    }

    private suspend fun getHealth(url: String) : Boolean {
        delay(200)
        return try {
            getHealthResult(url).code() == 200
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun getHealthResult(url: String): HttpClient.HttpGet {
        return try {
            withTimeout(5000) {
                client.get("$url/health")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            HttpClient.HttpGet.generateBadResponse(e.message?: "Something very-very bad happened\n$url")
        }
    }

    sealed class State(val position: Int) {
        class Refresh(position: Int): State(position)
        class Fail(position: Int): State(position)
        class Ok(position: Int): State(position)
    }

}