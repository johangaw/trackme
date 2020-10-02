package com.example.trackme.tracking.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.common.ui.Distance
import com.example.trackme.common.ui.Speed
import com.example.trackme.data.TrackEntry
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun TrackingScreen(
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalLength: Float,
    currentSpeed: Float,
    trackEntries: List<TrackEntry>,
) {
    val normalizer = trackEntries.firstOrNull()?.time ?: 0
    val speedPoints =
        remember(trackEntries) { trackEntries.map { Point((it.time - normalizer).toFloat(), it.speed) } }
    val started = startedAt != null

    Column(Modifier.fillMaxSize()) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if(started) {
                Clock(startedAt)
            } else if(trackEntries.isNotEmpty()) {
                val start = LocalDateTime.ofEpochSecond(trackEntries.first().time / 1000, 0, ZoneOffset.UTC)
                val end = LocalDateTime.ofEpochSecond(trackEntries.last().time / 1000, 0, ZoneOffset.UTC)
                StaticClock(start, end)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Distance(totalLength, style = MaterialTheme.typography.h3)
            Speed(currentSpeed, style = MaterialTheme.typography.h3)
        }
        LineGraph(
            modifier = Modifier.fillMaxWidth().preferredHeight(200.dp),
            data = speedPoints,
        )
        if (started) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundTextButton(
                    onClick = onStopClick,
                    text = "Stop",
                )
            }
        }
    }
}

@Composable
@Preview(
    name = "Tracking in progress",
    device = Devices.PIXEL_3,
    showBackground = true,
)
fun TrackingScreenPreview() {
    val trackStartedAt = LocalDateTime.now().minusHours(1).minusMinutes(2).minusSeconds(36)
    MaterialTheme {
        TrackingScreen(
            onStopClick = {},
            startedAt = trackStartedAt,
            totalLength = 1337F,
            currentSpeed = 3.67F,
            trackEntries = trackEntries,
        )
    }
}

@Composable
@Preview(
    name = "Tracking not in progress",
    device = Devices.PIXEL_3,
    showBackground = true,
)
fun NotTrackingScreenPreview() {
    MaterialTheme {
        TrackingScreen(
            onStopClick = {},
            startedAt = null,
            totalLength = 0F,
            currentSpeed = 0.0F,
            trackEntries = trackEntries
        )
    }
}

fun createEntry(time: Long, speed: Float): TrackEntry =
    TrackEntry(time = time * 1000,
               trackId = 0,
               latitude = 0.0,
               longitude = 0.0,
               bearing = 0F,
               speed = speed,
               altitude = 0.0,
               id = 0)

val trackEntries = listOf<TrackEntry>(
    createEntry(0, 3f),
    createEntry(1, 4f),
    createEntry(2, 5f),
    createEntry(3, 4f),
    createEntry(6, 3f),
    createEntry(9, 3f),
    createEntry(11, 3.5f),
    createEntry(14, 3.6f),
    createEntry(16, 3.7f),
    createEntry(17, 4f),
    createEntry(18, 5f),
    createEntry(19, 5.5f),
)
