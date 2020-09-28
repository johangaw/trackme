package com.example.trackme.tracks.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ColumnScope.gravity
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.tracks.TrackData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TracksScreen(
    tracks: List<TrackData>,
    onTrackClick: (TrackData) -> Unit,
    onNewClick: () -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewClick
            ) {
                Icon(Icons.Filled.Add)
            }
        }
    ) {
        LazyColumnFor(items = tracks, contentPadding = it) { track ->
            TrackRow(track = track, onClick = onTrackClick)
        }
    }
}

@Composable
fun TrackRow(track: TrackData, onClick: (TrackData) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(track) })
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(track.name, style = MaterialTheme.typography.h5)
        Text(track.startTime?.format(DateTimeFormatter.ISO_DATE) ?: "",
             style = MaterialTheme.typography.h6,
             modifier = Modifier.align(Alignment.CenterVertically))
    }
}


@Composable
@Preview(device = Devices.PIXEL_3, showBackground = true)
fun TracksScreenPreview() {
    val tracks: List<TrackData> = listOf(
        TrackData(1, "Track 1", LocalDateTime.now()),
        TrackData(1, "Track 2", LocalDateTime.now()),
        TrackData(1, "Track 3", LocalDateTime.now()),
        TrackData(1, "Track 4", LocalDateTime.now()),
    )
    TracksScreen(tracks = tracks, onTrackClick = {}, onNewClick = {})

}