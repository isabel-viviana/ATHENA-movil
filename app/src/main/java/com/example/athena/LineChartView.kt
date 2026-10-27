package com.example.athena

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var dataPoints: List<Float> = listOf(20f, 45f, 30f, 65f, 50f, 85f, 70f)
    
    private val paintLine = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        strokeWidth = 8f
        style = Paint.Style.STROKE
        isAntiAlias = true
    }

    private val paintFill = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        alpha = 50
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintPoint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_blue_dark)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun setData(points: List<Float>) {
        dataPoints = points
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataPoints.isEmpty()) return

        val widthFloat = width.toFloat()
        val heightFloat = height.toFloat()
        val padding = 40f

        val availableWidth = widthFloat - (padding * 2)
        val availableHeight = heightFloat - (padding * 2)

        val maxVal = dataPoints.maxOrNull() ?: 100f
        val minVal = dataPoints.minOrNull() ?: 0f
        val range = if (maxVal == minVal) 1f else (maxVal - minVal)

        val path = Path()
        val fillPath = Path()

        val dx = availableWidth / (dataPoints.size - 1.coerceAtLeast(1))

        dataPoints.forEachIndexed { index, value ->
            val x = padding + index * dx
            val y = heightFloat - padding - ((value - minVal) / range) * availableHeight

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, heightFloat - padding)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }
        }

        if (dataPoints.size > 1) {
            val lastX = padding + (dataPoints.size - 1) * dx
            fillPath.lineTo(lastX, heightFloat - padding)
            fillPath.close()
            canvas.drawPath(fillPath, paintFill)
        }

        canvas.drawPath(path, paintLine)

        dataPoints.forEachIndexed { index, value ->
            val x = padding + index * dx
            val y = heightFloat - padding - ((value - minVal) / range) * availableHeight
            canvas.drawCircle(x, y, 10f, paintPoint)
        }
    }
}
