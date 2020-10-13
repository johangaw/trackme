package com.trackme.android.ui.common

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import kotlin.math.roundToInt

/**
 * @param totalLength in meter
 */
@Composable
fun Distance(
    totalLength: Float,
    style: TextStyle = MaterialTheme.typography.body1,
    modifier: Modifier = Modifier
) {
    Text(text = formatDistance(totalLength), style = style, modifier = modifier)
}

private fun formatDistance(distance: Float): String {
    return "${((distance / 1000f) * 100f).roundToInt() / 100f} km"
}