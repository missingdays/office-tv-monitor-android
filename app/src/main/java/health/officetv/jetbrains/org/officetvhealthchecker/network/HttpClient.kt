package health.officetv.jetbrains.org.officetvhealthchecker.network

import com.github.kittinunf.fuel.httpGet
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.random.Random

interface HttpClient {

    fun get(url: String): HttpGet

    interface HttpGet {
        fun stream(): InputStream
        fun body(): String
        fun code(): Int
        fun responseMessage(): String

        companion object {
            fun generateBadResponse(message: String) = object :HttpGet {
                override fun responseMessage() = message
                override fun stream() = ByteArrayInputStream(body().toByteArray())
                override fun body() = ""
                override fun code() = Random.nextInt(100, 999)
            }
        }
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

        override fun responseMessage(): String {
            return response.second.responseMessage
        }
    }
}

