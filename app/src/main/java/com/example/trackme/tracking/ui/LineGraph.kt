package com.example.trackme.tracking.ui

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview

data class Point(val x: Float, val y: Float)

class Interpolation(srcFrom: Float, srcTo: Float, targetFrom: Float, targetTo: Float) {
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

@Composable
fun LineGraph(
    modifier: Modifier,
    data: List<Point>,
) {

    val xMin = data.minOf { it.x }
    val xMax = data.maxOf { it.x }
    val yMin = data.minOf { it.y }
    val yMax = data.maxOf { it.y }

    Canvas(modifier = modifier.background(Color.White)) {
        val xInter = Interpolation(xMin - xMax * 0.05f, xMax * 1.1f,  0f, size.width)
        val yInter = Interpolation(yMin - yMax * 0.05f, yMax * 1.1f, size.height, 0f)

        drawOriginLines(this, xInter, yInter)

        val color = Color.Red
        data.zipWithNext{p1, p2 ->
            val x1 = xInter.interpolate(p1.x)
            val y1 = yInter.interpolate(p1.y)
            val x2 = xInter.interpolate(p2.x)
            val y2 = yInter.interpolate(p2.y)
            drawLine(color, Offset(x1, y1), Offset(x2, y2))
        }

        data.forEach {p ->
            val center = Offset(xInter.interpolate(p.x), yInter.interpolate(p.y))
            drawCircle(color, 15f, center)
        }
    }
}

fun drawOriginLines(drawScope: DrawScope, xInter: Interpolation, yInter: Interpolation ) {
    drawScope.apply {
        val xOrigin = xInter.interpolate(0f)
        val yOrigin = yInter.interpolate(0f)

        drawLine(Color.Black, Offset(0f, yOrigin), Offset(size.width, yOrigin))
        drawLine(Color.Black, Offset(xOrigin, 0f), Offset(xOrigin, size.height))
    }
}


@Composable
@Preview(
    device = Devices.PIXEL_3,
    showBackground = true,
    uiMode = Configuration.ORIENTATION_LANDSCAPE
)
fun LineGraphPreview() {
    MaterialTheme {
        LineGraph(
            modifier = Modifier.fillMaxSize(),
            data = listOf(
                Point(0f, 0f),
                Point(1f, 10f),
                Point(2f, 6.4f),
                Point(3f, 16.44f)
            )
        )
    }
}