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
import kotlin.math.abs

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
    val speedPoints =
        remember(trackEntries, graphType) {
            val normalizer = trackEntries.firstOrNull()?.time ?: 0
            trackEntries.map {
                Point((it.time - normalizer).toFloat(),
                      if (graphType == GraphType.SPEED) it.speed else it.altitude.toFloat())
            }
        }
    val (selectionValue, setSelectionValue) = remember {
        mutableStateOf(speedPoints.firstOrNull()?.x ?: 0f)
    }
    val selection = SelectionLine(selectionValue, null, 4f, MaterialTheme.colors.secondary)

    Column(Modifier.fillMaxSize()) {
        TotalTrackTime(trackEntries, Modifier.align(Alignment.CenterHorizontally))
        DistanceSpeedRow(totalDistance, averageSpeed)

        LineGraph(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            data = speedPoints,
            showPoints = speedPoints.size < 10,
            selectionLine = selection,
            onSelectedRangeChange = onSelectTrackRange
        )

        Spacer(modifier = Modifier.height(16.dp))

        val map = rememberMapViewWithLifecycle()
        val current = speedPoints.indexOf(speedPoints.reduceOrNull { left, right ->
            if (abs(left.x - selectionValue) < abs(right.x - selectionValue)) left else right
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

        val normalizer = trackEntries.firstOrNull()?.time ?: 0
        val selected = trackEntries.minByOrNull { abs(it.time - selectionValue.toLong() + normalizer) }

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
            value = selectionValue,
            onValueChange = setSelectionValue,
            valueRange = rangeLower..rangeUpper,
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