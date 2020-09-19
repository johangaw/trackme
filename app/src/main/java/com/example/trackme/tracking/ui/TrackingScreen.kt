package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Devices
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.math.roundToInt

@Composable
fun TrackingScreen(
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    startedAt: LocalDateTime?,
    totalLength: Float,
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
                Text(text = formatDistance(totalLength), style = MaterialTheme.typography.h3)
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

fun formatDistance(distance: Float): String {
    return "${((distance / 1000f) * 100f).roundToInt() / 100f} km"
}

fun timer(delay: Long, cb: () -> Unit): Job {
    return GlobalScope.launch {
        while (true) {
            cb()
            delay(delay)
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
    val trackStartedAt = LocalDateTime.now().minusHours(1).minusMinutes(2).minusSeconds(36)
    MaterialTheme {
        TrackingScreen(
            onStartClick = {},
            onStopClick = {},
            startedAt = trackStartedAt,
            totalLength = 1337F,
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
            totalLength = 0F,
            currentSpeed = 0.0,
        )
    }
}