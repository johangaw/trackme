package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime

const val DEFAULT_TEXT = "00:00:00"

@Composable
fun Clock(startTime: LocalDateTime?, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf(DEFAULT_TEXT) }
    var runningJob: Job? by remember { mutableStateOf(null) }

    onCommit(startTime) {
        if (startTime == null) {
            runningJob?.cancel()
            text = DEFAULT_TEXT
        } else {
            runningJob = timer(500) {
                val timeSince = Duration.between(startTime, LocalDateTime.now()).seconds
                val hours = timeSince / 3600
                val minutes = (timeSince - (hours * 3600)) / 60
                val seconds = timeSince - hours * 3600 - minutes * 60
                text = "${hours.toString().padStart(2, '0')}:${
                    minutes.toString().padStart(2, '0')
                }:${seconds.toString().padStart(2, '0')}"
            }
            onDispose { runningJob?.cancel() }
        }
    }

    Text(text = text, modifier = modifier, style = MaterialTheme.typography.h3)
}

fun timer(delay: Long, cb: () -> Unit): Job {
    return GlobalScope.launch {
        while (true) {
            cb()
            delay(delay)
        }
    }
}