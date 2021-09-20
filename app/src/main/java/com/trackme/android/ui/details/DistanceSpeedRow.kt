package com.trackme.android.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trackme.android.ui.common.Distance
import com.trackme.android.ui.common.Speed

@Composable
fun DistanceSpeedRow(distance: Float, speed: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Distance(distance, style = MaterialTheme.typography.h4)
        Speed(speed, style = MaterialTheme.typography.h4)
    }
}