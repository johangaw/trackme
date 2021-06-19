package com.trackme.android.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.trackme.android.MainActivity
import com.trackme.android.R
import com.trackme.android.data.AppDatabase
import com.trackme.android.data.TrackActivity
import com.trackme.android.data.asTrackEntry
import com.google.android.gms.location.*
import com.trackme.android.data.Route
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
        return LocationRequest.create()
            ?.apply {
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
        val showTrackIntent = Intent(
            Intent.ACTION_VIEW,
            Route.TrackDetails.createDeepLink(trackId).toUri(),
            applicationContext,
            MainActivity::class.java
        )

        return TaskStackBuilder.create(applicationContext). run {
            addNextIntentWithParentStack(showTrackIntent)
            getPendingIntent(SHOW_TRACK_INTENT_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun toggleTrackActivity(trackId: Long, newValue: Boolean) {
        GlobalScope.launch {
            appDatabase.trackDao()
                .updateActivity(TrackActivity(trackId, newValue))
        }
    }

    companion object {
        const val SHOW_TRACK_INTENT_REQUEST_CODE = 0
        const val EXTRA_TRACK_ID = "com.example.trackme.TRACK_ID_EXTRA"
        const val CHANNEL_ID = "LocationTrackerServiceChannel"
        const val CHANNEL_NAME = "LocationTrackerServiceChannel"
    }
}