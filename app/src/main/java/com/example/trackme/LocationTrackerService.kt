package com.example.trackme

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.asTrackEntry
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LocationTrackerService : Service() {

    val CHANNEL_ID = "LocationTrackerServiceChannel"
    val CHANNEL_NAME = "LocationTrackerServiceChannel"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var appDatabase: AppDatabase
    private var tracking = false
    private val TAG: String? = this::class.simpleName

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            GlobalScope.launch {
                appDatabase.trackEntryDao()
                    .insert(locationResult.locations.map { it.asTrackEntry() })
            }

            locationResult.locations.forEach {
                Log.d(TAG, it.toString())

            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        appDatabase = AppDatabase.getInstance(application)


    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (!tracking) {
            tracking = true

            showTrackingNotification()

            fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tracking) {
            tracking = false
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun createLocationRequest(): LocationRequest? {
        return LocationRequest.create()?.apply {
            interval = 2_000
            fastestInterval = 1_000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun showTrackingNotification() {
        val chan = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setOngoing(true)
            .setContentTitle("LocationTrackerService")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_NAVIGATION)
            .setContentIntent(createShowProgressIntent())

        startForeground(1, notificationBuilder.build())
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createShowProgressIntent(): PendingIntent {
        val showProgress = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(this, 0, showProgress, 0);
    }
}