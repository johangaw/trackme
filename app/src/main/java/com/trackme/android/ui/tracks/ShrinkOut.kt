package com.trackme.android.ui.tracks
//
//import androidx.compose.animation.animatedFloat
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.preferredHeight
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout
//import androidx.compose.ui.unit.dp
//import androidx.ui.tooling.preview.Devices
//import androidx.ui.tooling.preview.Preview
//import kotlin.math.roundToInt
//
//@Composable
//fun Modifier.shrinkOut(visible: Boolean, onEnd: () -> Unit = { }): Modifier {
//    val scaleHeightAnimation = animatedFloat(initVal = 1f)
//
//    onCommit(visible) {
//        when (visible) {
//            true -> scaleHeightAnimation.snapTo(1f)
//            false -> scaleHeightAnimation.animateTo(0f) { _, _ -> onEnd() }
//        }
//    }
//
//    return this.layout { measurable, constraints ->
//        val placeable = measurable.measure(constraints)
//        val scaleHeight = scaleHeightAnimation.value
//        this.layout(placeable.width, (placeable.height.toFloat() * scaleHeight).roundToInt()) {
//            placeable.place(0, 0)
//        }
//    }
//}
//
//@Preview(device = Devices.PIXEL, showBackground = true)
//@Composable
//fun ShrinkOutPreview() {
//    Column {
//        listOf(Color.Black, Color.Magenta, Color.Cyan, Color.Yellow).forEach { color ->
//            var visible by remember { mutableStateOf(true) }
//            Box(Modifier.background(color)
//                    .shrinkOut(visible = visible)
//                    .preferredHeight(50.dp)
//                    .fillMaxWidth()
//                    .clickable(onClick = {visible = false})
//            ){}
//        }
//    }
//}