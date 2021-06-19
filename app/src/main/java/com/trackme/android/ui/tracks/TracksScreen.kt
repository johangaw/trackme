package com.trackme.android.ui.tracks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.trackme.android.ui.common.Distance
import com.trackme.android.ui.common.Speed
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun TracksScreen(
    tracks: List<TrackData>,
    onTrackClick: (TrackData) -> Unit,
    onTrackDelete: (TrackData) -> Unit,
    onNewClick: () -> Unit,
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.semantics { text = AnnotatedString("Create new track") },
                onClick = onNewClick
            ) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        }
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            tracks.forEach { track ->
                key(track.id) {
                    TrackRow(
                        track = track,
                        onSelect = onTrackClick,
                        onDelete = onTrackDelete,
                    )

                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun TrackRow(
    track: TrackData,
    onSelect: (TrackData) -> Unit,
    onDelete: (TrackData) -> Unit,
    swipeableState: SwipeableState<String> = rememberSwipeableState("hidden"),
) {
    val anchors = mapOf(0f to "hidden", 300f to "visible")
    var deleted by remember { mutableStateOf(false) }

    Box(
        Modifier
            .shrinkOut(!deleted, onEnd = { onDelete(track) })
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                resistance = ResistanceConfig(300f, 2f, 2f)
            )
    ) {
        IconButton(
            onClick = {
                deleted = true
            },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(75.dp)
        ) {
            Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = "")
        }
        Card(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .padding(bottom = 16.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .clickable(onClick = { onSelect(track) }),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(track.startTime?.format(DateTimeFormatter.ISO_DATE) ?: "",
                     style = MaterialTheme.typography.h5,
                     modifier = Modifier.align(Alignment.CenterVertically))
                Distance(track.totalDistance, style = MaterialTheme.typography.h6)
                Speed(track.averageSpeed, style = MaterialTheme.typography.h6)
            }
        }
    }
}

@ExperimentalMaterialApi
@Preview(device = Devices.PIXEL_3, showBackground = true, showSystemUi = true)
@Composable
fun TracksScreenPreview() {
    val tracks: List<TrackData> = listOf(
        TrackData(1, "Track 1", LocalDateTime.now(), 13f, 3.5f),
        TrackData(1, "Track 2", LocalDateTime.now(), 2.6f, 3.55f),
        TrackData(1, "Track 3", LocalDateTime.now(), 3.6f, 2.5f),
        TrackData(1, "Track 4", LocalDateTime.now(), 13.7f, 0.5f),
    )
    TracksScreen(tracks = tracks, onTrackClick = {}, onNewClick = {}, onTrackDelete = {})
}