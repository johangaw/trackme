package com.example.trackme.tracking

import android.app.Application
import androidx.lifecycle.*
import com.example.trackme.data.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TrackingViewModel(
    private val database: AppDatabase,
    private val trackId: Long,
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
            it?.map { entry -> entry.asLocation() }
                ?.zipWithNext { l1, l2 -> l1.distanceTo(l2) }
                ?.sum() ?: 0F
        }
    val activeTrack: LiveData<Track> = database.trackDao().getAndObserve(trackId)

    fun startTracking() {
        viewModelScope.launch {
            database.trackDao().updateActivity(TrackActivity(trackId, true))
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            database.trackDao().updateActivity(TrackActivity(trackId, false))
        }
    }
}

@Suppress("UNCHECKED_CAST")
class TrackingViewModelFactory(private val application: Application, private val trackId: Long) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val database = AppDatabase.getInstance(application)
        return TrackingViewModel(database, trackId) as T
    }
}

