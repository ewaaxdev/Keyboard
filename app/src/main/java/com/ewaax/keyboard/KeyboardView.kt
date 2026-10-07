package com.ewaax.keyboard

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import android.view.View

// Jembatan View -> Service (mengetik, hapus, enter).
interface ImeHost {
    fun typeText(text: String)
    fun deleteBeforeCursor(count: Int)
    fun pressEnter()
}

// Satu-satunya tampilan keyboard: SEMUA tombol digambar di 1 kanvas
// (tanpa puluhan objek View) agar cepat dan hemat memori.
class KeyboardView(context: Context) : View(context) {

    var host: ImeHost? = null

    private var theme: ThemeColors = KeyboardThemes.resolve(context)
    private var layer: Layer = Layer.LETTERS
    private var shiftState: ShiftState = ShiftState.OFF

    private data class PlacedKey(val key: Key, val rect: RectF)
    private var placed: List<PlacedKey> = emptyList()
    private var pressedKey: Key? = null
    private var activePointerId = MotionEvent.INVALID_POINTER_ID

    private val gap = dp(6f)
    private val radius = dp(8f)
    private var keyHeight = dp(56f)

    private val keyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val iconCache = mutableMapOf<IconKind, Drawable>()

    private fun dp(value: Float): Float {
        return value * resources.displayMetrics.density
    }

    // Dipanggil service: ganti tema, tampilkan layer, reset shift.
    fun applyTheme(colors: ThemeColors) {
        theme = colors
        iconCache.clear()
        invalidate()
    }

    fun showLayer(next: Layer) {
        layer = next
        pressedKey = null
        requestLayout()
    }

    fun resetShift() {
        shiftState = ShiftState.OFF
        invalidate()
    }

    // --- Ukur dan susun tombol ---

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val rows = buildRows(layer).size
        val height = (paddingTop + paddingBottom + rows * keyHeight + (rows - 1) * gap).toInt()
        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldW: Int, oldH: Int) {
        layoutKeys(w)
    }

    private fun layoutKeys(width: Int) {
        val rows = buildRows(layer)
        val out = mutableListOf<PlacedKey>()
        var y = paddingTop.toFloat()
        val contentWidth = width - paddingLeft - paddingRight
        for (row in rows) {
            val totalWeight = row.keys.sumOf { it.weight.toDouble() }.toFloat()
            val unit = contentWidth / totalWeight
            // Baris tengah (a..l) digeser setengah tombol agar rapi di tengah.
            var x = paddingLeft.toFloat() + if (row.centered) unit / 2f else 0f
            val rowWidth = if (row.centered) contentWidth - unit else contentWidth.toFloat()
            for (key in row.keys) {
                val keyWidth = unit * key.weight
                // Gap digambar sebagai jarak: rect diperkecil di tiap sisi.
                val rect = RectF(
                    x + gap / 2f,
                    y,
                    (x + keyWidth - gap / 2f).coerceAtMost((paddingLeft + rowWidth).toFloat()),
                    y + keyHeight
                )
                out.add(PlacedKey(key, rect))
                x += keyWidth
            }
            y += keyHeight + gap
        }
        placed = out
    }

    // --- Gambar ---

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(theme.background)
        for ((key, rect) in placed) {
            drawKey(canvas, key, rect)
        }
    }

    private fun drawKey(canvas: Canvas, key: Key, rect: RectF) {
        val isShiftActive = key.action == KeyAction.SHIFT && shiftState != ShiftState.OFF
        keyPaint.color = when {
            isShiftActive -> theme.activeKey
            key.function -> theme.functionKey
            else -> theme.letterKey
        }
        canvas.drawRoundRect(rect, radius, radius, keyPaint)
        if (pressedKey == key) {
            overlayPaint.color = theme.pressedOverlay
            canvas.drawRoundRect(rect, radius, radius, overlayPaint)
        }
        if (key.action == KeyAction.SHIFT) {
            val tint = if (shiftState != ShiftState.OFF) theme.activeText else theme.icon
            drawIcon(canvas, shiftIcon(), rect, tint)
        } else if (key.icon != null) {
            drawIcon(canvas, key.icon, rect, theme.icon)
        } else {
            val label = displayText(key)
            textPaint.color = if (isShiftActive) theme.activeText else theme.text
            textPaint.textSize = if (key.function) dp(14f) else dp(20f)
            val centerY = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawText(label, rect.centerX(), centerY, textPaint)
        }
    }

    private fun displayText(key: Key): String {
        // Huruf ikut shift (kapital saat ONCE/LOCKED); simbol tidak.
        if (key.action == KeyAction.CHAR && key.text.length == 1
            && key.text[0].isLetter() && shiftState != ShiftState.OFF
        ) {
            return key.text.uppercase()
        }
        return key.text
    }

    private fun drawIcon(canvas: Canvas, kind: IconKind, rect: RectF, tint: Int) {
        val drawable = iconCache.getOrPut(kind) {
            val res = when (kind) {
                IconKind.SHIFT -> R.drawable.ic_shift
                IconKind.SHIFT_LOCKED -> R.drawable.ic_shift_filled
                IconKind.DELETE -> R.drawable.ic_backspace
                IconKind.GLOBE -> R.drawable.ic_globe
                IconKind.MIC -> R.drawable.ic_mic
                IconKind.ENTER -> R.drawable.ic_return
            }
            context.getDrawable(res)!!.mutate()
        }
        drawable.setTint(tint)
        val size = minOf(rect.width(), rect.height()) * 0.44f
        val left = rect.centerX() - size / 2f
        val top = rect.centerY() - size / 2f
        drawable.setBounds(left.toInt(), top.toInt(), (left + size).toInt(), (top + size).toInt())
        drawable.draw(canvas)
    }

    // Ikon shift penuh saat caps lock, kerangka saat sekali ketuk/mati.
    private fun shiftIcon(): IconKind {
        return if (shiftState == ShiftState.LOCKED) IconKind.SHIFT_LOCKED else IconKind.SHIFT
    }

    // --- Sentuh (Fase 1: ketuk saja) ---

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                activePointerId = event.getPointerId(0)
                pressedKey = findKey(event.x, event.y)?.key
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val key = findKey(event.x, event.y)?.key
                if (event.getPointerId(0) == activePointerId && key != null && key == pressedKey) {
                    activate(key)
                }
                pressedKey = null
                activePointerId = MotionEvent.INVALID_POINTER_ID
                invalidate()
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                pressedKey = null
                activePointerId = MotionEvent.INVALID_POINTER_ID
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findKey(x: Float, y: Float): PlacedKey? {
        return placed.firstOrNull { it.rect.contains(x, y) }
    }

    private fun activate(key: Key) {
        when (key.action) {
            KeyAction.CHAR -> {
                val text = displayText(key)
                host?.typeText(text)
                shiftState = consumeShiftOnLetter(shiftState)
            }
            KeyAction.SHIFT -> shiftState = tapShift(shiftState)
            KeyAction.DELETE -> host?.deleteBeforeCursor(1)
            KeyAction.SPACE -> host?.typeText(" ")
            KeyAction.ENTER -> host?.pressEnter()
            else -> Unit // Pindah layer/globe/mic menyusul di fase berikutnya.
        }
        invalidate()
    }
}
