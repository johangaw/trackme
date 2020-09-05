package com.example.trackme

import android.annotation.SuppressLint
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*


class LocationTrackerService : Service() {

    val CHANNEL_ID = "ForegroundServiceChannel"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tracking = false


    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val res = super.onStartCommand(intent, flags, startId)


        if(!tracking) {
            tracking = true

            val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("LocationTrackerService")
                .build()
            startForeground(1, notification)

            val locationRequest = LocationRequest().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: return
                    for (location in locationResult.locations){
                        Log.d(this::class.simpleName, location.toString())
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )

        }

        return res
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }
}