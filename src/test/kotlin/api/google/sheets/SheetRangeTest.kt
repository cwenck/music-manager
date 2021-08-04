package api.google.sheets

import org.testng.Assert.assertEquals
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class SheetRangeTest {

    @DataProvider
    fun fromSheetCellsData(): Array<Array<Any>> = arrayOf(
        arrayOf(
            SheetRange.fromSheetCells(SheetCell.fromSheetNotation("B2"), SheetCell.fromSheetNotation("A1")),
            SheetCell.fromSheetNotation("A1"),
            SheetCell.fromSheetNotation("B2")
        ),
        arrayOf(
            SheetRange.fromSheetCells(SheetCell.fromSheetNotation("A1"), SheetCell.fromSheetNotation("A1")),
            SheetCell.fromSheetNotation("A1"),
            SheetCell.fromSheetNotation("A1")
        ),
        arrayOf(
            SheetRange.fromSheetCells(SheetCell.fromSheetNotation("A3"), SheetCell.fromSheetNotation("C1")),
            SheetCell.fromSheetNotation("A1"),
            SheetCell.fromSheetNotation("C3")
        ),
    )

    @Test(dataProvider = "fromSheetCellsData")
    fun testFromSheetCellsData(range: SheetRange, expectedRangeStart: SheetCell, expectedRangeEnd: SheetCell) {
        assertEquals(range.rangeStart, expectedRangeStart)
        assertEquals(range.rangeEnd, expectedRangeEnd)
    }
}
