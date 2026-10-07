package com.ewaax.keyboard

// Model tombol murni (tanpa Android): 1 tombol = teks/ikon + aksi + lebar.
enum class IconKind {
    SHIFT, SHIFT_LOCKED, DELETE, GLOBE, MIC, ENTER
}

enum class KeyAction {
    CHAR, SHIFT, DELETE, SPACE, ENTER,
    TO_SYMBOLS, TO_MORE, TO_LETTERS, GLOBE, MIC
}

data class Key(
    val text: String,
    val action: KeyAction,
    val icon: IconKind?,
    val weight: Float,
    val function: Boolean
)

enum class Layer { LETTERS, SYMBOLS, MORE, NUMPAD }

// Bentuk baris bawah. SIMPLE dipakai Fase 1 (tanpa tombol pindah yang
// belum ada halamannya); COMPACT mulai Fase 2; sisanya mulai Fase 4.
enum class BottomRow { SIMPLE, COMPACT, NORMAL, EMAIL, URL }

data class KeyRow(val keys: List<Key>, val centered: Boolean = false)

private fun charKey(text: String, weight: Float = 1f) =
    Key(text, KeyAction.CHAR, null, weight, false)

private fun functionIcon(icon: IconKind, action: KeyAction, weight: Float) =
    Key("", action, icon, weight, true)

private fun functionText(text: String, action: KeyAction, weight: Float) =
    Key(text, action, null, weight, true)

// Susun baris tombol per layer. Fungsi murni: mudah di-unit-test.
fun buildRows(layer: Layer, bottom: BottomRow = BottomRow.SIMPLE): List<KeyRow> {
    return when (layer) {
        Layer.LETTERS -> letterRows(bottom)
        Layer.SYMBOLS -> symbolRows(bottom, first = false)
        Layer.MORE -> symbolRows(bottom, first = true)
        Layer.NUMPAD -> numpadRows()
    }
}

private fun bottomKeys(bottom: BottomRow): List<Key> {
    return when (bottom) {
        BottomRow.SIMPLE -> listOf(
            Key("Space", KeyAction.SPACE, null, 6f, false),
            Key("return", KeyAction.ENTER, IconKind.ENTER, 2f, true)
        )
        BottomRow.COMPACT -> listOf(
            functionText("123", KeyAction.TO_SYMBOLS, 1.5f),
            Key("Space", KeyAction.SPACE, null, 5f, false),
            Key("return", KeyAction.ENTER, IconKind.ENTER, 1.5f, true)
        )
        BottomRow.NORMAL -> listOf(
            functionText("123", KeyAction.TO_SYMBOLS, 1.2f),
            functionIcon(IconKind.GLOBE, KeyAction.GLOBE, 1f),
            functionIcon(IconKind.MIC, KeyAction.MIC, 1f),
            Key("Space", KeyAction.SPACE, null, 3.6f, false),
            Key("return", KeyAction.ENTER, IconKind.ENTER, 1.2f, true)
        )
        BottomRow.EMAIL -> listOf(
            functionText("123", KeyAction.TO_SYMBOLS, 1.2f),
            Key("@", KeyAction.CHAR, null, 1f, true),
            Key("Space", KeyAction.SPACE, null, 3.6f, false),
            Key(".", KeyAction.CHAR, null, 1f, true),
            Key("return", KeyAction.ENTER, IconKind.ENTER, 1.2f, true)
        )
        BottomRow.URL -> listOf(
            functionText("123", KeyAction.TO_SYMBOLS, 1.2f),
            Key(".", KeyAction.CHAR, null, 1f, true),
            Key("Space", KeyAction.SPACE, null, 2.8f, false),
            Key(".com", KeyAction.CHAR, null, 1.8f, true),
            Key("return", KeyAction.ENTER, IconKind.ENTER, 1.2f, true)
        )
    }
}

private fun letterRows(bottom: BottomRow): List<KeyRow> {
    val row1 = "qwertyuiop".map { charKey(it.toString()) }
    val row2 = "asdfghjkl".map { charKey(it.toString()) }
    val row3 = listOf(functionIcon(IconKind.SHIFT, KeyAction.SHIFT, 1.5f)) +
        "zxcvbnm".map { charKey(it.toString()) } +
        listOf(functionIcon(IconKind.DELETE, KeyAction.DELETE, 1.5f))
    return listOf(
        KeyRow(row1),
        KeyRow(row2, centered = true),
        KeyRow(row3),
        KeyRow(bottomKeys(bottom))
    )
}

private fun symbolRows(bottom: BottomRow, first: Boolean): List<KeyRow> {
    val row1 = if (!first) {
        "1234567890".map { charKey(it.toString()) }
    } else {
        listOf("[", "]", "{", "}", "#", "%", "^", "*", "+", "=").map { charKey(it) }
    }
    val row2 = if (!first) {
        listOf("-", "/", ":", ";", "(", ")", "$", "&", "\"").map { charKey(it) }
    } else {
        listOf("_", "\\", "|", "~", "<", ">", "€", "£", "¥", "•").map { charKey(it) }
    }
    val toggle = if (!first) {
        functionText("#+=", KeyAction.TO_MORE, 1.5f)
    } else {
        functionText("123", KeyAction.TO_SYMBOLS, 1.5f)
    }
    val row3 = listOf(toggle) +
        listOf(".", ",", "?", "!", "'").map { charKey(it) } +
        listOf(functionIcon(IconKind.DELETE, KeyAction.DELETE, 1.5f))
    // Di halaman 123, tombol kiri bawah kembali ke huruf (ABC).
    // Di halaman #+=, tombol itu tetap 123 (kembali ke halaman angka).
    val bottomRow = bottomKeys(bottom).map {
        if (!first && it.action == KeyAction.TO_SYMBOLS && it.text == "123") {
            functionText("ABC", KeyAction.TO_LETTERS, it.weight)
        } else {
            it
        }
    }
    return listOf(KeyRow(row1), KeyRow(row2), KeyRow(row3), KeyRow(bottomRow))
}

private fun numpadRows(): List<KeyRow> {
    val rows = listOf("123", "456", "789").map { line ->
        KeyRow(line.map { charKey(it.toString()) })
    }
    val last = KeyRow(
        listOf(
            functionText("ABC", KeyAction.TO_LETTERS, 1f),
            charKey("0"),
            functionIcon(IconKind.DELETE, KeyAction.DELETE, 1f),
            Key("return", KeyAction.ENTER, IconKind.ENTER, 1f, true)
        )
    )
    return rows + last
}
