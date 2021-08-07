package api.google.sheets

import com.google.api.services.sheets.v4.model.ValueRange

class SheetGrid(val range: SheetRange, valueRange: ValueRange) {

    operator fun contains(cell: SheetCell): Boolean = cell in range
    operator fun get(cell: SheetCell): String? = gridValues[cell]

    operator fun set(cell: SheetCell, value: String) {
        if (cell !in range) throw IndexOutOfBoundsException("Cell is not contained within the range")
        gridValues[cell] = value
    }

    fun getRow(row: SheetDimension): List<SheetValue> =
        range.rowSequence(row)
            .map { cell -> SheetValue(cell, get(cell)) }
            .toList()

    fun getColumn(col: SheetDimension): List<SheetValue> =
        range.columnSequence(col)
            .map { cell -> SheetValue(cell, get(cell)) }
            .toList()

    private val gridValues: MutableMap<SheetCell, String> =
        valueRange.getValues().flatMapIndexed { relativeRowIndex, inputRowValues ->
            inputRowValues.mapIndexed { relativeColIndex, value ->
                val cell = range.getRelativeCell(relativeColIndex, relativeRowIndex)
                value as String
                cell to value
            }
        }.toMap().toMutableMap()
}
