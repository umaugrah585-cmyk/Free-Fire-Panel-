package com.example.freefirepanel

import android.app.Activity
import android.os.Bundle
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.FrameLayout

class MainActivity : Activity() {
    private lateinit var arena: ArenaView
    private lateinit var toggle: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(18, 18, 18, 18)
            setBackgroundColor(Color.rgb(13, 15, 20))
        }

        val title = TextView(this).apply {
            text = "ESP TEST PANEL"
            textSize = 22f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 12)
        }
        root.addView(title)

        val subtitle = TextView(this).apply {
            text = "Standalone demo • simulated targets only"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(4, 0, 4, 12)
        }
        root.addView(subtitle)

        arena = ArenaView(this)
        root.addView(arena, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        toggle = Button(this).apply {
            text = "ESP: OFF"
            setOnClickListener {
                arena.espEnabled = !arena.espEnabled
                text = if (arena.espEnabled) "ESP: ON" else "ESP: OFF"
                arena.invalidate()
            }
        }
        root.addView(toggle, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ))

        val note = TextView(this).apply {
            text = "This demo draws boxes and tracer lines over its own test arena. It does not read or modify Free Fire."
            textSize = 11f
            setTextColor(Color.GRAY)
            gravity = Gravity.CENTER
            setPadding(8, 12, 8, 4)
        }
        root.addView(note)

        setContentView(root)
    }
}

private class ArenaView(context: android.content.Context) : View(context) {
    var espEnabled = false
    private val bg = Paint(Paint.ANTI_ALIAS_FLAG)
    private val grid = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(43, 49, 59)
        strokeWidth = 1.5f
    }
    private val box = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 65, 75)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(255, 65, 75)
        strokeWidth = 3f
    }
    private val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 27f
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    private val targets = listOf(
        floatArrayOf(.23f, .30f), floatArrayOf(.66f, .43f), floatArrayOf(.48f, .68f)
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.rgb(22, 27, 34))
        val w = width.toFloat()
        val h = height.toFloat()

        // Simple test-arena grid.
        for (i in 1..5) {
            val y = h * i / 6f
            canvas.drawLine(0f, y, w, y, grid)
        }
        for (i in 1..5) {
            val x = w * i / 6f
            canvas.drawLine(x, 0f, x, h, grid)
        }
        label.textSize = 24f
        label.color = Color.rgb(160, 170, 185)
        canvas.drawText("SIMULATED TEST ARENA", 22f, 38f, label)

        // Draw target silhouettes; ESP graphics are gated by the single toggle.
        targets.forEachIndexed { index, p ->
            val cx = p[0] * w
            val cy = p[1] * h
            val body = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(95, 105, 118) }
            canvas.drawCircle(cx, cy - 35f, 15f, body)
            canvas.drawRoundRect(cx - 18f, cy - 18f, cx + 18f, cy + 55f, 8f, 8f, body)

            if (espEnabled) {
                val left = cx - 32f
                val top = cy - 57f
                val right = cx + 32f
                val bottom = cy + 67f
                canvas.drawRect(left, top, right, bottom, box)
                canvas.drawLine(w / 2f, h - 4f, cx, bottom, line)
                label.textSize = 22f
                label.color = Color.WHITE
                canvas.drawText("TARGET ${index + 1}", left - 8f, top - 10f, label)
            }
        }
        if (!espEnabled) {
            label.textSize = 25f
            label.color = Color.LTGRAY
            canvas.drawText("ESP OFF", 22f, h - 24f, label)
        }
    }
}
