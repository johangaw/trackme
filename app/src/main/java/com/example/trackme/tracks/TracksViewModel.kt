package com.example.trackme.tracks

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.Track
import java.time.LocalDateTime

class TracksViewModel(private val database: AppDatabase) : ViewModel() {
    val tracks: LiveData<List<TrackData>> = Transformations.
        map(database.trackDao().getAllAndObserve()) { tracks -> tracks.map { TrackData(it.id, it.name, LocalDateTime.now()) }}

    suspend fun newTrack(): Track {
        val trackId = database.trackDao().insert(Track()).first()
        return database.trackDao().get(trackId)
    }
}

data class TrackData(
    val id: Long,
    val name: String,
    val startTime: LocalDateTime?
)

@Suppress("UNCHECKED_CAST")
class TracksViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TracksViewModel(AppDatabase.getInstance(application)) as T
    }
}