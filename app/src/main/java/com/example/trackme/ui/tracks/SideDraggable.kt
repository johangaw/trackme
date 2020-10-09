package com.example.trackme.ui.tracks

import androidx.compose.animation.animatedFloat
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.onCommit
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview


@Composable
fun Modifier.sideDraggable(
    maxOffset: Float = -75f,
    onEnd: (finished: Boolean) -> Unit = {},
    selectedState: MutableState<Boolean> = mutableStateOf(false),
    key: Any? = null,
): Modifier {
    val offset = animatedFloat(0f)
    onCommit(key) {
        offset.snapTo(if (selectedState.value) maxOffset else 0f)
    }
    return this
        .offset(offset.value.dp)
        .draggable(
            Orientation.Horizontal,
            onDrag = {
                offset.snapTo(offset.value + it / 2f)
            },
            onDragStopped = {
                val config = FlingConfig(listOf(maxOffset, 0f).sorted())
                offset.fling(
                    -it,
                    config
                ) { _, animationValue, _ ->
                    val selected = animationValue != 0f
                    selectedState.value = selected
                    onEnd(selected)
                }
            }
        )
}


@Preview(device = Devices.PIXEL)
@Composable
fun SideDraggablePreview() {
    Box(Modifier.fillMaxWidth().background(Color.Red).padding(16.dp).border(2.dp, Color.Black)) {
        Surface(elevation = 8.dp, modifier = Modifier.fillMaxSize().sideDraggable()) {

        }
    }
}