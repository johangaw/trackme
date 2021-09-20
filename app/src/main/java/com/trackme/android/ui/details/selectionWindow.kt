package com.trackme.android.ui.details

import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import kotlin.math.absoluteValue

class SelectionWindowState {
    var canvasWidth by mutableStateOf(1f)
    var canvasHeight by mutableStateOf(1f)
    var selectionWidth by mutableStateOf(canvasWidth)
    var offset by mutableStateOf(canvasWidth / 2)

    operator fun component1() = canvasWidth
    operator fun component2() = canvasHeight
    operator fun component3() = selectionWidth
    operator fun component4() = offset
}

@Composable
fun rememberSelectionWindowState(): SelectionWindowState {
    return remember { SelectionWindowState() }
}

fun Modifier.selectionWindow(
    state: SelectionWindowState,
): Modifier {


    return this
        .onGloballyPositioned {
            val newCanvasWidth = it.size.width.toFloat()
            val interpolator = Interpolation(0f, state.canvasWidth, 0f, newCanvasWidth)
            state.canvasWidth = newCanvasWidth
            state.canvasHeight = it.size.height.toFloat()
            state.selectionWidth = interpolator.interpolate(state.selectionWidth)
            state.offset = interpolator.interpolate(state.offset)
        }
        .pointerInput(null) {
            detectTransformGestures(
                onGesture = { _, panGesture, zoomGesture, _ ->
                    val newSelectionWidth =
                        (zoomGesture * state.selectionWidth).coerceIn(0f, state.canvasWidth)
                    val selectionRightEdge =
                        (state.offset + newSelectionWidth / 2f).coerceAtMost(state.canvasWidth)
                    val selectionLeftEdge =
                        (state.offset - newSelectionWidth / 2f).coerceAtLeast(0f)
                    state.selectionWidth = (selectionRightEdge - selectionLeftEdge).absoluteValue

                    val newOffset = (state.offset + panGesture.x).coerceIn(state.selectionWidth / 2,
                                                                           state.canvasWidth - state.selectionWidth / 2)

                    // TODO see if this can be refactored
                    state.offset = when {
                        zoomGesture == 1f -> newOffset
                        selectionRightEdge == state.canvasWidth -> state.canvasWidth - state.selectionWidth / 2f
                        selectionLeftEdge == 0f -> state.selectionWidth / 2f
                        else -> newOffset
                    }
                }
            )
        }
}