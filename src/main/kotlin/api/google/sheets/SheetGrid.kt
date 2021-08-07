package api.google.sheets

import com.google.api.services.sheets.v4.model.ValueRange

class SheetGrid(val range: SheetRange, valueRange: ValueRange) {

    operator fun contains(cell: SheetCell): Boolean = cell in range
    operator fun get(cell: SheetCell): String? = gridValues[cell]

    operator fun set(cell: SheetCell, value: String) {
        if (cell !in range) throw IndexOutOfBoundsException("Cell is not contained within the range")
        gridValues[cell] = value
    }

    private val gridValues: MutableMap<SheetCell, String> =
        valueRange.getValues().flatMapIndexed { rowIndex, inputRowValues ->
            inputRowValues.mapIndexed { colIndex, value ->
                val cell = SheetCell.fromIndexes(colIndex, rowIndex)
                value as String
                cell to value
            }
        }.toMap().toMutableMap()
}
