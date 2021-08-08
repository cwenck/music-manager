package api.google.sheets

import com.google.api.services.sheets.v4.model.ValueRange

class SheetGrid private constructor(val range: SheetRange, values: Map<SheetCell, String>) {
    private val gridValues: MutableMap<SheetCell, String> = values.toMutableMap()

    companion object {
        fun fromValueRange(range: SheetRange, valueRange: ValueRange): SheetGrid {
            val values = valueRange.getValues().flatMapIndexed { relativeRowIndex, inputRowValues ->
                inputRowValues.mapIndexed { relativeColIndex, value ->
                    val cell = range.getRelativeCell(relativeColIndex, relativeRowIndex)
                    value as String
                    cell to value
                }
            }.toMap().toMutableMap()

            return SheetGrid(range, values)
        }

        fun fromValueMap(range: SheetRange, valueMap: Map<SheetCell, String>): SheetGrid = SheetGrid(range, valueMap)
    }

    operator fun contains(cell: SheetCell): Boolean = cell in range
    operator fun get(cell: SheetCell): String? = gridValues[cell]

    operator fun set(cell: SheetCell, value: String) {
        if (cell !in range) throw IndexOutOfBoundsException("Cell is not contained within the range")
        gridValues[cell] = value
    }

    fun getRow(row: SheetDimension): List<SheetValue> =
        range.rowCellsSequence(row)
            .map { cell -> SheetValue(cell, get(cell)) }
            .toList()

    fun getColumn(col: SheetDimension): List<SheetValue> =
        range.columnCellsSequence(col)
            .map { cell -> SheetValue(cell, get(cell)) }
            .toList()

//    private val gridValues: MutableMap<SheetCell, String> =
//        valueRange.getValues().flatMapIndexed { relativeRowIndex, inputRowValues ->
//            inputRowValues.mapIndexed { relativeColIndex, value ->
//                val cell = range.getRelativeCell(relativeColIndex, relativeRowIndex)
//                value as String
//                cell to value
//            }
//        }.toMap().toMutableMap()

    fun toValueRange(worksheetTitle: String): ValueRange {
        val values = range.rowsSequence()
            .map { row ->
                range.rowCellsSequence(row)
                    .map { cell -> get(cell) ?: "" }
                    .toList()
            }.toList()

        return ValueRange()
            .setRange(range.toSheetNotation(worksheetTitle))
            .setValues(values)
            .setMajorDimension("ROWS")
    }
}
