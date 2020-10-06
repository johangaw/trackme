package com.example.trackme.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.trackme.MainActivity
import com.example.trackme.R
import com.example.trackme.data.AppDatabase
import com.example.trackme.data.TrackActivity
import com.example.trackme.data.asTrackEntry
import com.google.android.gms.location.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class LocationTrackerService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var appDatabase: AppDatabase
    private var tracking = false
    private var trackId: Long = -1

    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult ?: return
            GlobalScope.launch {
                appDatabase.trackEntryDao()
                    .insert(locationResult.locations.map { it.asTrackEntry(trackId) })
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

        val newTrackingId = intent?.getLongExtra(EXTRA_TRACK_ID, -1) ?: -1
        if (trackId > 0) {
            toggleTrackActivity(trackId, false)
        }
        require(newTrackingId >= 0
        ) { "LocationTrackerService must be started with EXTRA_TRACK_ID intent param" }
        trackId = newTrackingId
        toggleTrackActivity(trackId, true)

        if (!tracking) {
            tracking = true

            showTrackingNotification()

            fusedLocationClient.requestLocationUpdates(
                createLocationRequest(),
                locationCallback,
                Looper.getMainLooper()
            )
        }

        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tracking) {
            tracking = false
            fusedLocationClient.removeLocationUpdates(locationCallback)
            toggleTrackActivity(trackId, false)
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
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(channel)

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
        showProgress.putExtra(MainActivity.EXTRA_TRACK_ID, trackId)
        return PendingIntent.getActivity(this,
                                         0,
                                         showProgress,
                                         PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun toggleTrackActivity(trackId: Long, newValue: Boolean) {
        GlobalScope.launch {
            appDatabase.trackDao().updateActivity(TrackActivity(trackId, newValue))
        }
    }

    companion object {
        const val EXTRA_TRACK_ID = "com.example.trackme.TRACK_ID_EXTRA"
        const val CHANNEL_ID = "LocationTrackerServiceChannel"
        const val CHANNEL_NAME = "LocationTrackerServiceChannel"
    }
}