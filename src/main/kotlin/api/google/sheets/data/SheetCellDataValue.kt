package api.google.sheets.data

import com.google.api.services.sheets.v4.model.ExtendedValue

// TODO : Rename to SheetCellValue
sealed interface SheetCellDataValue {
    fun toExtendedValue(): ExtendedValue

    companion object {
        fun ofNumber(number: Double): SheetCellDataValueNumber = SheetCellDataValueNumber(number)
        fun ofString(string: String): SheetCellDataValueString = SheetCellDataValueString(string)
        fun ofBoolean(bool: Boolean): SheetCellDataValueBoolean = SheetCellDataValueBoolean(bool)
        fun ofFormula(formula: String): SheetCellDataValueFormula = SheetCellDataValueFormula(formula)
        fun emptyValue(): SheetCellDataValueEmpty = SheetCellDataValueEmpty
    }

    data class SheetCellDataValueNumber(val value: Double) : SheetCellDataValue {
        override fun toExtendedValue(): ExtendedValue = ExtendedValue().setNumberValue(value)
    }

    data class SheetCellDataValueString(val value: String) : SheetCellDataValue {
        override fun toExtendedValue(): ExtendedValue = ExtendedValue().setStringValue(value)
    }

    data class SheetCellDataValueBoolean(val value: Boolean) : SheetCellDataValue {
        override fun toExtendedValue(): ExtendedValue = ExtendedValue().setBoolValue(value)
    }

    data class SheetCellDataValueFormula(val value: String) : SheetCellDataValue {
        override fun toExtendedValue(): ExtendedValue = ExtendedValue().setFormulaValue(value)
    }

    object SheetCellDataValueEmpty : SheetCellDataValue {
        override fun toExtendedValue(): ExtendedValue = ExtendedValue()
    }
}
