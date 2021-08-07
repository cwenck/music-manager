package api.google.sheets

import api.google.sheets.SheetDimension.Companion.compareTo
import api.google.sheets.SheetDimension.Companion.fromIndex
import api.google.sheets.SheetDimension.Companion.fromName
import api.google.sheets.SheetDimension.Companion.fromNumber

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

    companion object {
        private val SHEET_NOTATION_REGEX = """^([A-Z]+)([1-9][0-9]*)$""".toRegex()

        fun fromIndexes(colIndex: Int, rowIndex: Int) = SheetCell(fromIndex(colIndex), fromIndex(rowIndex))
        fun fromSheetNotation(notation: String): SheetCell {
            val matchResult = SHEET_NOTATION_REGEX.find(notation.uppercase())
            val (columnName, rowNumberText) = matchResult!!.destructured
            val rowNumber = rowNumberText.toInt()
            return SheetCell(fromName(columnName), fromNumber(rowNumber))
        }

//        private fun indexToName(index: Int): String {
//            var dividend = index + 1
//            var result = ""
//
//            while (dividend > 0) {
//                val remainder = (dividend - 1) % 26
//                result = ('A' + remainder) + result
//                dividend = (dividend - remainder) / 26
//            }
//
//            return result
//        }
//
//        private fun nameToIndex(name: String): Int {
//            var result = 0
//            val normalizedName = name.uppercase()
//            for (i: Int in normalizedName.indices) {
//                val digitPosition = (normalizedName.length - i - 1)
//                val digit = (normalizedName[i] - 'A') + 1
//                result += digit * pow(26, digitPosition)
//            }
//
//            return result - 1
//        }
//
//        private fun indexToNumberText(index: Int): String = "${index + 1}"
//        private fun numberToIndex(number: Int): Int = number - 1
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
