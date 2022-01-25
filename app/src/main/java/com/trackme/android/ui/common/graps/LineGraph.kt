package com.trackme.android.ui.common.graps

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import kotlin.math.abs
import kotlin.math.roundToInt

interface IPoint {
    val x: Float
    val y: Float

    fun toOffset(int: Interpolation): Offset = Offset(x, y).transform(int)
}

fun Offset.transform(int: Interpolation) = Offset(x * int.dx, y * int.dy)

data class Interpolation(val dx: Float, val dy: Float)

fun createInterpolation(
    xRange: Pair<Float, Float>,
    xTargetRange: Pair<Float, Float>,
    yRange: Pair<Float, Float>,
    yTargetRange: Pair<Float, Float>,
): Interpolation {
    val dx = abs(xTargetRange.second - xTargetRange.first) / abs(xRange.second - xRange.first)
    val dy = abs(yTargetRange.second - yTargetRange.first) / abs(yRange.second - yRange.first)
    return Interpolation(dx, dy)
}

infix fun Float.to(target: Float): Pair<Float, Float> = Pair(this, target)


data class Options(
    val enabled: Boolean = true,
)

@Composable
fun LineGraph(
    points: List<IPoint>,
    modifier: Modifier = Modifier,
    options: Options = Options(),
) {
    val selectionColor = Color.Green
    val lineBrush = SolidColor(Color.Black)
    val selectionKnobSize = 24.dp
    val xRange =
        remember(points) {
            points.minOf { it.x }
                .coerceAtMost(0f) to points.maxOf { it.x }
        }
    val yRange =
        remember(points) {
            points.minOf { it.y }
                .coerceAtMost(0f) to points.maxOf { it.y }
        }

    var selectionOffset by remember { mutableStateOf(0f) }
    var canvasSize by remember { mutableStateOf(IntSize(0, 0)) }

    val int = remember(canvasSize, xRange, yRange) {
        createInterpolation(
            xRange,
            0f to canvasSize.width.toFloat(),
            yRange,
            0f to canvasSize.height.toFloat()
        )
    }

    val selectedPoint = points.minByOrNull { abs(it.toOffset(int).x - selectionOffset) }
//    Log.d("LineGraph", selectedPoint.toString())
//    Log.d("LineGraph", "int = $int,   selectionOffset = $selectionOffset")
//    Log.d("LineGraph", "int = ${canvasSize.width}")

    Box(
        modifier = modifier
            .background(Color.White)
            .padding(horizontal = selectionKnobSize / 2)
    ) {
        SelectionLine(selectionOffset, selectedPoint, selectionColor)

        Column {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .onGloballyPositioned { canvasSize = it.size }
            ) {

                withTransform(transformBlock = {
                    translate(0f, size.height)
                    scale(1f, -1f, Offset(0f, 0f))
                }) {

                    // Corners
//                drawCircle(Color.Red, 10f, Offset(0f, 0f))
//                drawCircle(Color.Red, 10f, Offset(0f, size.height))
//                drawCircle(Color.Red, 10f, Offset(size.width, 0f))
//                drawCircle(Color.Red, 10f, Offset(size.width, size.height))

                    points.zipWithNext()
                        .forEach { (start, end) ->
                            drawLine(
                                lineBrush,
                                start.toOffset(int),
                                end.toOffset(int),
                                strokeWidth = 5f
                            )
                        }

                    points.forEach {
                        drawCircle(
                            if (it == selectedPoint) Color.Red else Color.Black,
                            10f,
                            it.toOffset(int)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                Box(
                    Modifier
                        .height(4.dp)
                        .fillMaxWidth()
                        .border(1.dp, Color.Black)
                )
                Box(
                    Modifier
                        .size(selectionKnobSize)
                        .offset {
                            IntOffset(
                                selectionOffset.roundToInt() - (selectionKnobSize.toPx() / 2).toInt(),
                                0
                            )
                        }
                        .clip(CircleShape)
                        .background(selectionColor)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                Log.d("LineGraph", "dragging")
                                selectionOffset =
                                    (selectionOffset + delta).coerceIn(
                                        0f,
                                        canvasSize.width.toFloat(),
                                    )
                            })
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

}

@Composable
private fun SelectionLine(
    selectionOffset: Float,
    selectedPoint: IPoint?,
    selectionColor: Color,
) {
    val selectionLineWidth = 2.dp
    ConstraintLayout(modifier = Modifier
        .fillMaxHeight()
        .offset {
            IntOffset(
                selectionOffset.roundToInt() - (selectionLineWidth.toPx() / 2).toInt(),
                0
            )
        }) {
        val (text, line) = createRefs()

        Text("${selectedPoint?.y}m/s",
             softWrap = false,
             overflow = TextOverflow.Visible,
             modifier = Modifier.constrainAs(text) {
                 top.linkTo(parent.top)
                 centerHorizontallyTo(line)
             })

        Box(
            Modifier
                .width(selectionLineWidth)
                .constrainAs(line) {
                    top.linkTo(text.bottom)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .background(
                    Brush.verticalGradient(
                        0f to Color.White,
                        0.5f to Color.White,
                        0.5f to selectionColor,
                        1.0f to selectionColor,
                        startY = 0f,
                        endY = 10f,
                        tileMode = TileMode.Repeated,
                    )
                )
        )

    }
}


data class Point(override val x: Float, override val y: Float) : IPoint

//val points = (0..4).map { Point(it.toFloat(), Random.nextFloat() * 100) }
val points = listOf(
    Point(0f, 33f),
    Point(1f, 10f),
    Point(2f, 88f),
    Point(3f, 66f),
    Point(4f, 27f),
)

@Composable
@Preview(showBackground = true)
fun LineGraphPreview() {
    Column(Modifier.padding(16.dp)) {
        LineGraph(
            points,
            Modifier
                //            .width(500.dp)
                .height(300.dp)
        )
//        Spacer(modifier = Modifier.height(16.dp))
    }
}