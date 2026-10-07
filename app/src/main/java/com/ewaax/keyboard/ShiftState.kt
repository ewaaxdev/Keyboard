package com.ewaax.keyboard

// Mesin status shift: mati -> sekali ketuk -> kunci kapital -> mati.
// Murni (tanpa Android) agar bisa di-unit-test.
enum class ShiftState { OFF, ONCE, LOCKED }

// Ketuk tombol shift: OFF -> ONCE -> LOCKED -> OFF.
fun tapShift(state: ShiftState): ShiftState {
    return when (state) {
        ShiftState.OFF -> ShiftState.ONCE
        ShiftState.ONCE -> ShiftState.LOCKED
        ShiftState.LOCKED -> ShiftState.OFF
    }
}

// Setelah 1 huruf diketik: ONCE kembali OFF, LOCKED dan OFF tetap.
fun consumeShiftOnLetter(state: ShiftState): ShiftState {
    return if (state == ShiftState.ONCE) ShiftState.OFF else state
}
