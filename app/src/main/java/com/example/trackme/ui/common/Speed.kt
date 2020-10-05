package com.example.trackme.ui.common

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
    val unit = SettingsAmbient.current.speedUnit
    Text(text = formatSpeed(speed, unit), style = style, modifier = modifier)
}

const val KNOTS_CONVERSION_FACTOR = 3600f / 1852f
const val KILOMETERS_PER_HOUR_CONVERSION_FACTOR = 3.6f

fun formatSpeed(speed: Float, unit: SpeedUnit): String {
    return when(unit) {
        SpeedUnit.METER_PER_SECOND -> {
            val rounded = (speed * 100).roundToInt() / 100f
            "$rounded m/s"
        }
        SpeedUnit.KILOMETERS_PER_HOUR -> {
            val rounded = (speed * KILOMETERS_PER_HOUR_CONVERSION_FACTOR * 10).roundToInt() / 10f
            "$rounded km/h"
        }
        SpeedUnit.KNOTS -> {
            "${((speed * KNOTS_CONVERSION_FACTOR) * 10).roundToInt() / 10f} kn"
        }
    }


}