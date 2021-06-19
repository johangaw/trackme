package com.trackme.android.ui.tracking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackme.android.data.TrackEntry
import com.trackme.android.ui.common.Altitude
import com.trackme.android.ui.common.Distance
import com.trackme.android.ui.common.Speed
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.abs

@Composable
fun TrackingScreen(
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalDistance: Float,
    averageSpeed: Float,
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
            Distance(totalDistance, style = MaterialTheme.typography.h3)
            Speed(averageSpeed, style = MaterialTheme.typography.h3)
        }

        var graphType: GraphType by remember { mutableStateOf(GraphType.SPEED) }
        val normalizer = trackEntries.firstOrNull()?.time ?: 0
        val speedPoints =
            remember(trackEntries, graphType) {
                trackEntries.map {
                    Point((it.time - normalizer).toFloat(),
                          if (graphType == GraphType.SPEED) it.speed else it.altitude.toFloat())
                }
            }
        val (value, setValue) = remember { mutableStateOf(speedPoints.firstOrNull()?.x ?: 0f) }
        val selection = SelectionLine(value, null, 4f, Color.Yellow)

        LineGraph(
            modifier = Modifier.fillMaxWidth()
                .height(200.dp),
            data = speedPoints,
            showPoints = speedPoints.size < 10,
            selectionLine = if (!running) selection else null,
        )
        if (running) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                RoundTextButton(
                    onClick = onStopClick,
                    text = "Stop",
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LabeledRadioButton(
                    graphType == GraphType.SPEED,
                    { graphType = GraphType.SPEED },
                    "Speed"
                )

                LabeledRadioButton(
                    graphType == GraphType.ALTITUDE,
                    { graphType = GraphType.ALTITUDE },
                    "Altitude"
                )
            }

            val selected = search(value.toLong() + normalizer, trackEntries)
            val rangeLower = speedPoints.minOfOrNull { it.x } ?: 0f
            val rangeUpper = speedPoints.maxOfOrNull { it.x } ?: 0f
            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Speed(speed = selected?.speed ?: -1f, style = MaterialTheme.typography.h3)
                Altitude(selected?.altitude ?: -1.0, style = MaterialTheme.typography.h3)
            }
            Slider(
                value = value,
                onValueChange = setValue,
                valueRange = rangeLower..rangeUpper,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun LabeledRadioButton(selected: Boolean, onClick: () -> Unit, label: String) {
    Row(Modifier.clickable(onClick = onClick)) {
        RadioButton(
            onClick = { },
            selected = selected,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = label)
    }
}

enum class GraphType {
    SPEED, ALTITUDE
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
            totalDistance = 1337F,
            averageSpeed = 3.67F,
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
            totalDistance = 0F,
            averageSpeed = 0.0F,
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
