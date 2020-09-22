package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.data.TrackEntry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun TrackingScreen(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalLength: Float,
    currentSpeed: Double,
    trackEntries: List<TrackEntry>,
) {

    val speedPoints =
        remember(trackEntries) { trackEntries.map { Point(it.time.toFloat(), it.speed) } }

    if (startedAt == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalGravity = Alignment.CenterHorizontally,
        ) {
            RoundTextButton(
                onClick = onStartClick,
                text = "Start",
            )
        }

    } else {
        Column(Modifier.fillMaxSize()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Clock(startedAt)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = formatDistance(totalLength), style = MaterialTheme.typography.h3)
                Text(text = "$currentSpeed m/s", style = MaterialTheme.typography.h3)
            }
            LineGraph(
                modifier = Modifier.fillMaxWidth().preferredHeight(200.dp),
                data = speedPoints,
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalGravity = Alignment.CenterVertically,
            ) {
                RoundTextButton(
                    onClick = onStopClick,
                    text = "Stop",
                )
            }
        }
    }
}

fun formatDistance(distance: Float): String {
    return "${((distance / 1000f) * 100f).roundToInt() / 100f} km"
}

fun timer(delay: Long, cb: () -> Unit): Job {
    return GlobalScope.launch {
        while (true) {
            cb()
            delay(delay)
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
            onStartClick = {},
            onStopClick = {},
            startedAt = trackStartedAt,
            totalLength = 1337F,
            currentSpeed = 3.67,
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
            onStartClick = {},
            onStopClick = {},
            startedAt = null,
            totalLength = 0F,
            currentSpeed = 0.0,
            trackEntries = emptyList()
        )
    }
}

fun createEntry(time: Long, speed: Float): TrackEntry =
    TrackEntry(time = time,
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
