package com.example.trackme.tracks

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.Track
import com.example.trackme.data.totalDistance
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
                    if (entries.isNotEmpty()) totalDistance / (entries.last().time - entries.first().time) * 1000f else 0f
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

    suspend fun newTrack(): Track {
        val trackId = database.trackDao().insert(Track()).first()
        return database.trackDao().get(trackId)
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
class TracksViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TracksViewModel(AppDatabase.getInstance(application)) as T
    }
}