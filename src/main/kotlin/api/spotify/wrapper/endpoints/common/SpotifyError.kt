package api.spotify.wrapper.endpoints.common

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyError(
    @JsonProperty("error") val errorDetails: SpotifyErrorDetails,
)
