package com.example.trackme.ui.tracking

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview

data class Point(val x: Float, val y: Float) {
    operator fun plus(scalar: Float): Point {
        return Point(x + scalar, y + scalar)
    }

    operator fun minus(scalar: Float): Point {
        return Point(x - scalar, y - scalar)
    }
}

private class Interpolation(srcFrom: Float, srcTo: Float, targetFrom: Float, targetTo: Float) {
    private var scaleFactor: Float = 1f
    private var offset: Float = 0f

    init {
        scaleFactor = (targetTo - targetFrom) / (srcTo - srcFrom)
        offset = targetTo - scaleFactor * srcTo
    }

    fun interpolate(value: Float): Float {
        return offset + scaleFactor * value
    }
}

private data class DrawingContext(
    val xInter: Interpolation,
    val yInter: Interpolation,
    val drawScope: DrawScope,
)

data class SelectionLine(
    val x: Float?,
    val y: Float?,
    val width: Float,
    val color: Color,
)

@Composable
fun LineGraph(
    modifier: Modifier,
    data: List<Point>,
    showPoints: Boolean = true,
    selectionLine: SelectionLine? = null,
) {
    Canvas(modifier = modifier.background(Color.White)) {
        val drawingContext = getDrawingContext(this, data)

        drawOriginLines(drawingContext)

        val color = Color.Red
        if (data.isNotEmpty()) drawLine(drawingContext, data, Color.LightGray)
        if (data.isNotEmpty()) drawBezierLine(drawingContext, data, color)
        if (showPoints && data.isNotEmpty()) drawPoints(drawingContext, data, color)
        if (selectionLine != null) drawSelectionLine(drawingContext, selectionLine)
    }
}

private fun getDrawingContext(drawScope: DrawScope, data: List<Point>): DrawingContext {
    val noLines = data.size < 2

    val xMin = data.minOfOrNull { it.x } ?: 0f
    val yMin = (data.minOfOrNull { it.y } ?: 0f).coerceAtMost(0f)
    val xMax = if(noLines) xMin + 1f else data.maxOf { it.x }
    val yMax = if(noLines) yMin + 1f else data.maxOf { it.y }

    val xInter = Interpolation(xMin - xMax * 0.05f, xMax * 1.1f, 0f, drawScope.size.width)
    val yInter = Interpolation(yMin - yMax * 0.05f, yMax * 1.1f, drawScope.size.height, 0f)
    return DrawingContext(xInter, yInter, drawScope)
}

private fun drawOriginLines(drawingContext: DrawingContext) {
    drawSelectionLine(drawingContext, SelectionLine(0f, 0f, 1f, Color.Black))
}

private fun drawSelectionLine(drawingContext: DrawingContext, selection: SelectionLine) {
    val (xInter, yInter, _) = drawingContext
    drawingContext.drawScope.apply {
        if (selection.x != null) {
            val verticalSelection = xInter.interpolate(selection.x)
            this.drawLine(
                selection.color,
                Offset(verticalSelection, 0f),
                Offset(verticalSelection, size.height),
                selection.width
            )
        }
        if (selection.y != null) {
            val horizontalSelection = yInter.interpolate(selection.y)
            this.drawLine(
                selection.color,
                Offset(0f, horizontalSelection),
                Offset(size.width, horizontalSelection),
                selection.width
            )
        }
    }
}

private fun drawLine(drawingContext: DrawingContext, data: List<Point>, color: Color) {
    val (xInter, yInter) = drawingContext
    drawingContext.drawScope.apply {
        val path = Path()
        data.forEachIndexed { index, point ->
            val x = xInter.interpolate(point.x)
            val y = yInter.interpolate(point.y)
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color, style = Stroke())
    }
}

private fun drawBezierLine(drawingContext: DrawingContext, data: List<Point>, color: Color) {
    val (xInter, yInter) = drawingContext
    drawingContext.drawScope.apply {
        val path = Path()
        val first = data.first()
        path.moveTo(xInter.interpolate(first.x), yInter.interpolate(first.y))

        val mMap: MutableMap<Int, Float> = mutableMapOf()
        (1..data.count() - 2).forEach { i ->
            val (p0, p1, p2) = data.subList(i - 1, i + 2)

            val m = ((p2.y - p1.y) / (p2.x - p1.x) + (p1.y - p0.y) / (p1.x - p0.x)) * 0.5f
            mMap[i] = m
        }
        mMap[0] = 0f
        mMap[data.count() - 1] = 0f

        (1 until data.count()).forEach { i ->
            val (pMinus, pi) = data.subList(i - 1, i + 1)
            val divider = 5f
            val pp1 =
                Point(pMinus.x + 1 / divider, pMinus.y + mMap.getOrDefault(i - 1, 0f) / divider)
            val pp2 = Point(pi.x - 1 / divider, pi.y - mMap.getOrDefault(i, 0f) / divider)

            path.cubicTo(
                xInter.interpolate(pp1.x),
                yInter.interpolate(pp1.y),
                xInter.interpolate(pp2.x),
                yInter.interpolate(pp2.y),
                xInter.interpolate(pi.x),
                yInter.interpolate(pi.y),
            )
        }

        val last = data.last()
        path.lineTo(xInter.interpolate(last.x), yInter.interpolate(last.y))

        drawPath(path, color, style = Stroke())
    }
}

private fun drawPoints(drawingContext: DrawingContext, data: List<Point>, color: Color) {
    val (xInter, yInter) = drawingContext
    drawingContext.drawScope.apply {
        data.forEach { p ->
            val center = Offset(xInter.interpolate(p.x),
                                yInter.interpolate(p.y))
            drawCircle(color, 10f, center)
        }
    }
}


private val samplePoints = listOf(
    Point(0f, 6f),
    Point(1f, 10f),
    Point(2f, 6.4f),
    Point(3f, 16.44f),
    Point(4f, 10.44f),
    Point(5f, 11.7f),
    Point(6f, 2.3f),
    Point(7f, 5.7f),
)

@Composable
@Preview(
    device = Devices.PIXEL_3,
    showBackground = true,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
fun LineGraphPreview() {
    MaterialTheme {
        LineGraph(
            modifier = Modifier.fillMaxWidth()
                .preferredHeight(300.dp),
            data = samplePoints
        )
    }
}

@Composable
@Preview(
    device = Devices.PIXEL_3,
    showBackground = true,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
fun LineGraphPreview_WithoutDots_WithSelectionLine() {
    MaterialTheme {
        LineGraph(
            modifier = Modifier.fillMaxWidth()
                .preferredHeight(300.dp),
            data = samplePoints,
            showPoints = false,
            SelectionLine(5f, 10f, 2f, Color.Blue)
        )
    }
}

@Composable
@Preview(
    device = Devices.PIXEL_3,
    showBackground = true,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
fun LineGraphPreview_WithoutData() {
    MaterialTheme {
        LineGraph(
            modifier = Modifier.fillMaxWidth()
                .preferredHeight(300.dp),
            showPoints = true,
            data = listOf(Point(1f, 10f)),
        )
    }
}