package api.google.sheets

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.ValueRange

class SpreadsheetHelper(private val sheetsApi: Sheets, private val spreadsheetId: String) {

    private val spreadsheet: Spreadsheet = sheetsApi.spreadsheets()
        .get(spreadsheetId)
        .execute()
        ?: throw IllegalStateException("No sheet with ID $spreadsheetId found")


    private val sheetsByTitle = spreadsheet.sheets.associateBy { it.properties.title }
    private val sheetsById = spreadsheet.sheets.associateBy { it.properties.sheetId }

    private fun getSheetId(title: String): Int? = getSheetByTitle(title)?.properties?.sheetId
    private fun getSheetIdOrThrow(title: String): Int = getSheetByTitleOrThrow(title).properties.sheetId

    private fun getSheetByTitle(title: String): Sheet? = sheetsByTitle[title]
    private fun getSheetByTitleOrThrow(title: String): Sheet =
        sheetsByTitle[title] ?: throw IllegalArgumentException("No sheet found titled \"$title\"")

    fun readRange(title: String, range: SheetRange): SheetGrid {
        val values: ValueRange = sheetsApi.spreadsheets().values()
            .get(spreadsheetId, range.toSheetNotation(title))
            .execute()
            ?: throw IllegalStateException("Failed to execute API request")


        return SheetGrid(range, values)
    }

//    private fun rangeToSheetNotation(title: String, range: SheetRange): String = "$title!${range.toSheetNotation()}"

//    fun readRange(range: String) {
//        sheetsApi.spreadsheets().values()
//            .batchGet(spreadsheetId)
//            .setRanges()
//            .execute()
//            ?: throw IllegalStateException("Failed to read values for range $range")
//    }
}
