package com.example.trackme.tracks.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Device
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import com.example.trackme.data.Track
import com.example.trackme.tracking.ui.RoundTextButton

@Composable
fun TracksScreen(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onNewClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        for (track in tracks) {
            Row(modifier = Modifier.fillMaxWidth().clickable(onClick = { onTrackClick(track) })) {
                Text(track.id.toString(), modifier = Modifier.preferredWidth(24.dp))
                Text(track.name)
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
@Preview(device = Devices.PIXEL_3)
fun TracksScreenPreview() {
    val tracks: List<Track> = listOf(
        Track(1, "Track 1", false),
        Track(1, "Track 2", false),
        Track(1, "Track 3", false),
        Track(1, "Track 4", false),
    )
    TracksScreen(tracks = tracks, onTrackClick = {}, onNewClick = {})

}