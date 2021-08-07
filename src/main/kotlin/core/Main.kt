import api.google.GoogleApiUtil
import api.google.sheets.SheetCell
import api.google.sheets.SheetRange
import api.google.sheets.SpreadsheetHelper
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport

private val SPREADSHEET_ID = "1Bs1WCVUM6KmWlKJ8cyvBjbkbWSzbdGh0QFonqJymMB8"

fun main(args: Array<String>) {
    val transport = GoogleNetHttpTransport.newTrustedTransport();
    val sheetsApi = GoogleApiUtil.getSheetsApi(transport)

    sheetsApi.spreadsheets().values().batchGet(SPREADSHEET_ID).execute()
    val spreadsheetHelper = SpreadsheetHelper(sheetsApi, SPREADSHEET_ID)
//    val sheet: Sheet = spreadsheetHelper.getSheetByTitleOrThrow("Groovy Baby")


    val result = spreadsheetHelper.readRange("Groovy Baby", SheetRange.fromSheetCells(
        SheetCell.fromSheetNotation("A1"),
        SheetCell.fromSheetNotation("C5")
    ))

    println(result)
//    println(sheet)
}
