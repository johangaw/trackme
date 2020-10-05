package com.example.trackme.common.ui

import androidx.compose.runtime.staticAmbientOf

data class Settings(
    val speedUnit: SpeedUnit = SpeedUnit.METER_PER_SECOND

)

enum class SpeedUnit {
    METER_PER_SECOND,
    KILOMETERS_PER_HOUR,
    KNOTS,
}

val SettingsAmbient = staticAmbientOf { Settings() }