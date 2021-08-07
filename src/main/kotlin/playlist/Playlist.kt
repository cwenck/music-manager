package playlist

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import playlist.sync.action.*

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val songs: List<Song>,
    val lastSyncVersion: String? = null,
) {
    val currentVersion: String = calculateVersion()

    private fun calculateVersion(): String {
        val nameBytes = name.encodeToByteArray()
        val descriptionBytes = description.encodeToByteArray()
        val songBytes = songs.asIterable()
            .map { it.id }
            .flatMap { it.encodeToByteArray().asIterable() }
            .toByteArray()

        val combinedBytes = nameBytes + descriptionBytes + songBytes


        val hash = DigestUtils.getSha256Digest().digest(combinedBytes)
        return String(Hex.encodeHex(hash))
    }

    fun isSyncRequired(): Boolean = currentVersion != lastSyncVersion

    companion object {
        fun calculateSyncActions(currentState: Playlist, desiredState: Playlist): List<PlaylistSyncAction> {
            val actions: MutableList<PlaylistSyncAction> = mutableListOf()

            if (currentState.name != desiredState.name) {
                actions.add(UpdateNamePlaylistSyncAction(desiredState.name))
            }

            if (currentState.description != desiredState.description) {
                actions.add(UpdateDescriptionPlaylistSyncAction(desiredState.description))
            }

            val currentSongs = currentState.songs.toSet()
            val desiredSongs = desiredState.songs.toSet()

            val songAdditions = desiredSongs.minus(currentSongs)
            actions.addAll(songAdditions.asSequence()
                .map { AddSongPlaylistSyncAction(it) }
                .toList())

            val songRemovals = currentSongs.minus(desiredSongs)
            actions.addAll(songRemovals.asSequence()
                .map { RemoveSongPlaylistSyncAction(it) }
                .toList())

            return actions.toList()
        }
    }
}
