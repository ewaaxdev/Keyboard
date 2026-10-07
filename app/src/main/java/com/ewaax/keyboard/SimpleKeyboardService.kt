package com.ewaax.keyboard

import android.inputmethodservice.InputMethodService
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button

// Service keyboard. Android memanggil onCreateInputView() tiap kali
// keyboard perlu tampil, dan hasilnya dijadikan tampilan keyboard.
class SimpleKeyboardService : InputMethodService() {

    // Shift satu ketuk ala iPhone: aktif untuk 1 huruf, lalu mati sendiri.
    private var isCaps = false
    private var shiftButton: Button? = null

    // 3 halaman: huruf, angka/simbol, simbol lanjutan.
    private var pageLetters: View? = null
    private var pageSymbols: View? = null
    private var pageMore: View? = null

    // Mesin hapus-berulang saat tombol hapus ditahan.
    private val deleteHandler = Handler(Looper.getMainLooper())
    private var deleting = false
    private val deleteRepeat = object : Runnable {
        override fun run() {
            backspace()
            if (deleting) deleteHandler.postDelayed(this, 50)
        }
    }

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_main, null)
        pageLetters = view.findViewById(R.id.page_letters)
        pageSymbols = view.findViewById(R.id.page_symbols)
        pageMore = view.findViewById(R.id.page_more)
        wireKeys(view)
        showPage(pageLetters)
        return view
    }

    // Tampilkan 1 halaman, sembunyikan 2 lainnya.
    private fun showPage(page: View?) {
        pageLetters?.visibility = if (page == pageLetters) View.VISIBLE else View.GONE
        pageSymbols?.visibility = if (page == pageSymbols) View.VISIBLE else View.GONE
        pageMore?.visibility = if (page == pageMore) View.VISIBLE else View.GONE
    }

    // Menyusuri semua tombol di 3 halaman, lalu memasang aksinya.
    // Semua tombol dikenali dari tag: huruf/angka/simbol = tag 1 karakter,
    // tombol khusus = tag kata (shift, space, backspace, enter, to_*).
    private fun wireKeys(root: View) {
        if (root is ViewGroup) {
            for (i in 0 until root.childCount) {
                wireKeys(root.getChildAt(i))
            }
            return
        }
        if (root !is Button) return
        when (val tag = root.tag as? String) {
            "shift" -> {
                shiftButton = root
                root.setOnClickListener { toggleShift() }
            }
            "space" -> root.setOnClickListener { typeText(" ") }
            "backspace" -> wireBackspace(root)
            "enter" -> root.setOnClickListener { pressEnter() }
            "to_symbols" -> root.setOnClickListener { showPage(pageSymbols) }
            "to_letters" -> root.setOnClickListener { showPage(pageLetters) }
            "to_more" -> root.setOnClickListener { showPage(pageMore) }
            else -> {
                if (tag != null && tag.length == 1) {
                    if (tag[0].isLetter()) {
                        root.setOnClickListener { typeLetter(tag) }
                    } else {
                        root.setOnClickListener { typeText(tag) }
                    }
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
            updateShiftKey()
        }
    }

    private fun typeText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    // Hapus 1 karakter di kiri kursor.
    private fun backspace() {
        currentInputConnection?.deleteSurroundingText(1, 0)
    }

    // Ketuk = hapus 1x. Tahan = hapus terus tiap 50ms sampai dilepas.
    // onTouch mengembalikan false agar ketukan cepat tetap jadi klik biasa.
    private fun wireBackspace(button: Button) {
        button.setOnClickListener { backspace() }
        button.setOnLongClickListener {
            deleting = true
            deleteHandler.post(deleteRepeat)
            true
        }
        button.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP
                || event.action == MotionEvent.ACTION_CANCEL
            ) {
                deleting = false
                deleteHandler.removeCallbacks(deleteRepeat)
            }
            false
        }
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
        updateShiftKey()
    }

    // Shift menyala = latar gelap + teks putih, mati = kembali abu-abu.
    private fun updateShiftKey() {
        val button = shiftButton ?: return
        if (isCaps) {
            button.setBackgroundResource(R.drawable.key_function_active)
            button.setTextColor(0xFFFFFFFF.toInt())
        } else {
            button.setBackgroundResource(R.drawable.key_function)
            button.setTextColor(0xFF000000.toInt())
        }
    }
}
