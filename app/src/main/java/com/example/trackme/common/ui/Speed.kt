package com.example.trackme.common.ui

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun Speed(
    speed: Float,
    style: TextStyle = MaterialTheme.typography.body1,
    modifier: Modifier = Modifier,
) {
    Text(text = "$speed m/s", style = style, modifier = modifier)
}