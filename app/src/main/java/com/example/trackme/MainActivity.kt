package com.example.trackme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackme.databinding.ActivityMainBinding
import com.example.trackme.tracking.ui.TrackingScreen


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val TAG: String? = this::class.simpleName



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
            val viewModel: TrackingViewModel = viewModel()

            TrackingScreen(onStartClick = viewModel::startTracking,
                           onStopClick = viewModel::stopTracking,
                           startedAt = viewModel.trackStartedAt,
                           totalLength = 0,
                           currentSpeed = 0.0)
        }
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)

//        binding.startTracking.setOnClickListener {
//            Log.d(TAG, "Starting")
//            startLocationTracking()
//        }
//
//        binding.stopTracking.setOnClickListener {
//            Log.d(TAG, "Stop tracking")
//            stopLocationTracking()
//        }


    }

    private fun stopLocationTracking() {
        stopService(Intent(this, LocationTrackerService::class.java))
    }

    private fun startLocationTracking() {
        if (!hasLocationPermission) {
            requestLocationPermission()
        } else {
            val serviceIntent = Intent(this, LocationTrackerService::class.java)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        );
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
}