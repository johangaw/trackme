package com.trackme.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.trackme.android.services.LocationTrackerService
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory(application)
    }

    private val hasLocationPermission: Boolean
        get() {
            return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }


    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                requestLocationTracking()
            }
        }

    @ExperimentalMaterialApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val startOnTrack = intent.getLongExtra(EXTRA_TRACK_ID, -1)
        viewModel.focusTrack(startOnTrack)
        setContent {
            val startTrackOn by viewModel.focusTrackRequests.observeAsState(FocusTrackRequest(-1))
            App(
                onBackPressedDispatcher,
                this::requestLocationTracking,
                this::stopLocationTracking,
                startTrackOn
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val newFocusedTrack = intent?.getLongExtra(EXTRA_TRACK_ID, -1) ?: -1
        if (newFocusedTrack >= 0)
            viewModel.focusTrack(newFocusedTrack)
    }

    private fun stopLocationTracking() {
        stopService(Intent(this, LocationTrackerService::class.java))
    }

    private fun requestLocationTracking() {
        if (!hasLocationPermission) {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            lifecycleScope.launch {
                val newTrack = viewModel.newTrack()
                startLocationTracking(newTrack.id)
                // TODO trigger deep link navigation to track
            }
        }
    }

    private fun startLocationTracking(trackId: Long) {
        val serviceIntent = Intent(this@MainActivity, LocationTrackerService::class.java)
        serviceIntent.putExtra(LocationTrackerService.EXTRA_TRACK_ID, trackId)
        startForegroundService(serviceIntent)
    }

    companion object {
        /**
         * If provided the tracking screen for the selected track will be shown from the start
         */
        const val EXTRA_TRACK_ID = "com.example.trackme.TRACK_ID_EXTRA"
    }
}