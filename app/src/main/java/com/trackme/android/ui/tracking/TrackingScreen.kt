package com.trackme.android.ui.tracking

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackme.android.data.TrackEntry
import com.trackme.android.ui.common.Altitude
import com.trackme.android.ui.common.Distance
import com.trackme.android.ui.common.Speed
import com.trackme.android.ui.common.ToggleButton
import com.trackme.android.ui.common.map.MapViewContainer
import com.trackme.android.ui.common.map.rememberMapViewWithLifecycle
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
    selectedTrackEntries:  List<TrackEntry>,
    onSelectTrackRange: (range: IntRange) -> Unit
) {
    val running = startedAt != null

    Column(Modifier.fillMaxSize()) {
        if (running) {
            Clock(startedAt, Modifier.align(Alignment.CenterHorizontally))
        } else if (selectedTrackEntries.isNotEmpty()) {
            val start =
                LocalDateTime.ofEpochSecond(selectedTrackEntries.first().time / 1000, 0, ZoneOffset.UTC)
            val end =
                LocalDateTime.ofEpochSecond(selectedTrackEntries.last().time / 1000, 0, ZoneOffset.UTC)
            StaticClock(start, end, Modifier.align(Alignment.CenterHorizontally))
        } else {
            val time = LocalDateTime.MIN
            StaticClock(time, time, Modifier.align(Alignment.CenterHorizontally))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Distance(totalDistance, style = MaterialTheme.typography.h4)
            Speed(averageSpeed, style = MaterialTheme.typography.h4)
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
        val selection = SelectionLine(value, null, 4f, MaterialTheme.colors.secondary)

        LineGraph(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            data = speedPoints,
            showPoints = speedPoints.size < 10,
            selectionLine = if (!running) selection else null,
            onSelectedRangeChange = onSelectTrackRange
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

            val map = rememberMapViewWithLifecycle()
            val current = speedPoints.indexOf(speedPoints.reduceOrNull { left, right ->
                if (abs(left.x - value) < abs(right.x - value)) left else right
            })
                .let {
                    if (it > -1)
                        trackEntries[it]
                    else null
                }
            MapViewContainer(map = map,
                             track = selectedTrackEntries,
                             current = current,
                             modifier = Modifier
                                 .fillMaxWidth()
                                 .height(200.dp),
                             viewportTrack = trackEntries
            )


            Spacer(modifier = Modifier.height(16.dp))
            val selected = search(value.toLong() + normalizer, trackEntries)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                ToggleButton(checked = graphType == GraphType.SPEED,
                             onCheckedChange = { graphType = GraphType.SPEED }) {
                    Speed(speed = selected?.speed ?: -1f, style = MaterialTheme.typography.h4)
                }
                ToggleButton(checked = graphType == GraphType.ALTITUDE,
                             onCheckedChange = { graphType = GraphType.ALTITUDE }) {
                    Altitude(selected?.altitude ?: -1.0, style = MaterialTheme.typography.h4)
                }
            }

            val rangeLower = speedPoints.minOfOrNull { it.x } ?: 0f
            val rangeUpper = speedPoints.maxOfOrNull { it.x } ?: 0f
            Slider(
                value = value,
                onValueChange = setValue,
                valueRange = rangeLower..rangeUpper,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
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
            selectedTrackEntries = trackEntries,
            onSelectTrackRange = {}
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
            trackEntries = trackEntries,
            selectedTrackEntries = trackEntries,
            onSelectTrackRange = {}
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
