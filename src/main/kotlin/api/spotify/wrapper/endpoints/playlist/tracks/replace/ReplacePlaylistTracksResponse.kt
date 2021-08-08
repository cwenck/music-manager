package api.spotify.wrapper.endpoints.playlist.tracks.replace

import com.fasterxml.jackson.annotation.JsonProperty

data class ReplacePlaylistTracksResponse(
    @JsonProperty("snapshot_id") val snapshotId: String,
)
