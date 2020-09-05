package com.example.trackme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.trackme.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val TAG: String? = this::class.simpleName

    private lateinit var binding: ActivityMainBinding

    private val hasLocationPermission: Boolean
        get() {
            return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startTracking.setOnClickListener {
            Log.d(TAG, "Starting")
            startLocationTracking()
        }
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
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationTracking()
            }
        }
    }
}