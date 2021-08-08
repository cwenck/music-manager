package api.spotify.wrapper.endpoints.common

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyErrorDetails(
    @JsonProperty("status") val status: Int,
    @JsonProperty("message") val message: String,
)
