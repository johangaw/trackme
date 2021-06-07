package com.trackme.android.ui.tracks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trackme.android.ui.common.Distance
import com.trackme.android.ui.common.Speed
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TracksScreen(
    tracks: List<TrackData>,
    onTrackClick: (TrackData) -> Unit,
    onTrackDelete: (TrackData) -> Unit,
    onNewClick: () -> Unit,
) {
    // TODO Fixme, make it possible to remove items...
//    val uiStates = rememberUiStateMap(tracks.map { it.id })

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
        LazyColumn(contentPadding = it) {

            items(tracks) { track ->
                TrackRow(
                    track = track,
                    onSelect = onTrackClick,
                    onDelete = onTrackDelete,
//                    uiState = uiStates[track.id]
//                        ?: error("Missing uiState for ${track.id} in $uiStates"),
                )
            }
        }
    }
}

//data class UiState(
//    val key: Any?,
//    val selected: MutableState<Boolean>,
//    val deleting: MutableState<Boolean>,
//)

//@Composable
//fun rememberUiStateMap(ids: List<Any?>): Map<Any?, UiState> {
//    val uiStates = remember { mutableMapOf<Any?, UiState>() }
//    onCommit(ids) {
//        ids.forEach {
//            uiStates.putIfAbsent(it,
//                                 UiState(it,
//                                         mutableStateOf(false),
//                                         mutableStateOf(false)))
//        }
//        (uiStates.keys - ids).forEach { uiStates.remove(it) }
//    }
//    return uiStates
//}

@Composable
fun TrackRow(
    track: TrackData,
    onSelect: (TrackData) -> Unit,
    onDelete: (TrackData) -> Unit,
//    uiState: UiState,
) {
    Box(
//        Modifier.fillMaxWidth().shrinkOut(!uiState.deleting.value) { onDelete(track) }
    ) {
        Card(
            modifier = Modifier
//                .sideDraggable(maxOffset = 75f, key = uiState.key, selectedState = uiState.selected)
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
            onClick = {
//                uiState.deleting.value = true
              },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(75.dp)
        ) {
            Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = "")
        }
    }
}

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