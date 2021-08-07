import api.google.GoogleApiUtil
import api.google.sheets.SpreadsheetHelper
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import playlist.GoogleSheetsPlaylistLoader

private val SPREADSHEET_ID = "1Bs1WCVUM6KmWlKJ8cyvBjbkbWSzbdGh0QFonqJymMB8"

fun main(args: Array<String>) {
    val transport = GoogleNetHttpTransport.newTrustedTransport();
    val sheetsApi = GoogleApiUtil.getSheetsApi(transport)

    sheetsApi.spreadsheets().values().batchGet(SPREADSHEET_ID).execute()
    val api = SpreadsheetHelper(sheetsApi, SPREADSHEET_ID)
    val sheetsPlaylistLoader = GoogleSheetsPlaylistLoader(api)
    val playlist = sheetsPlaylistLoader.loadPlaylist("Groovy Baby")

    println(playlist)
//    println(sheet)
}
