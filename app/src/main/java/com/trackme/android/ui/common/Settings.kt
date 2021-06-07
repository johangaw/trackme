package com.trackme.android.ui.common

import androidx.compose.runtime.staticCompositionLocalOf


data class Settings(
    val speedUnit: SpeedUnit = SpeedUnit.METER_PER_SECOND

)

enum class SpeedUnit {
    METER_PER_SECOND,
    KILOMETERS_PER_HOUR,
    KNOTS,
}

val LocalSettings = staticCompositionLocalOf { Settings() }