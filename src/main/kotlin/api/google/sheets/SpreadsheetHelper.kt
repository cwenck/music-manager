package api.google.sheets

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.ValueRange
import exception.ApiRequestException

class SpreadsheetHelper(private val sheetsApi: Sheets, private val spreadsheetId: String) {

    fun worksheetTitles(): List<String> {
        val spreadsheet: Spreadsheet = sheetsApi.spreadsheets()
            .get(spreadsheetId)
            .execute()
            ?: throw ApiRequestException("Failed to lookup worksheet titles")

        return spreadsheet.sheets
            .map { sheet -> sheet.properties.title }
            .toList()
    }

    fun readRange(title: String, range: SheetRange): SheetGrid {
        val rangeSheetNotation = range.toSheetNotation(title)
        val valueRange: ValueRange = sheetsApi.spreadsheets().values()
            .get(spreadsheetId, rangeSheetNotation)
            .execute()
            ?: throw ApiRequestException("Failed to read range")

        return SheetGrid.fromValueRange(range, valueRange)
    }

    fun writeRange(title: String, grid: SheetGrid) {
        val rangeSheetNotation = grid.range.toSheetNotation(title)
        val valueRange = grid.toValueRange(title)
        sheetsApi.spreadsheets().values()
            .update(spreadsheetId, rangeSheetNotation, valueRange)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }
}
