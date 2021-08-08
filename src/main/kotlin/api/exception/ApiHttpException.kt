package api.exception

class ApiHttpException(val statusCode: Int, message: String, cause: Throwable? = null) :
    ApiException(message, cause)
