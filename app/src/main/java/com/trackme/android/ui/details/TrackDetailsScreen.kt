package com.trackme.android.ui.details

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.trackme.android.data.TrackEntry
import java.time.LocalDateTime

@Composable
fun TrackDetailsScreen(
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalDistance: Float,
    averageSpeed: Float,
    trackEntries: List<TrackEntry>,
    selectedTrackEntries: List<TrackEntry>,
    onSelectTrackRange: (range: IntRange) -> Unit,
    onMapClick: () -> Unit,
) {
    val running = startedAt != null

    if (running)
        RunningTrackDetails(onStopClick, startedAt, totalDistance, averageSpeed)
    else
        TrackDetails(totalDistance,
                     averageSpeed,
                     trackEntries,
                     selectedTrackEntries,
                     onSelectTrackRange,
                     onMapClick
        )
}


@Composable
@Preview(
    name = "Tracking in progress",
    device = Devices.PIXEL_3,
    showBackground = true,
)
fun TrackingScreenPreview() {
    val trackStartedAt = LocalDateTime.now()
        .minusHours(1)
        .minusMinutes(2)
        .minusSeconds(36)
    MaterialTheme {
        TrackDetailsScreen(
            onStopClick = {},
            startedAt = trackStartedAt,
            totalDistance = 1337F,
            averageSpeed = 3.67F,
            trackEntries = trackEntries,
            selectedTrackEntries = trackEntries,
            onSelectTrackRange = {},
            onMapClick = {},
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
        TrackDetailsScreen(
            onStopClick = {},
            startedAt = null,
            totalDistance = 0F,
            averageSpeed = 0.0F,
            trackEntries = trackEntries,
            selectedTrackEntries = trackEntries,
            onSelectTrackRange = {},
            onMapClick = {},
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
