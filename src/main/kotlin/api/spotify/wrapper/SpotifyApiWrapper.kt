package api.spotify.wrapper

import api.CredentialExtensions.refreshTokenIfRequired
import api.spotify.wrapper.endpoints.playlist.tracks.add.AddPlaylistTracksEndpoint
import api.spotify.wrapper.endpoints.playlist.tracks.add.AddPlaylistTracksRequest
import api.spotify.wrapper.endpoints.playlist.tracks.add.AddPlaylistTracksResponse
import api.spotify.wrapper.endpoints.playlist.tracks.replace.ReplacePlaylistTracksEndpoint
import api.spotify.wrapper.endpoints.playlist.tracks.replace.ReplacePlaylistTracksRequest
import api.spotify.wrapper.endpoints.playlist.tracks.replace.ReplacePlaylistTracksResponse
import com.google.api.client.auth.oauth2.Credential
import com.wrapper.spotify.SpotifyApi
import com.wrapper.spotify.model_objects.specification.Track
import exception.ApiRequestException
import playlist.Playlist
import playlist.Song
import playlist.SongMetadata
import playlist.SongWithMetadata

class SpotifyApiWrapper(private val credential: Credential) {
    private val api: SpotifyApi
        get() {
            credential.refreshTokenIfRequired()
            return SpotifyApi.Builder()
                .setAccessToken(credential.accessToken)
                .build()
        }

    private val songsWithMetadataCache: MutableMap<String, SongWithMetadata> = mutableMapOf()

    private fun replacePlaylistSongs(playlistId: String, songs: List<Song>): ReplacePlaylistTracksResponse {
        val request = ReplacePlaylistTracksRequest(
            playlistId = playlistId,
            songUris = songs.map { it.uri },
        )
        return ReplacePlaylistTracksEndpoint(credential).call(request)
    }

    private fun addPlaylistSongs(
        playlistId: String,
        songs: List<Song>,
        position: Int? = null,
    ): AddPlaylistTracksResponse {
        val request = AddPlaylistTracksRequest(
            playlistId = playlistId,
            songUris = songs.map { it.uri },
            position = position,
        )
        return AddPlaylistTracksEndpoint(credential).call(request)
    }

    fun getSongsWithMetadata(songs: List<Song>): List<SongWithMetadata> {
        val songsById = songs.associateBy { it.id }
        val songsWithMetadata: MutableMap<String, SongWithMetadata> = mutableMapOf()

        val songsWithExistingMetadata = songsById.values
            .mapNotNull { if (it is SongWithMetadata) it else null }
            .associateBy { it.id }
        songsWithMetadata.putAll(songsWithExistingMetadata)

        val cachedSongsById = songsWithMetadataCache.filterKeys { it in songsById.keys }
        songsWithMetadata.putAll(cachedSongsById)

        val songsWithoutMetadataById = songsById.filterKeys { it !in songsWithMetadata.keys }
        val songRequestLists = songsWithoutMetadataById.values
            .map { song -> song.id }
            .chunked(50)

        val tracks = songRequestLists.map {
            api.getSeveralTracks(*it.toTypedArray())
                .build()
                .execute()
                ?: throw ApiRequestException("Failed to get tracks")
        }.flatMap { it.toList() }

        val songsWithNewMetadataById = tracks.map { it.toSongWithMetadata() }.associateBy { it.id }
        songsWithMetadataCache.putAll(songsWithNewMetadataById)
        songsWithMetadata.putAll(songsWithNewMetadataById)

        return songs.map { songsWithMetadata[it.id] ?: throw IllegalStateException("Every song lookup should succeed") }
    }

    private fun Track.toSongWithMetadata(): SongWithMetadata {
        val artists = artists.map { artist -> artist.name }
        val metadata = SongMetadata(name, artists, album.name)
        return SongWithMetadata.fromId(id, metadata)
    }

    fun syncPlaylist(playlist: Playlist) {
        if (playlist.isMetadataSyncRequired()) {
            println("Syncing playlist metadata")
            api.changePlaylistsDetails(playlist.id)
                .name(playlist.name)
                .description(playlist.description)
                .build()
                .execute()
        }

        if (playlist.isSongSyncRequired()) {
            println("Syncing songs")

            // Remove all the songs from the playlist
            replacePlaylistSongs(playlist.id, emptyList())

            // Add the playlist songs in batches of 100
            playlist.songs.chunked(100).forEach { songChunk ->
                addPlaylistSongs(playlist.id, songChunk)
            }
        }
    }
}
