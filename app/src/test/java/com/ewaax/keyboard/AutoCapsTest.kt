package com.ewaax.keyboard

import android.text.InputType
import org.junit.Assert.assertEquals
import org.junit.Test

class AutoCapsTest {

    private val sentences = InputType.TYPE_CLASS_TEXT or
        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES

    @Test
    fun emptyFieldIsSentenceStart() {
        assertEquals(ShiftState.ONCE, desiredShift("", sentences, true))
    }

    @Test
    fun afterSentenceEndIsSentenceStart() {
        assertEquals(ShiftState.ONCE, desiredShift("Halo. ", sentences, true))
        assertEquals(ShiftState.ONCE, desiredShift("Wah! ", sentences, true))
        assertEquals(ShiftState.ONCE, desiredShift("Apa? ", sentences, true))
        assertEquals(ShiftState.ONCE, desiredShift("Baris\n", sentences, true))
    }

    @Test
    fun midWordIsNotSentenceStart() {
        assertEquals(ShiftState.OFF, desiredShift("Hal", sentences, true))
        assertEquals(ShiftState.OFF, desiredShift("Halo.ok", sentences, true))
    }

    @Test
    fun disabledStaysOff() {
        assertEquals(ShiftState.OFF, desiredShift("", sentences, false))
    }

    @Test
    fun nonTextClassStaysOff() {
        val number = InputType.TYPE_CLASS_NUMBER
        assertEquals(ShiftState.OFF, desiredShift("", number, true))
    }

    @Test
    fun passwordStaysOff() {
        val password = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        assertEquals(ShiftState.OFF, desiredShift("", password, true))
    }

    @Test
    fun capCharactersLocks() {
        val caps = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
        assertEquals(ShiftState.LOCKED, desiredShift("abc", caps, true))
    }

    @Test
    fun capWordsOnlyAtWordStart() {
        val words = InputType.TYPE_CLASS_TEXT or
            InputType.TYPE_TEXT_FLAG_CAP_WORDS
        assertEquals(ShiftState.ONCE, desiredShift("Halo ", words, true))
        assertEquals(ShiftState.OFF, desiredShift("Halo", words, true))
    }
}
