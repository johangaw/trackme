package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview

@Composable
fun TrackingScreen(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    isTracking: Boolean,
) {

    if (!isTracking) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalGravity = Alignment.CenterHorizontally,
        ) {
            RoundTextButton(
                onClick = onStartClick,
                text = "Start",
            )
        }

    } else {
        Column(Modifier.fillMaxSize()) {


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OutlinedButton(onClick = onStartClick) {
                    Text("Start")
                }

                OutlinedButton(onClick = onStopClick) {
                    Text("Stop")
                }
            }
        }
    }

}

@Composable
@Preview(
    name = "Tracking in progress",
    device = Devices.PIXEL_3,
    showBackground = true,
)
fun TrackingScreenPreview() {
    MaterialTheme {
        TrackingScreen(onStartClick = {}, onStopClick = {}, true)
    }
}

@Composable
@Preview(
    name = "Tracking not in progress",
    device = Devices.PIXEL_3,
    showBackground = true,
)
fun NotTrackingScreenPreview() {
    MaterialTheme {
        TrackingScreen(onStartClick = {}, onStopClick = {}, false)
    }
}