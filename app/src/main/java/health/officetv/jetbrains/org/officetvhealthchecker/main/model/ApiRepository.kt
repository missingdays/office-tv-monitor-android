package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import android.content.SharedPreferences

class ApiRepository(
    private val sharedPreferences: SharedPreferences
) : Repository<String, Data> {

    override fun set(param: String, value: Data) {
        sharedPreferences.edit().putString(param, value.toSharedString()).apply()
    }

    override fun get(param: String): Data {
        val res = sharedPreferences.getString(param, "") ?: return Data(
            "not found",
            "not found"
        )
        return Data.fromSharedString(res)
    }

    override fun remove(param: String) {
        sharedPreferences.edit().remove(param).apply()
    }

    override fun getAll(): List<Data> {
        return sharedPreferences.all.values.map {
            Data.fromSharedString(it as String)
        }.sortedBy { it.name }
    }
}