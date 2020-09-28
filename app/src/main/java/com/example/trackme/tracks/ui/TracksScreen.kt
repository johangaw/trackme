package com.example.trackme.tracks.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.trackme.common.ui.Distance
import com.example.trackme.common.ui.Speed
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
        Text(track.startTime?.format(DateTimeFormatter.ISO_DATE) ?: "",
             style = MaterialTheme.typography.h5,
             modifier = Modifier.align(Alignment.CenterVertically))
        Distance(track.totalDistance, style = MaterialTheme.typography.h6)
        Speed(track.averageSpeed, style = MaterialTheme.typography.h6)
    }
}


@Composable
@Preview(device = Devices.PIXEL_3, showBackground = true)
fun TracksScreenPreview() {
    val tracks: List<TrackData> = listOf(
        TrackData(1, "Track 1", LocalDateTime.now(), 13f, 3.5f),
        TrackData(1, "Track 2", LocalDateTime.now(), 2.6f, 3.5f),
        TrackData(1, "Track 3", LocalDateTime.now(), 3.6f, 2.5f),
        TrackData(1, "Track 4", LocalDateTime.now(), 13.7f, 0.5f),
    )
    TracksScreen(tracks = tracks, onTrackClick = {}, onNewClick = {})
}