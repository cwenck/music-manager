package api.google.sheets.data

import api.google.sheets.SheetCell
import api.google.sheets.formatting.SheetCellFormat

data class SheetCellData(
    val cell: SheetCell,
    val value: SheetCellDataValue = SheetCellDataValue.emptyValue(),
    val format: SheetCellFormat? = null,
)

//sealed interface SheetCellData {
//    val cell: SheetCell
//    val value: SheetCellDataValue
//    val format: SheetCellFormat?
//
//    companion object {
//        fun of(
//            cell: SheetCell,
//            value: SheetCellDataValue = SheetCellDataValue.emptyValue(),
//            format: SheetCellFormat? = null,
//        ): SheetCellData =
//            if (format == null) {
//                SheetCellDataWithoutFormat(cell, value)
//            } else {
//                SheetCellDataWithFormat(cell, value, format)
//            }
//    }
//}
//
//data class SheetCellDataWithFormat(
//    override val cell: SheetCell,
//    override val value: SheetCellDataValue,
//    override val format: SheetCellFormat,
//) : SheetCellData {
//
//    companion object {
//        fun of(
//            cell: SheetCell,
//            value: SheetCellDataValue = SheetCellDataValue.emptyValue(),
//            format: SheetCellFormat? = null,
//        ): SheetCellData =
//            if (format == null) {
//                SheetCellDataWithoutFormat(cell, value)
//            } else {
//                SheetCellDataWithFormat(cell, value, format)
//            }
//    }
//}
//
//data class SheetCellDataWithoutFormat(
//    override val cell: SheetCell,
//    override val value: SheetCellDataValue,
//) : SheetCellData {
//    override val format: SheetCellFormat? = null
//}
