import api.google.GoogleApiUtil
import api.google.sheets.SpreadsheetHelper
import api.spotify.SpotifyApiUtil
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import playlist.GoogleSheetsPlaylistManager
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        System.err.println("Usage: music-manager <Google Sheets ID>")
        exitProcess(1)
    }

    val spreadsheetId = args[0]
    val transport = GoogleNetHttpTransport.newTrustedTransport();
    val spotifyApi = SpotifyApiUtil.getApi()

    val sheetsApi = SpreadsheetHelper(GoogleApiUtil.getSheetsApi(transport), spreadsheetId)
    val sheetsPlaylistLoader = GoogleSheetsPlaylistManager(sheetsApi)

    val worksheetTitle = "Groovy Baby"
    val playlistMetadata = sheetsPlaylistLoader.loadPlaylistMetadata(worksheetTitle)
    val (songs, songLocationRegistry) = sheetsPlaylistLoader.loadSongs(worksheetTitle)
    val songsWithMetadata = spotifyApi.getSongsWithMetadata(songs)
    val playlist = playlistMetadata.withSongs(songsWithMetadata)


    spotifyApi.syncPlaylist(playlist)
    sheetsPlaylistLoader.updateVersions(worksheetTitle, playlist)
    sheetsPlaylistLoader.updateSongMetadata(worksheetTitle, songsWithMetadata, songLocationRegistry)
}
