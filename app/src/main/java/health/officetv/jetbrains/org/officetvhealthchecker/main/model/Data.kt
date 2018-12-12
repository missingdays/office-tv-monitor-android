package health.officetv.jetbrains.org.officetvhealthchecker.main.model

import java.io.Serializable

data class Data(val name: String, val url: String): Serializable {
    companion object {
        fun fromSharedString(str: String): Data {
            val (name, url) = str.split(",")
            return Data(name, url)
        }
    }

    fun toSharedString(): String {
        return "$name,$url"
    }
}