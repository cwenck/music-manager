package playlist

import java.util.*

sealed class Song(val id: String) {
    abstract val metadata: SongMetadata?
    val uri: String
        get() = "spotify:track:$id"
    val url: String
        get() = "https://open.spotify.com/track/$id"

    abstract fun hasMetadata(): Boolean
    fun withMetadata(metadata: SongMetadata): SongWithMetadata = SongWithMetadata(id, metadata)
    fun withoutMetadata(): SongWithoutMetadata = SongWithoutMetadata(id)

    companion object {
        fun fromUrl(url: String, metadata: SongMetadata? = null) =
            if (metadata != null) {
                SongWithMetadata(extractId(url), metadata)
            } else {
                SongWithoutMetadata(extractId(url))
            }

        fun fromId(id: String, metadata: SongMetadata? = null) =
            if (metadata != null) {
                SongWithMetadata(id, metadata)
            } else {
                SongWithoutMetadata(id)
            }
    }

    override fun hashCode(): Int = Objects.hash(id, metadata)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SongWithMetadata

        if (id != other.id) return false
        if (metadata != other.metadata) return false

        return true
    }
}

class SongWithMetadata(id: String, override val metadata: SongMetadata) : Song(id) {
    override fun hasMetadata() = true
    override fun toString(): String = "[$uri] ${metadata.title} - ${metadata.artistsString}"

    companion object {
        fun fromUrl(url: String, metadata: SongMetadata) = SongWithMetadata(extractId(url), metadata)
        fun fromId(id: String, metadata: SongMetadata) = SongWithMetadata(id, metadata)
    }
}

class SongWithoutMetadata(id: String) : Song(id) {
    override val metadata: SongMetadata? = null
    override fun hasMetadata(): Boolean = false
    override fun toString(): String = "[$id]"

    companion object {
        fun fromUrl(url: String) = SongWithoutMetadata(extractId(url))
        fun fromId(id: String) = SongWithoutMetadata(id)
    }
}

private val ID_EXTRACTOR_REGEX: Regex = """^https?://open\.spotify\.com/track/([0-9a-zA-Z]+)(?:\?.*$|$)""".toRegex()
private fun extractId(url: String): String {
    val matchResult = ID_EXTRACTOR_REGEX.find(url)!!
    return matchResult.destructured.component1()
}
