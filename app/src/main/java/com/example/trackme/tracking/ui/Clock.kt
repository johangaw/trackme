package com.example.trackme.tracking.ui

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import java.time.Duration
import java.time.LocalDateTime


@Composable
fun Clock(startTime: LocalDateTime, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf("00:00:00") }

    onActive {
        val timerJob = timer(500) {
            val timeSince = Duration.between(startTime, LocalDateTime.now()).seconds
            val hours = timeSince / 3600
            val minutes = (timeSince - (hours * 3600)) / 60
            val seconds = timeSince - hours * 3600 - minutes * 60
            text = "${hours.toString().padStart(2, '0')}:${
                minutes.toString().padStart(2, '0')
            }:${seconds.toString().padStart(2, '0')}"
        }

        onDispose { timerJob.cancel() }
    }

    Text(text = text, modifier = modifier, style = MaterialTheme.typography.h3)
}