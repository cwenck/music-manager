package api.google.sheets

import com.google.common.math.IntMath

class SheetDimension private constructor(val index: Int) {
    val number: Int = index + 1
    val name: String = indexToName(index)

    operator fun plus(count: Int): SheetDimension = fromIndex(index + count)
    operator fun minus(count: Int): SheetDimension = fromIndex(index - count)
    operator fun compareTo(dim: SheetDimension): Int = index.compareTo(dim.index)
    operator fun compareTo(value: Int): Int = index.compareTo(value)

    companion object {
        operator fun Int.compareTo(dimension: SheetDimension): Int = this.compareTo(dimension.index)

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
                result += digit * IntMath.pow(26, digitPosition)
            }

            return result - 1
        }

        private fun indexToNumberText(index: Int): String = "${index + 1}"
        private fun numberToIndex(number: Int): Int = number - 1

        fun fromIndex(index: Int): SheetDimension {
            if (index < 0) throw IllegalArgumentException("Index must be non-negative")
            return SheetDimension(index)
        }

        fun fromNumber(number: Int): SheetDimension {
            if (number < 1) throw IllegalArgumentException("Number must be at least one")
            return SheetDimension(numberToIndex(number))
        }

        fun fromName(name: String): SheetDimension = SheetDimension(nameToIndex(name))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SheetDimension

        if (index != other.index) return false
        if (number != other.number) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + number
        result = 31 * result + name.hashCode()
        return result
    }
}
