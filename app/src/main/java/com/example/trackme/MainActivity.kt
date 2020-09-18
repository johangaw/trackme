package com.example.trackme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.trackme.tracking.ui.TrackingScreen
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {
    private val viewModel: TrackingViewModel by viewModels { TrackingViewModelFactory(application) }

    private val hasLocationPermission: Boolean
        get() {
            return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val totalDistance by viewModel.totalDistance.observeAsState()
            val trackStartedAt by viewModel.trackStartedAt.observeAsState()

            TrackingScreen(
                onStartClick = {
                    requestLocationTracking()
                },
                onStopClick = {
                    stopLocationTracking()
                    viewModel.stopTracking()
                },
                startedAt = trackStartedAt,
                totalLength = totalDistance ?: 0F,
                currentSpeed = 0.0
            )
        }
    }

    private fun stopLocationTracking() {
        stopService(Intent(this, LocationTrackerService::class.java))
    }

    private fun requestLocationTracking() {
        if (!hasLocationPermission) {
            requestLocationPermission()
        } else {
            startLocationTracking()
        }
    }

    private fun startLocationTracking() {
        lifecycleScope.launch {
            val track = viewModel.newTrack()
            val serviceIntent = Intent(this@MainActivity, LocationTrackerService::class.java)
            serviceIntent.putExtra(LocationTrackerService.EXTRA_TRACK_ID, track.id)
            ContextCompat.startForegroundService(this@MainActivity, serviceIntent)
            viewModel.startTracking()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    @Override
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationTracking()
            }
        }
    }

    companion object {
        private const val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }
}