package com.trackme.android.ui.details.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trackme.android.data.TrackEntry
import com.trackme.android.ui.common.map.MapViewContainer

@Composable
fun TrackMapScreen(track: List<TrackEntry>) {
    MapViewContainer(track = track,
                     modifier = Modifier.fillMaxSize(),

    )
}