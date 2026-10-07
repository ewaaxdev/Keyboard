package com.ewaax.keyboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button

// Layar pengaturan minimal. Tugasnya cuma 2: membukakan halaman
// pengaturan keyboard sistem, dan memunculkan pemilih keyboard.
// Android tidak mengizinkan aplikasi mengaktifkan keyboard sendiri
// (alasan keamanan), jadi user tetap menekan tombolnya manual.
class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Tombol 1: lompat ke daftar keyboard di Pengaturan HP.
        findViewById<Button>(R.id.btn_open_settings).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }

        // Tombol 2: tampilkan dialog "pilih keyboard" jika keyboard
        // ini sedang dipakai (misal saat mengetik).
        findViewById<Button>(R.id.btn_show_picker).setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showInputMethodPicker()
        }
    }
}
