package com.example.trackme.data

import android.location.Location

fun Location.asTrackEntry(): TrackEntry {
    return TrackEntry(
        longitude = longitude,
        latitude = latitude,
        altitude = altitude,
        speed = speed,
        bearing = bearing,
        time = time
    )
}