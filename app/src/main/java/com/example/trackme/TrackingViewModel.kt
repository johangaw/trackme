package com.example.trackme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

class TrackingViewModel() : ViewModel() {

    var trackStartedAt: LocalDateTime? by mutableStateOf<LocalDateTime?>( null )
        private set

    fun startTracking() {
        trackStartedAt = LocalDateTime.now()
    }

    fun stopTracking() {
        trackStartedAt = null
    }
}

