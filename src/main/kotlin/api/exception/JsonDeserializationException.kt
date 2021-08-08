package api.exception

class JsonDeserializationException(message: String, cause: Throwable? = null) : ApiException(message, cause)
