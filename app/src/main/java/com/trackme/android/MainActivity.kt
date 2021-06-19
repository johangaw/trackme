package com.trackme.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.trackme.android.services.LocationTrackerService
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private var onTrackingStartedCallback: ((newTrackId: Long) -> Unit)? = null
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
        setContent {
            App(
                this::requestLocationTracking,
                this::stopLocationTracking,
            )
        }
    }

    private fun stopLocationTracking() {
        stopService(Intent(this, LocationTrackerService::class.java))
    }

    private fun requestLocationTracking(onTrackingStarted: ((newTrackId: Long) -> Unit)? = null) {
        if(onTrackingStarted !== null) onTrackingStartedCallback = onTrackingStarted

        if (!hasLocationPermission) {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            lifecycleScope.launch {
                val newTrack = viewModel.newTrack()
                startLocationTracking(newTrack.id)
                onTrackingStartedCallback?.invoke(newTrack.id)
                onTrackingStartedCallback = null
            }
        }
    }

    private fun startLocationTracking(trackId: Long) {
        val serviceIntent = Intent(this@MainActivity, LocationTrackerService::class.java)
        serviceIntent.putExtra(LocationTrackerService.EXTRA_TRACK_ID, trackId)
        startForegroundService(serviceIntent)
    }
}