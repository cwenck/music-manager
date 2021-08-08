package playlist

import api.google.sheets.*
import exception.PlaylistConfigurationException

class GoogleSheetsPlaylistManager(val api: SpreadsheetHelper) {
    companion object {
        private val playlistNameCell: SheetCell = SheetCell.fromSheetNotation("B1")
        private val playlistDescriptionCell: SheetCell = SheetCell.fromSheetNotation("B2")
        private val playlistUrlCell: SheetCell = SheetCell.fromSheetNotation("B3")
        private val lastSyncMetadataVersionCell: SheetCell = SheetCell.fromSheetNotation("B4")
        private val lastSyncSongVersionCell: SheetCell = SheetCell.fromSheetNotation("B5")

        private val firstRowCell: SheetCell = SheetCell.fromSheetNotation("B6")
        private val lastRowCell: SheetCell = SheetCell.fromSheetNotation("B7")

        private val songRangeStartCol: SheetDimension = SheetDimension.fromName("C")
        private val songRangeEndCol: SheetDimension = SheetDimension.fromName("F")

        private val songMetadataRangeStartCol: SheetDimension = SheetDimension.fromName("D")
        private val songMetadataRangeEndCol: SheetDimension = SheetDimension.fromName("F")

        private val songUrlCol = SheetDimension.fromName("C")
        private val songNameCol = SheetDimension.fromName("D")
        private val songArtistsCol = SheetDimension.fromName("E")
        private val songAlbum = SheetDimension.fromName("F")

        private const val SONG_URL_INDEX: Int = 0
        private const val SONG_TITLE_INDEX: Int = 1
        private const val SONG_ARTISTS_INDEX: Int = 2
        private const val SONG_ALBUM_INDEX: Int = 3
    }

    fun loadPlaylistMetadata(worksheetTitle: String): Playlist {
        val metadataGrid = api.readRange(worksheetTitle, SheetRange.fromSheetNotation("B1:B5"))

        val name = metadataGrid[playlistNameCell] ?: throw PlaylistConfigurationException("Missing playlist name")
        val url = metadataGrid[playlistUrlCell] ?: throw PlaylistConfigurationException("Missing playlist ID")
        val description = metadataGrid[playlistDescriptionCell] ?: ""
        val lastSyncMetadataVersion = metadataGrid[lastSyncMetadataVersionCell]
        val lastSyncSongVersion = metadataGrid[lastSyncSongVersionCell]

        return Playlist.fromUrl(url, emptyList(), name, description, lastSyncMetadataVersion, lastSyncSongVersion)
    }

    fun loadSongs(worksheetTitle: String): Pair<List<Song>, SongLocationRegistry> {
        val songMetadataGrid = api.readRange(worksheetTitle, SheetRange.fromSheetNotation("B6:B7"))
        val firstRow = songMetadataGrid[firstRowCell]?.toInt()
            ?: throw PlaylistConfigurationException("Missing first song row calculation")
        val lastRow = songMetadataGrid[lastRowCell]?.toInt()
            ?: throw PlaylistConfigurationException("Missing last song row calculation")

        if (lastRow < firstRow) return Pair(emptyList(), SongLocationRegistry(emptyMap()))

        val songRangeStartCell = songRangeStartCol.toCellAtRow(SheetDimension.fromNumber(firstRow))
        val songRangeEndCell = songRangeEndCol.toCellAtRow(SheetDimension.fromNumber(lastRow))
        val songRange = SheetRange.fromSheetCells(songRangeStartCell, songRangeEndCell)
        val songGrid = api.readRange(worksheetTitle, songRange)

        val songsByDimension = songGrid.range.rowsSequence()
            .map { row ->
                val song = loadSong(songGrid.getRow(row)) ?: return@map null
                row to song
            }
            .filterNotNull()
            .toMap(LinkedHashMap())

        return Pair(songsByDimension.values.toList(), SongLocationRegistry(songsByDimension))
    }


    private fun loadSong(rowValues: List<SheetValue>): Song? {
        val url = songUrl(rowValues) ?: return null
        return Song.fromUrl(url, songMetadata(rowValues))
    }

    private fun songMetadata(rowValues: List<SheetValue>): SongMetadata? {
        val title = songTitle(rowValues) ?: return null
        val album = songAlbum(rowValues) ?: return null
        val artists = songArtists(rowValues)

        if (artists.isEmpty()) return null
        return SongMetadata(title, artists, album)
    }

    private fun songUrl(rowValues: List<SheetValue>): String? = rowValues.getOrNull(SONG_URL_INDEX)?.value
    private fun songTitle(rowValues: List<SheetValue>): String? = rowValues.getOrNull(SONG_TITLE_INDEX)?.value
    private fun songAlbum(rowValues: List<SheetValue>) = rowValues.getOrNull(SONG_ALBUM_INDEX)?.value
    private fun songArtists(rowValues: List<SheetValue>) =
        rowValues.getOrNull(SONG_ARTISTS_INDEX)?.value?.let { artistText ->
            artistText.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        } ?: emptyList()


    fun updateVersions(worksheetTitle: String, playlist: Playlist) {
        if (!playlist.isSyncRequired()) return

        val valueMap = mapOf(
            lastSyncMetadataVersionCell to playlist.metadataVersion,
            lastSyncSongVersionCell to playlist.songVersion,
        )

        val grid = SheetGrid.fromValueMap(SheetRange.fromSheetNotation("B4:B5"), valueMap)
        api.writeRange(worksheetTitle, grid)
    }

    fun updateSongMetadata(
        worksheetTitle: String,
        songs: Collection<SongWithMetadata>,
        songLocationRegistry: SongLocationRegistry,
    ) {
        if (songs.isEmpty()) return

        val firstRow = songLocationRegistry.rowStart!!
        val lastRow = songLocationRegistry.rowEnd!!

        val songRangeStartCell = songRangeStartCol.toCellAtRow(firstRow)
        val songRangeEndCell = songRangeEndCol.toCellAtRow(lastRow)
        val songRange = SheetRange.fromSheetCells(songRangeStartCell, songRangeEndCell)

        val songsWithMetadataById = songs.associateBy { it.id }

        val gridValues = songRange.rowsSequence().mapNotNull { row ->
            val songWithMetadata = songLocationRegistry[row]?.let { songWithoutMetadata ->
                songsWithMetadataById[songWithoutMetadata.id]
            }

            songWithMetadata?.let { mapSongWithMetadataToSheetCells(row, songWithMetadata) }
        }.flatMap { it }.toMap()
        val grid = SheetGrid.fromValueMap(songRange, gridValues)

        api.writeRange(worksheetTitle, grid)
    }

    private fun mapSongWithMetadataToSheetCells(
        row: SheetDimension,
        song: SongWithMetadata,
    ): List<Pair<SheetCell, String>> =
        listOf(
            row.toCellAtColumn(songUrlCol) to song.url,
            row.toCellAtColumn(songNameCol) to song.metadata.title,
            row.toCellAtColumn(songArtistsCol) to song.metadata.artistsString,
            row.toCellAtColumn(songAlbum) to song.metadata.album
        )

}
