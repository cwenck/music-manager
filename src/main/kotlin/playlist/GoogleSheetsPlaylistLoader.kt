package playlist

import api.google.sheets.*
import exception.PlaylistConfigurationException
import exception.SongConfigurationException

class GoogleSheetsPlaylistLoader(val api: SpreadsheetHelper) {
    companion object {
        private val playlistNameCell: SheetCell = SheetCell.fromSheetNotation("B1")
        private val playlistDescriptionCell: SheetCell = SheetCell.fromSheetNotation("B2")
        private val playlistIdCell: SheetCell = SheetCell.fromSheetNotation("B3")
        private val lastSyncedVersionCell: SheetCell = SheetCell.fromSheetNotation("B4")
        private val lastRowCell: SheetCell = SheetCell.fromSheetNotation("B5")

        private val songRangeStart: SheetCell = SheetCell.fromSheetNotation("C3")
        private val songRangeEnd: SheetCell = SheetCell.fromSheetNotation("E3")


    }

    fun loadPlaylist(worksheetTitle: String): Playlist? {
        val metadataGrid = api.readRange(worksheetTitle, SheetRange.fromSheetNotation("B1:B5"))

        val playlistName =
            metadataGrid[playlistNameCell] ?: throw PlaylistConfigurationException("Missing playlist name")
        val playlistId = metadataGrid[playlistIdCell] ?: throw PlaylistConfigurationException("Missing playlist ID")
        val lastRow = metadataGrid[lastRowCell]?.toInt()
            ?: throw PlaylistConfigurationException("Missing last song row calculation")

        val playlistDescription = metadataGrid[playlistDescriptionCell] ?: ""
        val lastSyncedVersion = metadataGrid[lastSyncedVersionCell]

        if (lastRow <= 2) {
            return Playlist(playlistId, playlistName, playlistDescription, emptyList(), lastSyncedVersion)
        }

        val adjustedSongRangeEnd = songRangeEnd.atRow(SheetDimension.fromNumber(lastRow))
        val songRange = SheetRange.fromSheetCells(songRangeStart, adjustedSongRangeEnd)
        val songGrid = api.readRange(worksheetTitle, songRange)

        val songs = songGrid.range.rowsSequence()
            .map { row -> songGrid.getRow(row) }
            .map { rowValues -> loadSong(rowValues) }
            .toList()

        return Playlist(playlistId, playlistName, playlistDescription, songs, lastSyncedVersion)
    }

    private fun loadSong(rowValues: List<SheetValue>): Song {
        val name = rowValues.getOrNull(0)?.value
        val artist = rowValues.getOrNull(1)?.value
        val url = rowValues.getOrNull(2)?.value ?: throw SongConfigurationException("Missing song URL")
        return Song(url, name, artist)
    }
}
