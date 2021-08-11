package api.google.sheets

import api.exception.ApiException
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.BatchUpdateSpreadsheetRequest
import com.google.api.services.sheets.v4.model.Request
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.ValueRange
import exception.ApiRequestException

class SheetsApiWrapper(private val sheetsApi: Sheets, private val spreadsheetId: String) {

    private val spreadsheet: Spreadsheet = sheetsApi.spreadsheets()
        .get(spreadsheetId)
        .execute()
        ?: throw ApiException("Failed to lookup spreadsheet")

//    fun worksheetTitles(): List<String> {
//        val spreadsheet: Spreadsheet = sheetsApi.spreadsheets()
//            .get(spreadsheetId)
//            .execute()
//            ?: throw ApiException("Failed to lookup worksheet titles")
//
//        return spreadsheet.sheets
//            .map { sheet -> sheet.properties.title }
//            .toList()
//    }

    fun getWorksheetByTitle(title: String): Worksheet? = spreadsheet.sheets
        .filter { sheet -> sheet.properties.title == title }
        .map { sheet -> Worksheet(sheet.properties.sheetId, sheet.properties.title) }
        .firstOrNull()

    fun readRange(worksheet: Worksheet, range: SheetRange): SheetGrid {
        val rangeSheetNotation = range.toSheetNotation(worksheet)
        val valueRange: ValueRange = sheetsApi.spreadsheets().values()
            .get(spreadsheetId, rangeSheetNotation)
            .execute()
            ?: throw ApiRequestException("Failed to read range")

        return SheetGrid.fromValueRange(range, valueRange)
    }

    fun writeRange(worksheet: Worksheet, grid: SheetGrid) {
        val rangeSheetNotation = grid.range.toSheetNotation(worksheet)
        val valueRange = grid.toValueRange(worksheet)
        sheetsApi.spreadsheets().values()
            .update(spreadsheetId, rangeSheetNotation, valueRange)
            .setValueInputOption("USER_ENTERED")
            .execute()
    }

    fun writeRangeWithFormatting(worksheet: Worksheet, grid: SheetGrid) {
        val requests = grid.toUpdateCellsRequests(worksheet).map { Request().setUpdateCells(it) }
        val batchUpdateRequest = BatchUpdateSpreadsheetRequest()
            .setRequests(requests)

        sheetsApi.spreadsheets()
            .batchUpdate(spreadsheetId, batchUpdateRequest)
            .execute()
    }
}
