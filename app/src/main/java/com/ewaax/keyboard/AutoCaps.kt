package com.ewaax.keyboard

import android.text.InputType

// Tentukan status shift awal saat kolom input disentuh.
// Murni (hanya memakai konstanta Int) agar bisa di-unit-test di JVM biasa.
fun desiredShift(
    textBeforeCursor: String,
    inputType: Int,
    autoCapsEnabled: Boolean
): ShiftState {
    if (!autoCapsEnabled) return ShiftState.OFF
    val inputClass = inputType and InputType.TYPE_MASK_CLASS
    if (inputClass != InputType.TYPE_CLASS_TEXT) return ShiftState.OFF
    val variation = inputType and InputType.TYPE_MASK_VARIATION
    if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD
        || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
    ) {
        return ShiftState.OFF
    }
    val flags = inputType and (
        InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS or
            InputType.TYPE_TEXT_FLAG_CAP_WORDS or
            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        )
    if (flags and InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS != 0) {
        return ShiftState.LOCKED
    }
    if (flags and InputType.TYPE_TEXT_FLAG_CAP_WORDS != 0) {
        return if (isWordStart(textBeforeCursor)) ShiftState.ONCE else ShiftState.OFF
    }
    // Tanpa flag (atau flag kalimat): kapital di awal dan setelah akhir kalimat.
    return if (isSentenceStart(textBeforeCursor)) ShiftState.ONCE else ShiftState.OFF
}

// Awal kata = kosong atau karakter terakhir spasi/baris baru.
private fun isWordStart(text: String): Boolean {
    if (text.isEmpty()) return true
    val last = text.last()
    return last == ' ' || last == '\n'
}

// Awal kalimat = kosong, baris baru, atau diakhiri ". ", "! ", "? ".
private fun isSentenceStart(text: String): Boolean {
    if (text.isEmpty()) return true
    if (text.endsWith("\n")) return true
    return text.endsWith(". ") || text.endsWith("! ") || text.endsWith("? ")
}
