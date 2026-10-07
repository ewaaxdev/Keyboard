package com.ewaax.keyboard

import android.content.Context
import android.content.res.Configuration

// Palet warna lengkap per tema. Angka ARGB ditulis langsung agar
// tidak perlu file colors.xml tambahan (APK tetap kecil).
data class ThemeColors(
    val background: Int,
    val letterKey: Int,
    val functionKey: Int,
    val activeKey: Int,
    val text: Int,
    val functionText: Int,
    val activeText: Int,
    val icon: Int,
    val pressedOverlay: Int,
    val previewBackground: Int,
    val previewText: Int
)

object KeyboardThemes {
    val LIGHT = ThemeColors(
        background = 0xFFD1D4DA.toInt(),
        letterKey = 0xFFFFFFFF.toInt(),
        functionKey = 0xFFACB0B9.toInt(),
        activeKey = 0xFF55585F.toInt(),
        text = 0xFF000000.toInt(),
        functionText = 0xFF000000.toInt(),
        activeText = 0xFFFFFFFF.toInt(),
        icon = 0xFF000000.toInt(),
        pressedOverlay = 0x33000000,
        previewBackground = 0xFFFFFFFF.toInt(),
        previewText = 0xFF000000.toInt()
    )

    val DARK = ThemeColors(
        background = 0xFF141416.toInt(),
        letterKey = 0xFF3F3F42.toInt(),
        functionKey = 0xFF262628.toInt(),
        activeKey = 0xFF55585F.toInt(),
        text = 0xFFFFFFFF.toInt(),
        functionText = 0xFFFFFFFF.toInt(),
        activeText = 0xFFFFFFFF.toInt(),
        icon = 0xFFFFFFFF.toInt(),
        pressedOverlay = 0x33FFFFFF,
        previewBackground = 0xFF3F3F42.toInt(),
        previewText = 0xFFFFFFFF.toInt()
    )

    // Pilihan tema: 0 = ikut sistem (default), 1 = selalu terang,
    // 2 = selalu gelap. Parameter pref disiapkan untuk Settings Fase 2.
    fun resolve(context: Context, pref: Int = 0): ThemeColors {
        if (pref == 1) return LIGHT
        if (pref == 2) return DARK
        val night = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
        return if (night == Configuration.UI_MODE_NIGHT_YES) DARK else LIGHT
    }
}
