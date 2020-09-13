package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun TrackingScreen(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalLength: Int,
    currentSpeed: Double,
) {

    if (startedAt == null) {
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
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Clock(startedAt)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = "$totalLength m", style = MaterialTheme.typography.h3)
                Text(text = "$currentSpeed m/s", style = MaterialTheme.typography.h3)
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalGravity = Alignment.CenterVertically,
            ) {
                RoundTextButton(
                    onClick = onStopClick,
                    text = "Stop",
                )
            }
        }
    }
}

@Composable
fun Clock(startTime: LocalDateTime, modifier: Modifier = Modifier) {
    val text = remember(LocalDateTime.now()) {
        val timeSince = Duration.between(startTime, LocalDateTime.now()).seconds
        val hours = timeSince / 3600
        val minutes = (timeSince - (hours * 3600)) / 60
        val seconds = timeSince - hours * 3600 - minutes * 60
        "${hours.toString().padStart(2, '0')}:${
            minutes.toString().padStart(2, '0')
        }:${seconds.toString().padStart(2, '0')}"
    }

    Text(text = text, modifier = modifier, style = MaterialTheme.typography.h3)
}

@Composable
@Preview(
    name = "Tracking in progress",
    device = Devices.PIXEL_3,
    showBackground = true,
)
fun TrackingScreenPreview() {
    val trackStartedAt = LocalDateTime.now().minusHours(1).minusMinutes(2).minusSeconds(36)
    MaterialTheme {
        TrackingScreen(
            onStartClick = {},
            onStopClick = {},
            startedAt = trackStartedAt,
            totalLength = 1337,
            currentSpeed = 3.67
        )
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
        TrackingScreen(
            onStartClick = {},
            onStopClick = {},
            startedAt = null,
            totalLength = 0,
            currentSpeed = 0.0,
        )
    }
}