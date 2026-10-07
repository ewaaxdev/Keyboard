package com.ewaax.keyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// Service keyboard. Android memanggil onCreateInputView() tiap kali
// keyboard perlu tampil, dan hasilnya dijadikan tampilan keyboard.
class SimpleKeyboardService : InputMethodService() {

    // false = huruf kecil, true = huruf besar (satu huruf, lalu balik kecil).
    private var isCaps = false
    private lateinit var shiftButton: Button

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)
        wireKeys(view)
        return view
    }

    // Menyusuri semua tombol di layout, lalu memasang aksi kliknya.
    // Tombol huruf dikenali dari tag 1 huruf, tombol khusus dari id.
    private fun wireKeys(root: View) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                wireKeys(root.getChildAt(i))
            }
            return
        }
        if (root !is Button) return
        when (root.id) {
            R.id.btn_shift -> {
                shiftButton = root
                root.setOnClickListener { toggleShift() }
            }
            R.id.btn_space -> root.setOnClickListener { typeText(" ") }
            R.id.btn_backspace -> root.setOnClickListener { backspace() }
            R.id.btn_enter -> root.setOnClickListener { pressEnter() }
            else -> {
                val tag = root.tag as? String
                if (tag != null && tag.length == 1 && tag[0].isLetter()) {
                    root.setOnClickListener { typeLetter(tag) }
                }
            }
        }
    }

    // Ketik 1 huruf. Kalau shift aktif, jadi kapital lalu shift mati lagi.
    private fun typeLetter(letter: String) {
        val text = if (isCaps) letter.uppercase() else letter
        typeText(text)
        if (isCaps) {
            isCaps = false
            updateShiftLabel()
        }
    }

    private fun typeText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    // Hapus 1 karakter di kiri kursor.
    private fun backspace() {
        currentInputConnection?.deleteSurroundingText(1, 0)
    }

    // Enter dikirim sebagai event tombol (seperti keyboard fisik),
    // supaya aplikasi tujuan (chat, form) merespons dengan benar.
    private fun pressEnter() {
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }

    private fun toggleShift() {
        isCaps = !isCaps
        updateShiftLabel()
    }

    private fun updateShiftLabel() {
        shiftButton.text = if (isCaps) "SHIFT" else "shift"
    }
}
