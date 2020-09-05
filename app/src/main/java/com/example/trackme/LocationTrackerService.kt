package com.example.trackme

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*


class LocationTrackerService : Service() {

    val CHANNEL_ID = "LocationTrackerServiceChannel"
    val CHANNEL_NAME = "LocationTrackerServiceChannel"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var tracking = false
    private val TAG: String? = this::class.simpleName


    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if(!tracking) {
            tracking = true

            showTrackingNotification()

            fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                createLocationCallback(),
                Looper.getMainLooper()
            )
        }

        return START_STICKY
    }

    private fun createLocationCallback(): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Log.d(TAG, location.toString())
                }
            }
        }
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun showTrackingNotification() {
        val chan = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
//        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle("LocationTrackerService")
            .setCategory(Notification.CATEGORY_NAVIGATION)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}