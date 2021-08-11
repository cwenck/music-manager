package api.google.sheets

import api.google.sheets.formatting.SheetCellFormat
import com.google.api.services.sheets.v4.model.*

class SheetGrid private constructor(
    val range: SheetRange,
    private val gridValues: Map<SheetCell, String>,
    private val gridFormatting: Map<SheetCell, SheetCellFormat> = emptyMap(),
) {

    companion object {
        fun fromValueRange(
            range: SheetRange,
            valueRange: ValueRange,
            formatting: Map<SheetCell, SheetCellFormat> = emptyMap(),
        ): SheetGrid {
            val values = valueRange.getValues().flatMapIndexed { relativeRowIndex, inputRowValues ->
                inputRowValues.mapIndexed { relativeColIndex, value ->
                    val cell = range.getRelativeCell(relativeColIndex, relativeRowIndex)
                    value as String
                    cell to value
                }
            }.toMap().toMutableMap()

            return SheetGrid(range, values, formatting)
        }

        fun fromValueMap(
            range: SheetRange,
            valueMap: Map<SheetCell, String>,
            formatting: Map<SheetCell, SheetCellFormat> = emptyMap(),
        ): SheetGrid = SheetGrid(range, valueMap, formatting)
    }

    operator fun contains(cell: SheetCell): Boolean = cell in range
    fun getValue(cell: SheetCell): String? = gridValues[cell]
    fun getFormatting(cell: SheetCell): SheetCellFormat? = gridFormatting[cell]

    fun getRowValues(row: SheetDimension): List<SheetValue> =
        range.rowCellsSequence(row)
            .map { cell -> SheetValue(cell, getValue(cell)) }
            .toList()

    fun getColumnValues(col: SheetDimension): List<SheetValue> =
        range.columnCellsSequence(col)
            .map { cell -> SheetValue(cell, getValue(cell)) }
            .toList()

    fun toValueRange(worksheet: Worksheet): ValueRange {
        val values = range.rowsSequence()
            .map { row ->
                range.rowCellsSequence(row)
                    .map { cell -> getValue(cell) ?: "" }
                    .toList()
            }.toList()

        return ValueRange()
            .setRange(range.toSheetNotation(worksheet))
            .setValues(values)
            .setMajorDimension("ROWS")
    }

    fun toUpdateCellsRequests(worksheet: Worksheet): List<UpdateCellsRequest> {
        return range.rowsSequence().flatMap { row ->
            range.rowCellsSequence(row).map { cell ->
                val value = getValue(cell) ?: ""
                val formatting = getFormatting(cell)

                val data = RowData().setValues(listOf(
                    CellData()
                        .setUserEnteredValue(ExtendedValue().setStringValue(value))
                        .setUserEnteredFormat(formatting?.toCellFormat())
                ))
                val fields: MutableList<String> = mutableListOf()
                val formattingFields = formatting?.let {
                    it.getFields().map { field -> "userEnteredFormat.$field" }
                } ?: emptyList()

                fields.addAll(formattingFields)
                fields.add("userEnteredValue")

                UpdateCellsRequest()
                    .setRange(cell.toGridRange(worksheet))
                    .setRows(listOf(data))
                    .setFields(fields.joinToString(","))
            }.toList()
        }.toList()
    }
}
