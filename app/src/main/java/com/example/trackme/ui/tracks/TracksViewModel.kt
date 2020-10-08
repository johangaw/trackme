package com.example.trackme.ui.tracks

import android.content.Context
import androidx.lifecycle.*
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.totalDistance
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class TracksViewModel(private val database: AppDatabase) : ViewModel() {
    val tracks: LiveData<List<TrackData>> =
        Transformations.map(database.trackDao().getAllWithTracksAndObserve()) { trackWithEntries ->
            trackWithEntries.map { it ->
                val startTime = it.entries.firstOrNull()
                    ?.let { LocalDateTime.ofEpochSecond(it.time / 1000, 0, ZoneOffset.UTC) }
                val totalDistance = totalDistance(it.entries)
                val averageSpeed = it.entries.let { entries ->
                    if (entries.size > 1) totalDistance / (entries.last().time - entries.first().time) * 1000f else 0f
                }
                TrackData(
                    it.track.id,
                    it.track.name,
                    startTime,
                    totalDistance,
                    averageSpeed,
                )
            }
        }

    fun removeTrack(trackId: Long) {
        viewModelScope.launch {
            val track = database.trackDao().get(trackId)
            database.trackDao().remove(track)
        }
    }
}

data class TrackData(
    val id: Long,
    val name: String,
    val startTime: LocalDateTime?,

    /**
     * Distance in m
     */
    val totalDistance: Float,

    /**
     * Speed in m/s
     */
    val averageSpeed: Float,
)

@Suppress("UNCHECKED_CAST")
class TracksViewModelFactory(private val applicationContext: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TracksViewModel(AppDatabase.getInstance(applicationContext)) as T
    }
}