package com.example.trackme.ui.tracks

import androidx.compose.animation.animatedFloat
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.animation.FlingConfig
import androidx.compose.foundation.animation.fling
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.ui.common.Distance
import com.example.trackme.ui.common.Speed
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
                Icon(Icons.Filled.Add)
            }
        }
    ) {
        LazyColumnFor(items = tracks, contentPadding = it) { track ->
            TrackRow(track = track, onSelect = onTrackClick, onDelete = onTrackDelete)
        }
    }
}

@Composable
fun TrackRow(track: TrackData, onSelect: (TrackData) -> Unit, onDelete: (TrackData) -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Card(modifier = Modifier.sideDraggable().padding(bottom = 16.dp),
             elevation = 4.dp) {
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
        IconButton(onClick = { onDelete(track) },
                   modifier = Modifier
                       .align(Alignment.CenterEnd)
                       .preferredWidth(75.dp)
        ) {
            Icon(Icons.Default.Delete, tint = Color.Red)
        }
    }
}

@Composable
fun Modifier.sideDraggable(
    maxOffset: Float = -75f,
    onEnd: (finished: Boolean) -> Unit = {},
): Modifier {
    val offset = animatedFloat(0f)
    return this
        .offset(offset.value.dp)
        .draggable(
            Orientation.Horizontal,
            onDrag = {
                offset.snapTo(offset.value + it / 2f)
            },
            onDragStopped = {
                val config = FlingConfig(listOf(maxOffset, 0f).sorted())
                offset.fling(
                    -it,
                    config,
                    onAnimationEnd = { _, animationValue, _ ->
                        onEnd(animationValue != 0f)
                    }
                )
            }
        )
}


@Composable
@Preview(device = Devices.PIXEL_3, showBackground = true, showDecoration = true)
fun TracksScreenPreview() {
    val tracks: List<TrackData> = listOf(
        TrackData(1, "Track 1", LocalDateTime.now(), 13f, 3.5f),
        TrackData(1, "Track 2", LocalDateTime.now(), 2.6f, 3.55f),
        TrackData(1, "Track 3", LocalDateTime.now(), 3.6f, 2.5f),
        TrackData(1, "Track 4", LocalDateTime.now(), 13.7f, 0.5f),
    )
    TracksScreen(tracks = tracks, onTrackClick = {}, onNewClick = {}, onTrackDelete = {})
}