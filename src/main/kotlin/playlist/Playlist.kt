package playlist

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.util.*

class Playlist private constructor(
    val id: String,
    val songs: List<Song> = emptyList(),
    val name: String = "",
    val description: String = "",
    val lastSyncMetadataVersion: String? = null,
    val lastSyncSongVersion: String? = null,
) {

    val songVersion: String = songVersionHash()
    val metadataVersion: String = metadataVersionHash()
    val url: String
        get() = "https://open.spotify.com/playlist/$id"
    val uri: String
        get() = "spotify:playlist:$id"

    private fun metadataVersionHash(): String = versionHash("${versionHash(name)},${versionHash(description)}")
    private fun songVersionHash(): String = versionHash(songs.joinToString(",") { it.id })

    private fun versionHash(value: String): String {
        val bytes = value.encodeToByteArray()
        val hash = DigestUtils.getSha256Digest().digest(bytes)
        return String(Hex.encodeHex(hash))
    }

    fun isMetadataSyncRequired() = metadataVersion != lastSyncMetadataVersion
    fun isSongSyncRequired() = songVersion != lastSyncSongVersion
    fun isSyncRequired(): Boolean = isMetadataSyncRequired() || isSongSyncRequired()

    fun withSongs(songs: List<Song>): Playlist =
        Playlist(id, songs, name, description, lastSyncMetadataVersion, lastSyncSongVersion)

    companion object {
        private val ID_EXTRACTOR_REGEX: Regex =
            """^https?://open\.spotify\.com/playlist/([0-9a-zA-Z]+)(?:\?.*$|$)""".toRegex()

        private fun extractId(url: String): String {
            val matchResult = ID_EXTRACTOR_REGEX.find(url)!!
            val (id) = matchResult.destructured
            return id
        }

        fun fromId(
            id: String,
            songs: List<Song> = emptyList(),
            name: String = "",
            description: String = "",
            lastSyncMetadataVersion: String? = null,
            lastSyncSongVersion: String? = null,
        ) = Playlist(id, songs, name, description, lastSyncMetadataVersion, lastSyncSongVersion)

        fun fromUrl(
            url: String,
            songs: List<Song> = emptyList(),
            name: String = "",
            description: String = "",
            lastSyncMetadataVersion: String? = null,
            lastSyncSongVersion: String? = null,
        ) = Playlist(extractId(url), songs, name, description, lastSyncMetadataVersion, lastSyncSongVersion)
    }

    override fun toString(): String = "Playlist(name=$name, songCount=${songs.size}, id=$id)"
    override fun hashCode(): Int = Objects.hash(id, songs, name, description)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        if (id != other.id) return false
        if (songs != other.songs) return false
        if (name != other.name) return false
        if (description != other.description) return false
        return true
    }
}
