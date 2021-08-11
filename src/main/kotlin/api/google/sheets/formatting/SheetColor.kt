package api.google.sheets.formatting

import com.google.api.services.sheets.v4.model.Color

data class SheetColor(val red: Float = 0f, val green: Float = 0f, val blue: Float = 0f, val alpha: Float = 1f) {
    fun toColor(): Color = Color()
        .setRed(red)
        .setBlue(blue)
        .setGreen(green)
        .setAlpha(alpha)

    companion object {
        val TRANSPARENT = SheetColor(1f, 1f, 1f, 0f)
        val WHITE = SheetColor(1f, 1f, 1f)
        val BLACK = SheetColor(0f, 0f, 0f)
        val RED = SheetColor(1f, 0f, 0f)
        val GREEN = SheetColor(0f, 1f, 0f)
        val BLUE = SheetColor(0f, 0f, 1f)
    }
}
