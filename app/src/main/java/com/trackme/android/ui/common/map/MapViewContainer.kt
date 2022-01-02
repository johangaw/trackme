package com.trackme.android.ui.common.map

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.model.*
import com.google.maps.android.ktx.awaitMap
import com.trackme.android.data.TrackEntry
import kotlinx.coroutines.launch

data class MapContent(var track: Polyline?, var current: Marker?)

@Composable
fun MapViewContainer(
    track: List<TrackEntry>,
    modifier: Modifier = Modifier,
    current: TrackEntry? = null,
    viewportTrack: List<TrackEntry> = track,
    onClick: (() -> Unit)? = null,
    interactive: Boolean = true,
) {
    val map = rememberMapViewWithLifecycle()

    val mapContent = remember(map) {
        MapContent(null, null)
    }

    val onClick by rememberUpdatedState(onClick)
    LaunchedEffect(map) {
        val googleMap = map.awaitMap()
        googleMap.setOnMapClickListener { onClick?.invoke() }
        googleMap.setOnMarkerClickListener {
            onClick?.invoke()
            true
        }
    }

    LaunchedEffect(map, current) {
        val googleMap = map.awaitMap()
        if (current == null) {
            mapContent.current?.remove()
        } else {
            mapContent.current?.remove()
            mapContent.current =
                googleMap.addMarker(MarkerOptions().position(LatLng(current.latitude,
                                                                    current.longitude)))
        }
    }

    val trackColor = MaterialTheme.colors.primary
    LaunchedEffect(map, track) {
        val googleMap = map.awaitMap()
        mapContent.track?.remove()
        if (track.isNotEmpty()) {
            val latLong = track.map { LatLng(it.latitude, it.longitude) }
            mapContent.track = googleMap.addPolyline(PolylineOptions().addAll(
                latLong
            )
                                                         .color(trackColor.toArgb()))
        }
    }

    LaunchedEffect(viewportTrack) {
        if (viewportTrack.isNotEmpty()) {
            val googleMap = map.awaitMap()
            val latLong = viewportTrack.map { LatLng(it.latitude, it.longitude) }
            val boundary = LatLngBounds.Builder()
                .apply {
                    latLong.forEach { include(it) }
                }
                .build()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundary, 32))
        }
    }


    val coroutineScope = rememberCoroutineScope()
    AndroidView({ map }, modifier = modifier) { mapView ->
        coroutineScope.launch {
            val googleMap = mapView.awaitMap()
            googleMap.uiSettings.setAllGesturesEnabled(interactive)
        }
    }
}