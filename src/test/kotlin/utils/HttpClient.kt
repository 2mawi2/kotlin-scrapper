package utils

import app.fromJson
import app.toJson
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

class HttpClient {
    companion object {
        const val baseUri = "http://localhost:5000"

        inline fun <reified TResult : Any> get(uri: String): TResult? {
            val (_, _, result) = "$baseUri$uri"
                    .httpGet()
                    .responseString()

            return handleResult(result)
        }

        inline fun <reified TResult : Any> post(uri: String, payload: Any): TResult? {
            val (_, _, result) = "$baseUri$uri"
                    .httpPost()
                    .body(payload.toJson())
                    .responseString()

            return handleResult(result)
        }

        inline fun <reified TResult : Any> handleResult(result: Result<String, FuelError>): TResult? {
            val json = result.get()
            return if (json.isNotEmpty())
                json.fromJson()
            else
                null
        }
    }
}