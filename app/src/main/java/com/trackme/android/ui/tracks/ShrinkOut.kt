package com.trackme.android.ui.tracks

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun Modifier.shrinkOut(visible: Boolean, onEnd: () -> Unit = { }): Modifier {
    val scaleHeightAnimation = animateFloatAsState(if (visible) 1f else 0f, finishedListener = {
        if (it == 0f) onEnd()
    })

    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val scaleHeight = scaleHeightAnimation.value
        this.layout(placeable.width, (placeable.height.toFloat() * scaleHeight).roundToInt()) {
            placeable.place(0, 0)
        }
    }
}

@Preview(device = Devices.PIXEL, showBackground = true)
@Composable
fun ShrinkOutPreviewww() {
    Column {
        listOf(Color.Black, Color.Magenta, Color.Cyan, Color.Yellow).forEach { color ->
            var visible by remember { mutableStateOf(true) }
            Box(Modifier
                    .background(color)
                    .shrinkOut(visible = visible)
                    .height(50.dp)
                    .fillMaxWidth()
                    .clickable(onClick = { visible = false })
            ) {}
        }
    }
}