package api.exception

open class ApiException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
