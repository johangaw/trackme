package com.example.trackme.ui.tracking

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.ui.tooling.preview.Preview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

const val DEFAULT_TEXT = "00:00:00"

@Composable
fun Clock(startTime: LocalDateTime?, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("") }
    timer(startTime != null, 500) {
        text = formatTime(startTime!!, LocalDateTime.now())
    }
    onCommit(startTime) {
        if(startTime == null) text = DEFAULT_TEXT
    }

    Text(text = text, modifier = modifier, style = MaterialTheme.typography.h3)
}

fun formatTime(start: LocalDateTime, end: LocalDateTime): String {
    val timeSince = Duration.between(start, end).seconds
    val hours = timeSince / 3600
    val minutes = (timeSince - (hours * 3600)) / 60
    val seconds = timeSince - hours * 3600 - minutes * 60
    return "${hours.toString().padStart(2, '0')}:${
        minutes.toString().padStart(2, '0')
    }:${seconds.toString().padStart(2, '0')}"
}

@Composable
fun timer(running: Boolean, delay: Long, cb: () -> Unit) {
    launchInComposition(running) {
        while (running) {
            cb()
            delay(delay)
        }
    }
}

@Composable
fun StaticClock(
    startTime: LocalDateTime,
    endTime: LocalDateTime,
    modifier: Modifier = Modifier,
) {
    val text = formatTime(startTime, endTime)
    Text(text = text, modifier = modifier, style = MaterialTheme.typography.h3)
}

@Composable
@Preview(showBackground = true)
fun StaticClockPreview() {
    StaticClock(
        LocalDateTime.of(2020, 1, 1, 0, 0),
        LocalDateTime.of(2020, 1, 1, 1, 34, 24),
    )
}

