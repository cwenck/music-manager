package api.spotify.wrapper

import api.exception.ApiHttpException
import api.exception.JsonDeserializationException
import com.github.kittinunf.fuel.jackson.jacksonDeserializerOf

abstract class ApiEndpoint<REQUEST, RESPONSE> {

    abstract fun call(request: REQUEST): RESPONSE

    protected fun checkResponseStatus(statusCode: Int, successfulStatusCodes: Set<Int>) {
        if (statusCode !in successfulStatusCodes) {
            throw ApiHttpException(statusCode, "API request failed")
        }
    }

    protected inline fun<reified T: Any> checkResponseStatus(content: String, statusCode: Int, successfulStatusCodes: Set<Int>) {
        if (statusCode !in successfulStatusCodes) {
            val apiError = deserialize<T>(content)
            throw ApiHttpException(statusCode, "API request failed: '$apiError'")
        }
    }

    protected inline fun <reified T : Any> deserialize(content: String): T =
        jacksonDeserializerOf<T>().deserialize(content)
            ?: throw JsonDeserializationException("Failed to deserialize context into ${T::class.java.simpleName}: '$content'")
}
