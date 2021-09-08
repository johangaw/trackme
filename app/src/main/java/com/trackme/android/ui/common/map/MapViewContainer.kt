package com.trackme.android.ui.common.map

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.*
import com.google.maps.android.ktx.awaitMap
import com.trackme.android.data.TrackEntry
import kotlinx.coroutines.launch

data class MapContent(var track: Polyline?, var current: Marker?)

@Composable
fun MapViewContainer(
    map: MapView,
    track: List<TrackEntry>,
    current: TrackEntry?,
    modifier: Modifier = Modifier,
    viewportTrack: List<TrackEntry> = track,
) {

    val mapContent = remember(map) {
        MapContent(null, null)
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
        if(viewportTrack.isNotEmpty()) {
            val googleMap = map.awaitMap()
            val latLong = viewportTrack.map { LatLng(it.latitude, it.longitude) }
            val bundery = LatLngBounds.Builder()
                .apply {
                    latLong.forEach { include(it) }
                }
                .build()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bundery, 32))
        }
    }


    val coroutineScope = rememberCoroutineScope()
    AndroidView({ map }, modifier = modifier) { mapView ->
        coroutineScope.launch {
            val googleMap = mapView.awaitMap()
            googleMap.uiSettings.setAllGesturesEnabled(false)
        }
    }
}