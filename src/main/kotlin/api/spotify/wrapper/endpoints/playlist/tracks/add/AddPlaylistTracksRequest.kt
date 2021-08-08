package api.spotify.wrapper.endpoints.playlist.tracks.add

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AddPlaylistTracksRequest(
    @JsonIgnore val playlistId: String,
    @JsonProperty("uris") val songUris: List<String>,
    @JsonProperty("position") val position: Int? = null,
)
