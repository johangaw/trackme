package com.trackme.android.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun RunningTrackDetails(
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalDistance: Float,
    averageSpeed: Float,
) {
    Column(Modifier.fillMaxSize()) {
        Clock(startedAt, Modifier.align(Alignment.CenterHorizontally))
        DistanceSpeedRow(totalDistance, averageSpeed)

        val speedPoints =
            remember(trackEntries) {
                val normalizer = trackEntries.firstOrNull()?.time ?: 0
                trackEntries.map {
                    Point((it.time - normalizer).toFloat(), it.speed)
                }
            }

        LineGraph(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            data = speedPoints,
            showPoints = speedPoints.size < 10
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RoundTextButton(
                onClick = onStopClick,
                text = "Stop",
            )
        }
    }
}