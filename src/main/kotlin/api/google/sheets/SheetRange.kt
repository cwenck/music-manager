package api.google.sheets

import kotlin.math.max
import kotlin.math.min

class SheetRange constructor(
    val startCell: SheetCell,
    val endCell: SheetCell,
) {
    private val colIndexRange = startCell.col.index..endCell.col.index
    private val rowIndexRange = startCell.row.index..endCell.row.index

    val columns = (endCell.col.index - startCell.col.index) + 1
    val rows = (endCell.row.index - startCell.row.index) + 1

    fun rowCellsSequence(row: SheetDimension): Sequence<SheetCell> {
        if (!containsRow(row)) throw IllegalArgumentException("Row outside of range")
        return generateSequence(startCell.atRow(row)) { cell ->
            if (cell.col.index < endCell.col.index) {
                cell.plusColumns(1)
            } else {
                null
            }
        }
    }

    fun columnCellsSequence(col: SheetDimension): Sequence<SheetCell> {
        if (!containsColumn(col)) throw IllegalArgumentException("Column outside of range")
        return generateSequence(startCell.atColumn(col)) { cell ->
            if (cell.row.index < endCell.row.index) {
                cell.plusRows(1)
            } else {
                null
            }
        }
    }

    fun rowsSequence(): Sequence<SheetDimension> = generateSequence(startCell.row) { dim ->
        if (dim < endCell.row) {
            dim + 1
        } else {
            null
        }
    }


    fun columnsSequence(): Sequence<SheetDimension> = generateSequence(startCell.col) { dim ->
        if (dim < endCell.col) {
            dim + 1
        } else {
            null
        }
    }

//    fun rowByRowSequence(): Sequence<SheetCell> = generateSequence(startCell) { cell ->
//        if (cell.row.index < endCell.row.index) {
//            cell.plusRows(1)
//        } else if (cell.col.index < endCell.col.index) {
//            SheetCell.fromIndexes(cell.col.index + 1, startCell.row.index)
//        } else {
//            null
//        }
//    }
//
//    fun columnByColumnSequence(): Sequence<SheetCell> = generateSequence(startCell) { cell ->
//        if (cell.col.index < endCell.col.index) {
//            cell.plusColumns(1)
//        } else if (cell.row.index < endCell.row.index) {
//            SheetCell.fromIndexes(startCell.col.index, cell.row.index + 1)
//        } else {
//            null
//        }
//    }

    operator fun contains(cell: SheetCell): Boolean =
        cell.col.index in colIndexRange && cell.row.index in rowIndexRange

    fun containsRow(row: SheetDimension): Boolean = row.index in rowIndexRange
    fun containsColumn(col: SheetDimension): Boolean = col.index in colIndexRange

    fun cellAtRelativeIndex(colIndex: Int, rowIndex: Int): SheetCell =
        startCell.plusColumns(colIndex).plusRows(rowIndex)

    fun getRelativeRowIndex(cell: SheetCell): Int = cell.row.index - startCell.row.index
    fun getRelativeColumnIndex(cell: SheetCell): Int = cell.col.index - startCell.col.index

    fun getRelativeRowIndex(rowIndex: Int): Int = rowIndex - startCell.row.index
    fun getRelativeColumnIndex(colIndex: Int): Int = colIndex - startCell.col.index

    fun getAbsoluteRowIndex(relativeRowIndex: Int): Int = relativeRowIndex + startCell.row.index
    fun getAbsoluteColumnIndex(relativeColIndex: Int): Int = relativeColIndex + startCell.col.index

    fun getRelativeCell(relativeColIndex: Int, relativeRowIndex: Int): SheetCell {
        val colIndex = getAbsoluteColumnIndex(relativeColIndex)
        val rowIndex = getAbsoluteRowIndex(relativeRowIndex)

        if (colIndex !in colIndexRange) throw IndexOutOfBoundsException("Cell is outside this range")
        if (rowIndex !in rowIndexRange) throw IndexOutOfBoundsException("Cell is outside this range")

        return SheetCell.fromIndexes(colIndex, rowIndex)
    }

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
            val topLeftCellColIndex = min(cornerCellOne.col.index, cornerCellTwo.col.index)
            val topLeftCellRowIndex = min(cornerCellOne.row.index, cornerCellTwo.row.index)
            val topLeftCell = SheetCell.fromIndexes(topLeftCellColIndex, topLeftCellRowIndex)

            val bottomRightCellColIndex = max(cornerCellOne.col.index, cornerCellTwo.col.index)
            val bottomRightCellRowIndex = max(cornerCellOne.row.index, cornerCellTwo.row.index)
            val bottomRightCell = SheetCell.fromIndexes(bottomRightCellColIndex, bottomRightCellRowIndex)

            return SheetRange(topLeftCell, bottomRightCell)
        }

        fun fromSheetNotation(notation: String): SheetRange {
            val matchResult = SHEET_NOTATION_REGEX.find(notation.uppercase())!!
            val (startCellNotation, endCellNotation) = matchResult.destructured
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
