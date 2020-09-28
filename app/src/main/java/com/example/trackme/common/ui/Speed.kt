package com.example.trackme.common.ui

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

/**
 * @param speed in m/s
 */
@Composable
fun Speed(
    speed: Float,
    style: TextStyle = MaterialTheme.typography.body1,
    modifier: Modifier = Modifier,
) {
    val roundedSpeed = (speed * 100).roundToInt() / 100f
    Text(text = "$roundedSpeed m/s", style = style, modifier = modifier)
}