package com.example.trackme.tracks

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.Track

class TracksViewModel(private val database: AppDatabase) : ViewModel() {
    val tracks: LiveData<List<Track>> = database.trackDao().getAllAndObserve()

    suspend fun newTrack(): Track {
        val trackId = database.trackDao().insert(Track()).first()
        return database.trackDao().get(trackId)
    }
}

@Suppress("UNCHECKED_CAST")
class TracksViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TracksViewModel(AppDatabase.getInstance(application)) as T
    }
}