package com.ewaax.keyboard

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RowLayoutTest {

    @Test
    fun lettersHaveFourRowsWithCorrectCounts() {
        val rows = buildRows(Layer.LETTERS)
        assertEquals(4, rows.size)
        assertEquals(10, rows[0].keys.size)
        assertEquals(9, rows[1].keys.size)
        assertEquals(9, rows[2].keys.size)
    }

    @Test
    fun secondRowIsCentered() {
        val rows = buildRows(Layer.LETTERS)
        assertTrue(rows[1].centered)
    }

    @Test
    fun letterRowThreeStartsWithShiftEndsWithDelete() {
        val row = buildRows(Layer.LETTERS)[2].keys
        assertEquals(KeyAction.SHIFT, row.first().action)
        assertEquals(IconKind.SHIFT, row.first().icon)
        assertEquals(KeyAction.DELETE, row.last().action)
        assertEquals(IconKind.DELETE, row.last().icon)
    }

    @Test
    fun letterRowWeightsFillTenUnits() {
        val rows = buildRows(Layer.LETTERS)
        assertEquals(10f, rows[0].keys.sumOf { it.weight.toDouble() }.toFloat(), 0.01f)
    }

    @Test
    fun symbolsFirstRowIsDigits() {
        val row = buildRows(Layer.SYMBOLS)[0].keys
        assertEquals(10, row.size)
        assertEquals("1", row.first().text)
        assertEquals("0", row.last().text)
    }

    @Test
    fun moreLayerHasToggleBackToSymbols() {
        val page = buildRows(Layer.MORE).flatMap { it.keys }
        val toggle = page.first { it.text == "123" }
        assertEquals(KeyAction.TO_SYMBOLS, toggle.action)
    }

    @Test
    fun symbolsLeftKeyReturnsToLetters() {
        val page = buildRows(Layer.SYMBOLS, BottomRow.COMPACT).flatMap { it.keys }
        val back = page.first { it.text == "ABC" }
        assertEquals(KeyAction.TO_LETTERS, back.action)
    }

    @Test
    fun emailBottomRowHasAtAndDot() {
        val bottom = buildRows(Layer.LETTERS, BottomRow.EMAIL).last().keys
        assertTrue(bottom.any { it.text == "@" })
        assertTrue(bottom.any { it.text == "." })
    }

    @Test
    fun urlBottomRowHasDotCom() {
        val bottom = buildRows(Layer.LETTERS, BottomRow.URL).last().keys
        assertTrue(bottom.any { it.text == ".com" })
    }

    @Test
    fun numpadHasFourRowsEndingWithEnter() {
        val rows = buildRows(Layer.NUMPAD)
        assertEquals(4, rows.size)
        assertEquals(3, rows[0].keys.size)
        assertEquals(3, rows[1].keys.size)
        assertEquals(3, rows[2].keys.size)
        assertEquals(4, rows[3].keys.size)
        assertEquals(KeyAction.ENTER, rows[3].keys.last().action)
    }
}
