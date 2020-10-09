package com.example.trackme.ui.tracks

import android.util.Log
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout
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
import kotlin.math.roundToInt

@Composable
fun TracksScreen(
    tracks: List<TrackData>,
    onTrackClick: (TrackData) -> Unit,
    onTrackDelete: (TrackData) -> Unit,
    onNewClick: () -> Unit,
) {
    val uiStates = rememberUiStateMap(tracks.map { it.id })

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
            TrackRow(
                track = track,
                onSelect = onTrackClick,
                onDelete = onTrackDelete,
                uiState = uiStates[track.id]
                    ?: error("Missing uiState for ${track.id} in $uiStates"),
            )
        }
    }
}

data class UiState(
    val key: Any?,
    val selected: MutableState<Boolean>,
    val deleting: MutableState<Boolean>,
)

@Composable
fun rememberUiStateMap(ids: List<Any?>): Map<Any?, UiState> {
    val uiStates = remember { mutableMapOf<Any?, UiState>() }
    onCommit(ids) {
        ids.forEach {
            uiStates.putIfAbsent(it,
                                 UiState(it,
                                         mutableStateOf(false),
                                         mutableStateOf(false)))
        }
        (uiStates.keys - ids).forEach { uiStates.remove(it) }
    }
    return uiStates
}

@Composable
fun TrackRow(
    track: TrackData,
    onSelect: (TrackData) -> Unit,
    onDelete: (TrackData) -> Unit,
    uiState: UiState,
) {
    Box(
        Modifier.fillMaxWidth().shrinkOut(!uiState.deleting.value) { onDelete(track) }
    ) {
        Card(
            modifier = Modifier
                .sideDraggable(state = uiState)
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
        IconButton(
            onClick = { uiState.deleting.value = true },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .preferredWidth(75.dp)
        ) {
            Icon(Icons.Default.Delete, tint = Color.Red)
        }
    }
}

@Composable
fun Modifier.shrinkOut(visible: Boolean, onEnd: () -> Unit = { }): Modifier {
    val scaleHeightAnimation = animatedFloat(initVal = 1f)

    onCommit(visible) {
        when (visible) {
            true -> scaleHeightAnimation.snapTo(1f)
            false -> scaleHeightAnimation.animateTo(0f) { _, _ -> onEnd() }
        }
    }

    return this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        val scaleHeight = scaleHeightAnimation.value
        this.layout(placeable.width, (placeable.height.toFloat() * scaleHeight).roundToInt()) {
            placeable.place(0, 0)
        }
    }
}

@Composable
fun Modifier.sideDraggable(
    maxOffset: Float = -75f,
    onEnd: (finished: Boolean) -> Unit = {},
    state: UiState,
): Modifier {
    val offset = animatedFloat(0f)
    onCommit(state.key) {
        offset.snapTo(if (state.selected.value) maxOffset else 0f)
    }
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
                    config
                ) { reason, animationValue, _ ->
                    Log.d("UGG", "flingEnd  $reason")
                    val selected = animationValue != 0f
                    state.selected.value = selected
                    onEnd(selected)
                }
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