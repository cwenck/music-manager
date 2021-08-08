package playlist

data class SongMetadata(val title: String, val artists: List<String>, val album: String) {
    val artistsString: String
        get() = artists.joinToString(", ")
}
