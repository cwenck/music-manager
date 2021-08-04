package api.google.sheets

import org.testng.Assert.assertEquals
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

class SheetCellTest {

    @DataProvider
    fun toSheetNotationData(): Array<Array<Any>> = arrayOf(
        arrayOf(SheetCell.fromIndexes(0, 0), "A1"),
        arrayOf(SheetCell.fromIndexes(0, 1), "A2"),
        arrayOf(SheetCell.fromIndexes(1, 0), "B1"),
        arrayOf(SheetCell.fromIndexes(25, 0), "Z1"),
        arrayOf(SheetCell.fromIndexes(26, 0), "AA1"),
        arrayOf(SheetCell.fromIndexes(27, 0), "AB1"),
        arrayOf(SheetCell.fromIndexes(52, 0), "BA1"),
    )

    @Test(dataProvider = "toSheetNotationData")
    fun testToSheetNotation(cell: SheetCell, expectedSheetNotation: String) {
        val actualSheetNotation = cell.toSheetNotation()
        assertEquals(actualSheetNotation, expectedSheetNotation)
    }

    @DataProvider
    fun toRowColumnNotationData(): Array<Array<Any>> = arrayOf(
        arrayOf(SheetCell.fromIndexes(0, 0), "R1C1"),
        arrayOf(SheetCell.fromIndexes(0, 1), "R2C1"),
        arrayOf(SheetCell.fromIndexes(1, 0), "R1C2"),
        arrayOf(SheetCell.fromIndexes(25, 0), "R1C26"),
        arrayOf(SheetCell.fromIndexes(26, 0), "R1C27"),
        arrayOf(SheetCell.fromIndexes(52, 0), "R1C53"),
    )

    @Test(dataProvider = "toRowColumnNotationData")
    fun testToRowColumnNotation(cell: SheetCell, expectedRowColumnNotation: String) {
        val actualSheetNotation = cell.toRowColumnNotation()
        assertEquals(actualSheetNotation, expectedRowColumnNotation)
    }

    @DataProvider
    fun fromIndexesData(): Array<Array<Any>> = arrayOf(
        arrayOf(0, 0),
        arrayOf(0, 1),
        arrayOf(1, 0),
        arrayOf(1, 1),
        arrayOf(18, 15),
    )

    @Test(dataProvider = "fromIndexesData")
    fun testFromIndexes(colIndex: Int, rowIndex: Int) {
        val actualSheetCell = SheetCell.fromIndexes(colIndex, rowIndex)
        assertEquals(actualSheetCell.colIndex, colIndex)
        assertEquals(actualSheetCell.rowIndex, rowIndex)
    }

    @DataProvider
    fun fromSheetNotation(): Array<Array<Any>> = arrayOf(
        arrayOf("A1", 0, 0),
        arrayOf("A2", 0, 1),
        arrayOf("B1", 1, 0),
        arrayOf("Z1", 25, 0),
        arrayOf("AA1", 26, 0),
        arrayOf("AB1", 27, 0),
        arrayOf("BA1", 52, 0),
    )

    @Test(dataProvider = "fromSheetNotation")
    fun testFromSheetNotation(sheetNotation: String, expectedColIndex: Int, expectedRowIndex: Int) {
        val actualSheetCell = SheetCell.fromSheetNotation(sheetNotation)
        assertEquals(actualSheetCell.colIndex, expectedColIndex)
        assertEquals(actualSheetCell.rowIndex, expectedRowIndex)
    }
}
