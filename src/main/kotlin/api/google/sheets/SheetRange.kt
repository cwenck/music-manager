package api.google.sheets

import kotlin.math.max
import kotlin.math.min

class SheetRange constructor(
    val rangeStart: SheetCell,
    val rangeEnd: SheetCell,
) {
    fun toSheetNotation() =
        if (isSingleCellRange()) {
            rangeStart.toSheetNotation()
        } else {
            "${rangeStart.toSheetNotation()}:${rangeEnd.toSheetNotation()}"
        }

    fun isSingleCellRange(): Boolean = rangeStart == rangeEnd
    fun isMultiCellRange(): Boolean = !isSingleCellRange()

    companion object {
        fun fromSheetCells(cornerCellOne: SheetCell, cornerCellTwo: SheetCell): SheetRange {
            val topLeftCellColIndex = min(cornerCellOne.colIndex, cornerCellTwo.colIndex)
            val topLeftCellRowIndex = min(cornerCellOne.rowIndex, cornerCellTwo.rowIndex)
            val topLeftCell = SheetCell.fromIndexes(topLeftCellColIndex, topLeftCellRowIndex)

            val bottomRightCellColIndex = max(cornerCellOne.colIndex, cornerCellTwo.colIndex)
            val bottomRightCellRowIndex = max(cornerCellOne.rowIndex, cornerCellTwo.rowIndex)
            val bottomRightCell = SheetCell.fromIndexes(bottomRightCellColIndex, bottomRightCellRowIndex)

            return SheetRange(topLeftCell, bottomRightCell)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SheetRange

        if (rangeStart != other.rangeStart) return false
        if (rangeEnd != other.rangeEnd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = rangeStart.hashCode()
        result = 31 * result + rangeEnd.hashCode()
        return result
    }
}
