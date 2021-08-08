package exception

class ApiRequestException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
