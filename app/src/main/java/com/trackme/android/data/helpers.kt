package com.trackme.android.data

fun totalDistance(entries: List<TrackEntry>): Float {
    return entries.map { entry -> entry.asLocation() }
        .zipWithNext { l1, l2 -> l1.distanceTo(l2) }
        .sum()
}