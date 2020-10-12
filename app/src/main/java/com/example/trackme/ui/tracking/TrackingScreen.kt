package com.example.trackme.ui.tracking

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.data.TrackEntry
import com.example.trackme.ui.common.Distance
import com.example.trackme.ui.common.Speed
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun TrackingScreen(
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalLength: Float,
    currentSpeed: Float,
    trackEntries: List<TrackEntry>,
) {
    val running = startedAt != null

    Column(Modifier.fillMaxSize()) {
        if (running) {
            Clock(startedAt, Modifier.align(Alignment.CenterHorizontally))
        } else if (trackEntries.isNotEmpty()) {
            val start =
                LocalDateTime.ofEpochSecond(trackEntries.first().time / 1000, 0, ZoneOffset.UTC)
            val end =
                LocalDateTime.ofEpochSecond(trackEntries.last().time / 1000, 0, ZoneOffset.UTC)
            StaticClock(start, end, Modifier.align(Alignment.CenterHorizontally))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Distance(totalLength, style = MaterialTheme.typography.h3)
            Speed(currentSpeed, style = MaterialTheme.typography.h3)
        }

        val normalizer = trackEntries.firstOrNull()?.time ?: 0
        val speedPoints =
            remember(trackEntries) {
                trackEntries.map {
                    Point((it.time - normalizer).toFloat(),
                          it.speed)
                }
            }
        val firstPoint = speedPoints.firstOrNull() ?: Point(0f, 0f)
        val (value, setValue) = remember { mutableStateOf(firstPoint.x) }
        val selection = SelectionLine(value, null, 4f, Color.Yellow)
        LineGraph(
            modifier = Modifier.fillMaxWidth()
                .preferredHeight(200.dp),
            data = speedPoints,
            showPoints = speedPoints.size < 10,
            selectionLine = if (!running) selection else null,
        )
        if (running) {
            Box(
                modifier = Modifier.fillMaxSize(),
                alignment = Alignment.Center
            ) {
                RoundTextButton(
                    onClick = onStopClick,
                    text = "Stop",
                )
            }
        } else {
            val selected = search(value.toLong() + normalizer, trackEntries)
            val rangeLower = speedPoints.minOfOrNull { it.x } ?: 0f
            val rangeUpper = speedPoints.maxOfOrNull { it.x } ?: 0f
            Spacer(modifier = Modifier.preferredHeight(32.dp))

            Speed(speed = selected?.speed ?: -1f, style = MaterialTheme.typography.h3)
            Text("${selected?.altitude?.roundToInt() ?: -1} mas", style = MaterialTheme.typography.h3)
            Slider(
                value = value,
                onValueChange = setValue,
                valueRange = rangeLower..rangeUpper,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

fun search(time: Long, entries: List<TrackEntry>): TrackEntry? {
    return entries.minByOrNull { abs(it.time - time) }
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
