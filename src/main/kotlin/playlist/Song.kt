package playlist

class Song(val url: String, val name: String? = null, val artist: String? = null) {
    companion object {
        private val ID_EXTRACTOR_REGEX: Regex = """^https?://open.spotify.com/track/([0-9a-zA-Z]+)$""".toRegex()
    }

    val id: String = ID_EXTRACTOR_REGEX.find(url)!!.destructured.component1()

    override fun toString(): String = "$name - $artist [$id]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Song

        if (url != other.url) return false
        if (name != other.name) return false
        if (artist != other.artist) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (artist?.hashCode() ?: 0)
        result = 31 * result + id.hashCode()
        return result
    }


}
