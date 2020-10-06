package com.example.trackme

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.Track

class MainActivityViewModel(private val database: AppDatabase) : ViewModel() {

    private val _focusTrackRequests = MutableLiveData<FocusTrackRequest>()
    val focusTrackRequests: LiveData<FocusTrackRequest> = _focusTrackRequests

    fun focusTrack(trackId: Long) {
        _focusTrackRequests.postValue(FocusTrackRequest(trackId))
    }

    suspend fun newTrack(): Track {
        val trackId = database.trackDao().insert(Track()).first()
        return database.trackDao().get(trackId)
    }
}

class FocusTrackRequest(val trackId: Long)

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainActivityViewModel(AppDatabase.getInstance(application)) as T
    }
}