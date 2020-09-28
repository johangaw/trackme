package com.example.trackme.tracking

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.Track
import com.example.trackme.data.TrackEntry
import com.example.trackme.data.totalDistance
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TrackingViewModel(
    database: AppDatabase,
    trackId: Long,
) : ViewModel() {

    val activeTrackEntries: LiveData<List<TrackEntry>> =
        database.trackEntryDao().getAllAndObserve(trackId)

    val trackStartedAt: LiveData<LocalDateTime?> = Transformations.map(activeTrackEntries) {
        it.firstOrNull()?.time?.let { timeStamp ->
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())
        } ?: LocalDateTime.now()
    }

    val totalDistance: LiveData<Float> =
        Transformations.map(activeTrackEntries) {
            it?.let { totalDistance(it) } ?: 0f
        }

    val activeTrack: LiveData<Track> = database.trackDao().getAndObserve(trackId)
}

@Suppress("UNCHECKED_CAST")
class TrackingViewModelFactory(private val application: Application, private val trackId: Long) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val database = AppDatabase.getInstance(application)
        return TrackingViewModel(database, trackId) as T
    }
}

