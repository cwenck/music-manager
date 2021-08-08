package api.spotify.wrapper.endpoints.playlist.tracks.replace

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ReplacePlaylistTracksRequest(
    @JsonIgnore val playlistId: String,
    @JsonProperty("uris") val songUris: List<String>,
    @JsonProperty("snapshot_id") val snapshotId: String? = null,
)
