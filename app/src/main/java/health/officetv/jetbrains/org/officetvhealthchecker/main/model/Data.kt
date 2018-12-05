package health.officetv.jetbrains.org.officetvhealthchecker.main.model

data class Data(val name: String, val url: String) {
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