package com.ewaax.keyboard

import android.content.res.Configuration
import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View

// Service keyboard: menampung KeyboardView dan menyambungkannya ke kolom teks.
// Fase 1: tema ikut sistem, shift reset tiap kolom baru disentuh.
class SimpleKeyboardService : InputMethodService(), ImeHost {

    private var keyboardView: KeyboardView? = null

    override fun onCreateInputView(): View {
        val view = KeyboardView(this)
        view.host = this
        view.applyTheme(KeyboardThemes.resolve(this))
        keyboardView = view
        return view
    }

    override fun onStartInputView(info: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        keyboardView?.resetShift()
        keyboardView?.applyTheme(KeyboardThemes.resolve(this))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Putar HP / ganti tema sistem: terapkan ulang tema + ukur ulang.
        keyboardView?.applyTheme(KeyboardThemes.resolve(this))
        keyboardView?.requestLayout()
    }

    override fun typeText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    override fun deleteBeforeCursor(count: Int) {
        currentInputConnection?.deleteSurroundingText(count, 0)
    }

    override fun pressEnter() {
        val ic = currentInputConnection ?: return
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER))
    }
}
