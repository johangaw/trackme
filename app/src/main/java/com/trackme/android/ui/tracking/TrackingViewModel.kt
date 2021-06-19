package com.trackme.android.ui.tracking

import android.content.Context
import androidx.lifecycle.*
import com.trackme.android.data.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class TrackingViewModel(
    database: AppDatabase,
) : ViewModel() {

    private val _trackId: MutableLiveData<Long> = MutableLiveData()

    val activeTrackEntries: LiveData<List<TrackEntry>> = _trackId.switchMap {
        database.trackEntryDao().getAllAndObserve(it)
    }

    val trackStartedAt: LiveData<LocalDateTime?> = activeTrackEntries.map {
        it.firstOrNull()?.time?.let { timeStamp ->
            LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())
        } ?: LocalDateTime.now()
    }

    val totalDistance: LiveData<Float> = activeTrackEntries.map {
        it?.let { totalDistance(it) } ?: 0f
    }

    val averageSpeed: LiveData<Float> = activeTrackEntries.map {
        val totalDistance = totalDistance(it)
        averageSpeed(it, totalDistance)
    }

    val activeTrack: LiveData<Track> = _trackId.switchMap {
        database.trackDao().getAndObserve(it)
    }

    fun setTrackId(id: Long) {
        _trackId.postValue(id)
    }
}

@Suppress("UNCHECKED_CAST")
class TrackingViewModelFactory(private val applicationContext: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val database = AppDatabase.getInstance(applicationContext)
        return TrackingViewModel(database) as T
    }
}

