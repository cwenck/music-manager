package api.spotify.wrapper.endpoints.playlist.tracks.add

import com.fasterxml.jackson.annotation.JsonProperty

data class AddPlaylistTracksResponse(
    @JsonProperty("snapshot_id") val snapshotId: String,
)
