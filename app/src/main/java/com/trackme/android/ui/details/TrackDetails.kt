package com.trackme.android.ui.details

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.trackme.android.data.TrackEntry
import com.trackme.android.ui.common.Altitude
import com.trackme.android.ui.common.Speed
import com.trackme.android.ui.common.ToggleButton
import com.trackme.android.ui.common.map.MapViewContainer
import com.trackme.android.ui.common.map.rememberMapViewWithLifecycle
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.math.absoluteValue

enum class GraphType {
    SPEED, ALTITUDE
}

@Composable
fun TrackDetails(
    totalDistance: Float,
    averageSpeed: Float,
    trackEntries: List<TrackEntry>,
    selectedTrackEntries: List<TrackEntry>,
    onSelectTrackRange: (range: IntRange) -> Unit,
) {
    var graphType: GraphType by remember { mutableStateOf(GraphType.SPEED) }
    val normalizer = trackEntries.firstOrNull()?.time ?: 0
    val selectionRangeLower = trackEntries.minOfOrNull { (it.time - normalizer).toFloat() } ?: 0f
    val selectionRangeUpper = trackEntries.maxOfOrNull { (it.time - normalizer).toFloat() } ?: 0f
    val (selectionValue, setSelectionValue) = remember { mutableStateOf(selectionRangeLower) }
    val selectedEntry =
        trackEntries.minByOrNull { (it.time - normalizer - selectionValue.toLong()).absoluteValue }
    val graphPoints =
        remember(trackEntries, graphType, normalizer) {
            trackEntries.map {
                Point((it.time - normalizer).toFloat(),
                      if (graphType == GraphType.SPEED) it.speed else it.altitude.toFloat())
            }
        }

    Column(Modifier.fillMaxSize()) {
        TotalTrackTime(trackEntries, Modifier.align(Alignment.CenterHorizontally))
        DistanceSpeedRow(totalDistance, averageSpeed)

        LineGraph(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            data = graphPoints,
            showPoints = graphPoints.size < 10,
            selectionLine = SelectionLine(selectionValue, null, 4f, MaterialTheme.colors.secondary),
            onSelectedRangeChange = onSelectTrackRange
        )

        Spacer(modifier = Modifier.height(16.dp))

        MapViewContainer(map = rememberMapViewWithLifecycle(),
                         track = selectedTrackEntries,
                         current = selectedEntry,
                         modifier = Modifier
                             .fillMaxWidth()
                             .height(200.dp),
                         viewportTrack = trackEntries
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ToggleButton(checked = graphType == GraphType.SPEED,
                         onCheckedChange = { graphType = GraphType.SPEED }) {
                Speed(speed = selectedEntry?.speed ?: -1f, style = MaterialTheme.typography.h4)
            }
            ToggleButton(checked = graphType == GraphType.ALTITUDE,
                         onCheckedChange = { graphType = GraphType.ALTITUDE }) {
                Altitude(selectedEntry?.altitude ?: -1.0, style = MaterialTheme.typography.h4)
            }
        }

        Slider(
            value = selectionValue,
            onValueChange = setSelectionValue,
            valueRange = selectionRangeLower..selectionRangeUpper,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun TotalTrackTime(trackEntries: List<TrackEntry>, modifier: Modifier = Modifier) {
    if (trackEntries.isNotEmpty()) {
        val start =
            LocalDateTime.ofEpochSecond(trackEntries.first().time / 1000,
                                        0,
                                        ZoneOffset.UTC)
        val end =
            LocalDateTime.ofEpochSecond(trackEntries.last().time / 1000,
                                        0,
                                        ZoneOffset.UTC)
        StaticClock(start, end, modifier)
    } else {
        val time = LocalDateTime.MIN
        StaticClock(time, time, modifier)
    }
}