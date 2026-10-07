package com.ewaax.keyboard

import org.junit.Assert.assertEquals
import org.junit.Test

class ShiftStateTest {

    @Test
    fun tapCyclesOffOnceLockedOff() {
        assertEquals(ShiftState.ONCE, tapShift(ShiftState.OFF))
        assertEquals(ShiftState.LOCKED, tapShift(ShiftState.ONCE))
        assertEquals(ShiftState.OFF, tapShift(ShiftState.LOCKED))
    }

    @Test
    fun letterConsumesOnceOnly() {
        assertEquals(ShiftState.OFF, consumeShiftOnLetter(ShiftState.ONCE))
        assertEquals(ShiftState.LOCKED, consumeShiftOnLetter(ShiftState.LOCKED))
        assertEquals(ShiftState.OFF, consumeShiftOnLetter(ShiftState.OFF))
    }
}
