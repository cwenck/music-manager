import api.google.GoogleApiUtil
import api.google.sheets.*
import api.google.sheets.formatting.SheetCellFormat
import api.google.sheets.formatting.SheetColor
import api.google.sheets.formatting.SheetHorizontalAlignment
import api.google.sheets.formatting.SheetVerticalAlignment
import api.spotify.SpotifyApiUtil
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import exception.ProgramConfigurationException
import playlist.SheetsPlaylistManager
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 2) {
        System.err.println("Usage: music-manager <Google Spreadsheet ID> <Google Worksheet Name>")
        exitProcess(1)
    }

    val spreadsheetId = args[0]
    val worksheetName = args[1]

    val transport = GoogleNetHttpTransport.newTrustedTransport();
    val spotifyApi = SpotifyApiUtil.getApi()

    val sheetsApi = SheetsApiWrapper(GoogleApiUtil.getSheetsApi(transport), spreadsheetId)
    val sheetsPlaylistManager = SheetsPlaylistManager(sheetsApi)

    val worksheet = sheetsApi.getWorksheetByTitle(worksheetName)
        ?: throw ProgramConfigurationException("Failed to find a worksheet named '$worksheetName'")

    val playlistMetadata = sheetsPlaylistManager.loadPlaylistMetadata(worksheet)
    val (songs, songLocationRegistry) = sheetsPlaylistManager.loadSongs(worksheet)
    val songsWithMetadata = spotifyApi.getSongsWithMetadata(songs)
    val playlist = playlistMetadata.withSongs(songsWithMetadata)

    spotifyApi.syncPlaylist(playlist)
    if (playlist.isSyncRequired()) {
        sheetsPlaylistManager.updateVersions(worksheet, playlist)
        sheetsPlaylistManager.updateSongMetadata(worksheet, songsWithMetadata, songLocationRegistry)
    }


//    val formatOne = SheetCellFormat(
//        verticalAlignment = SheetVerticalAlignment.MIDDLE,
//        horizontalAlignment = SheetHorizontalAlignment.CENTER,
//        fontFamily = "Roboto Mono",
//        fontSize = 10,
//    )
//    val formatTwo = SheetCellFormat(
//        backgroundColor = SheetColor.BLACK,
//        fontColor = SheetColor.WHITE,
//    )
//
//    val values: Map<SheetCell, String> = mapOf(
//        "C10" to "https://open.spotify.com/track/2oeqKWbVwK5Ly2vjwWJKHd",
//        "C11" to "https://open.spotify.com/track/66tkDkPsznE5zIHNt4QkXB",
//    ).mapKeys { it.key.toSheetCell() }
//
//    val formats: Map<SheetCell, SheetCellFormat> = mapOf(
//        "C10" to formatOne,
//        "C11" to formatTwo,
//    ).mapKeys { it.key.toSheetCell() }
//
//    val sheetGrid = SheetGrid.fromValueMap("C10:C11".toSheetRange(), values, formats)
//    sheetsApi.writeRangeWithFormatting(worksheet, sheetGrid)
}
