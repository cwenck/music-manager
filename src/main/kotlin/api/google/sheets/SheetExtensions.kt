package api.google.sheets

fun String.toSheetCell() = SheetCell.fromSheetNotation(this)
fun String.toSheetRange() = SheetRange.fromSheetNotation(this)
