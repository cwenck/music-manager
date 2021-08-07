package api.google.sheets

import kotlin.math.max
import kotlin.math.min

class SheetRange constructor(
    val startCell: SheetCell,
    val endCell: SheetCell,
) {
    private val colIndexRange = startCell.colIndex..endCell.colIndex
    private val rowIndexRange = startCell.rowIndex..endCell.rowIndex

    val columns = (endCell.colIndex - startCell.colIndex) + 1
    val rows = (endCell.rowIndex - startCell.rowIndex) + 1

    fun rowByRowSequence(): Sequence<SheetCell> = generateSequence(startCell) { cell ->
        if (cell.rowIndex < endCell.rowIndex) {
            cell.plusRows(1)
        } else if (cell.colIndex < endCell.colIndex) {
            SheetCell.fromIndexes(cell.colIndex + 1, startCell.rowIndex)
        } else {
            null
        }
    }

    fun columnByColumnSequence(): Sequence<SheetCell> = generateSequence(startCell) { cell ->
        if (cell.colIndex < endCell.colIndex) {
            cell.plusColumns(1)
        } else if (cell.rowIndex < endCell.rowIndex) {
            SheetCell.fromIndexes(startCell.colIndex, cell.rowIndex + 1)
        } else {
            null
        }
    }

    operator fun contains(cell: SheetCell): Boolean =
        cell.colIndex in colIndexRange && cell.rowIndex in rowIndexRange

    fun cellAtRelativeIndex(colIndex: Int, rowIndex: Int): SheetCell =
        startCell.plusColumns(colIndex).plusRows(rowIndex)

    fun getRelativeRowIndex(cell: SheetCell): Int = cell.rowIndex - startCell.rowIndex
    fun getRelativeColIndex(cell: SheetCell): Int = cell.colIndex - startCell.colIndex

    fun toSheetNotation() =
        if (isSingleCellRange()) {
            startCell.toSheetNotation()
        } else {
            "${startCell.toSheetNotation()}:${endCell.toSheetNotation()}"
        }

    fun toSheetNotation(title: String) = "$title!${toSheetNotation()}"

    fun isSingleCellRange(): Boolean = startCell == endCell
    fun isMultiCellRange(): Boolean = !isSingleCellRange()

    companion object {
        private val SHEET_NOTATION_REGEX = """^([A-Z]+[1-9][0-9]*):([A-Z]+[1-9][0-9]*)$""".toRegex()

        fun fromSheetCells(cornerCellOne: SheetCell, cornerCellTwo: SheetCell): SheetRange {
            val topLeftCellColIndex = min(cornerCellOne.colIndex, cornerCellTwo.colIndex)
            val topLeftCellRowIndex = min(cornerCellOne.rowIndex, cornerCellTwo.rowIndex)
            val topLeftCell = SheetCell.fromIndexes(topLeftCellColIndex, topLeftCellRowIndex)

            val bottomRightCellColIndex = max(cornerCellOne.colIndex, cornerCellTwo.colIndex)
            val bottomRightCellRowIndex = max(cornerCellOne.rowIndex, cornerCellTwo.rowIndex)
            val bottomRightCell = SheetCell.fromIndexes(bottomRightCellColIndex, bottomRightCellRowIndex)

            return SheetRange(topLeftCell, bottomRightCell)
        }

        fun fromSheetNotation(notation: String): SheetRange {
            val matchResult = SHEET_NOTATION_REGEX.find(notation.uppercase())
            val (startCellNotation, endCellNotation) = matchResult!!.destructured
            val startCell = SheetCell.fromSheetNotation(startCellNotation)
            val endCell = SheetCell.fromSheetNotation(endCellNotation)
            return fromSheetCells(startCell, endCell)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SheetRange

        if (startCell != other.startCell) return false
        if (endCell != other.endCell) return false

        return true
    }

    override fun hashCode(): Int {
        var result = startCell.hashCode()
        result = 31 * result + endCell.hashCode()
        return result
    }

    override fun toString(): String {
        return toSheetNotation()
    }
}
