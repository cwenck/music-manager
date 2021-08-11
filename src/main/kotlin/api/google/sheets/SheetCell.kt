package api.google.sheets

import api.google.sheets.SheetDimension.Companion.compareTo
import api.google.sheets.SheetDimension.Companion.fromIndex
import api.google.sheets.SheetDimension.Companion.fromName
import api.google.sheets.SheetDimension.Companion.fromNumber
import com.google.api.services.sheets.v4.model.GridRange

class SheetCell private constructor(val col: SheetDimension, val row: SheetDimension) {
    fun toSheetNotation() = "${col.name}${row.number}"
    fun toRowColumnNotation() = "R${row.number}C${col.number}"

    fun atRow(row: SheetDimension): SheetCell = SheetCell(this.col, row)
    fun atColumn(col: SheetDimension): SheetCell = SheetCell(col, this.row)

    fun plusRows(count: Int): SheetCell {
        if (count == 0) return this
        if (count < 0) throw IllegalArgumentException("Count must be non-negative")
        return SheetCell(col, row + count)
    }

    fun plusColumns(count: Int): SheetCell {
        if (count == 0) return this
        if (count < 0) throw IllegalArgumentException("Count must be non-negative")
        return SheetCell(col + count, row)
    }

    fun minusRows(count: Int): SheetCell {
        if (count == 0) return this
        if (count < 0) throw IllegalArgumentException("Count must be non-negative")
        if (count > row) throw IllegalArgumentException("Count cannot exceed the row index")
        return SheetCell(col, row - count)
    }

    fun minusColumns(count: Int): SheetCell {
        if (count == 0) return this
        if (count < 0) throw IllegalArgumentException("Count must be non-negative")
        if (count > col) throw IllegalArgumentException("Count cannot exceed the column index")
        return SheetCell(col - count, row)
    }

    fun toGridRange(worksheet: Worksheet): GridRange = GridRange()
        .setSheetId(worksheet.id)
        .setStartRowIndex(row.index)
        .setEndRowIndex(row.index + 1)
        .setStartColumnIndex(col.index)
        .setEndColumnIndex(col.index + 1)


    companion object {
        private val SHEET_NOTATION_REGEX = """^([A-Z]+)([1-9][0-9]*)$""".toRegex()

        fun fromIndexes(colIndex: Int, rowIndex: Int) = SheetCell(fromIndex(colIndex), fromIndex(rowIndex))
        fun fromDimensions(col: SheetDimension, row: SheetDimension) = SheetCell(col, row)
        fun fromSheetNotation(notation: String): SheetCell {
            val matchResult = SHEET_NOTATION_REGEX.find(notation.uppercase())!!
            val (columnName, rowNumberText) = matchResult.destructured
            val rowNumber = rowNumberText.toInt()
            return SheetCell(fromName(columnName), fromNumber(rowNumber))
        }
    }


    override fun toString(): String {
        return toSheetNotation()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SheetCell

        if (col != other.col) return false
        if (row != other.row) return false

        return true
    }

    override fun hashCode(): Int {
        var result = col.hashCode()
        result = 31 * result + row.hashCode()
        return result
    }
}
