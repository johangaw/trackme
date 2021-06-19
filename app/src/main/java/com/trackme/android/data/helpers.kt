package com.trackme.android.data

fun totalDistance(entries: List<TrackEntry>): Float {
    return entries
        .sortedBy { it.time }
        .map { entry -> entry.asLocation() }
        .zipWithNext { l1, l2 -> l1.distanceTo(l2) }
        .sum()
}

fun averageSpeed(entries: List<TrackEntry>, totalDistance: Float): Float {
    return if (entries.size > 1) totalDistance / (entries.last().time - entries.first().time) * 1000f else 0f

}