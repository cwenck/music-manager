package api.google.sheets

import com.google.common.math.IntMath.pow

class SheetCell private constructor(val colIndex: Int, val rowIndex: Int) {
    fun toSheetNotation() = "${indexToName(colIndex)}${indexToNumberText(rowIndex)}"
    fun toRowColumnNotation() = "R${indexToNumberText(rowIndex)}C${indexToNumberText(colIndex)}"

    companion object {
        private val SHEET_NOTATION_REGEX = """^([A-Z]+)([1-9][0-9]*)$""".toRegex()

        fun fromIndexes(col: Int, row: Int) = SheetCell(col, row)
        fun fromSheetNotation(notation: String): SheetCell {
            val matchResult = SHEET_NOTATION_REGEX.find(notation.uppercase())
            val (columnName, rowNumberText) = matchResult!!.destructured
            val rowNumber = rowNumberText.toInt()
            return SheetCell(nameToIndex(columnName), numberToIndex(rowNumber))
        }

        private fun indexToName(index: Int): String {
            var dividend = index + 1
            var result = ""

            while (dividend > 0) {
                val remainder = (dividend - 1) % 26
                result = ('A' + remainder) + result
                dividend = (dividend - remainder) / 26
            }

            return result
        }

        private fun nameToIndex(name: String): Int {
            var result = 0
            val normalizedName = name.uppercase()
            for (i: Int in normalizedName.indices) {
                val digitPosition = (normalizedName.length - i - 1)
                val digit = (normalizedName[i] - 'A') + 1
                result += digit * pow(26, digitPosition)
            }

            return result - 1
        }

        private fun indexToNumberText(index: Int): String = "${index + 1}"
        private fun numberToIndex(number: Int): Int = number - 1
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SheetCell

        if (colIndex != other.colIndex) return false
        if (rowIndex != other.rowIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colIndex
        result = 31 * result + rowIndex
        return result
    }
}
