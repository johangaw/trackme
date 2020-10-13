package com.trackme.android.data

import android.location.Location
import android.location.LocationManager

fun Location.asTrackEntry(trackId: Long): TrackEntry {
    return TrackEntry(
        longitude = longitude,
        latitude = latitude,
        altitude = altitude,
        speed = speed,
        bearing = bearing,
        time = time,
        trackId = trackId
    )
}

fun TrackEntry.asLocation(): Location {
    val location = Location(LocationManager.GPS_PROVIDER)
    location.longitude = longitude
    location.longitude = longitude
    location.latitude = latitude
    location.altitude = altitude
    location.speed = speed
    location.bearing = bearing
    location.time = time
    return location
}