package com.example.trackme.tracks.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.tracking.ui.RoundTextButton
import com.example.trackme.tracks.TrackData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TracksScreen(
    tracks: List<TrackData>,
    onTrackClick: (TrackData) -> Unit,
    onNewClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (track in tracks) {
            Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { onTrackClick(track) }).padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(track.name, style = MaterialTheme.typography.h5)
                Text(track.startTime?.format(DateTimeFormatter.ISO_DATE) ?: "", style = MaterialTheme.typography.h6, modifier = Modifier.gravity(Alignment.CenterVertically))
            }
        }

        if (tracks.count() == 0) {
            RoundTextButton(
                modifier = Modifier.gravity(Alignment.CenterHorizontally),
                onClick = onNewClick,
                text = "New",
            )
        }
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