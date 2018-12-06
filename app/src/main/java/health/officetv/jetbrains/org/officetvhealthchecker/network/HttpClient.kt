package health.officetv.jetbrains.org.officetvhealthchecker.network

import com.github.kittinunf.fuel.httpGet
import java.io.ByteArrayInputStream
import java.io.InputStream

interface HttpClient {

    fun get(url: String): HttpGet

    interface HttpGet {
        fun stream(): InputStream
        fun body(): String
        fun code(): Int
    }
}

class FuelHttpClient: HttpClient {

    override fun get(url: String) = FuelHttpGet(url)

    class FuelHttpGet(url: String) : HttpClient.HttpGet {

        private val response = url.httpGet().response()

        override fun stream(): InputStream {
            return ByteArrayInputStream(response.third.get())
        }

        override fun body() = String(response.third.get())

        override fun code() = response.second.statusCode
    }
}

