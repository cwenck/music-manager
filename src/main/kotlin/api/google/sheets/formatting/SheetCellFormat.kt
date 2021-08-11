package api.google.sheets.formatting

import com.google.api.services.sheets.v4.model.CellFormat
import com.google.api.services.sheets.v4.model.TextFormat

data class SheetCellFormat(
    val fontSize: Int? = null,
    val fontFamily: String? = null,
    val bold: Boolean? = null,
    val italic: Boolean? = null,
    val underline: Boolean? = null,
    val strikethrough: Boolean? = null,
    val fontColor: SheetColor? = null,
    val backgroundColor: SheetColor? = null,
    val horizontalAlignment: SheetHorizontalAlignment? = null,
    val verticalAlignment: SheetVerticalAlignment? = null,
) {

    fun getFields(): List<String> {
        val textFormatFields: MutableList<String> = mutableListOf()
        if (fontSize != null) textFormatFields.add("fontSize")
        if (fontFamily != null) textFormatFields.add("fontFamily")
        if (bold != null) textFormatFields.add("bold")
        if (italic != null) textFormatFields.add("italic")
        if (underline != null) textFormatFields.add("underline")
        if (strikethrough != null) textFormatFields.add("strikethrough")
        if (fontColor != null) textFormatFields.add("foregroundColor")

        val cellFormatFields: MutableList<String> = mutableListOf()
        cellFormatFields.addAll(textFormatFields.map { field -> "textFormat.$field" })
        if (horizontalAlignment != null) cellFormatFields.add("horizontalAlignment")
        if (verticalAlignment != null) cellFormatFields.add("verticalAlignment")
        if (backgroundColor != null) cellFormatFields.add("backgroundColor")

        return cellFormatFields
    }

    fun toCellFormat(): CellFormat {
        val textFormat = TextFormat()
            .setFontSize(fontSize)
            .setFontFamily(fontFamily)
            .setBold(bold)
            .setItalic(italic)
            .setUnderline(underline)
            .setStrikethrough(strikethrough)
            .setForegroundColor(fontColor?.toColor())

        return CellFormat()
            .setTextFormat(textFormat)
            .setHorizontalAlignment(horizontalAlignment?.alignment)
            .setVerticalAlignment(verticalAlignment?.alignment)
            .setBackgroundColor(backgroundColor?.toColor())
    }
}
