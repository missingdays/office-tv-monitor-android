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
            try {
                withTimeout(15000) {
                    if (client.get(data.url).code() == 200) {
                        subjects[position]?.onNext(true)
                    } else throw Exception()
                }
            } catch (e: Exception) {
                subjects[position]?.onNext(false)
            }
        } finally {
            delay(1000)
            lockedPositions.remove(position)
        }

    }

}