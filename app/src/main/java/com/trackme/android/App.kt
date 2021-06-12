package com.trackme.android

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.trackme.android.ui.tracking.TrackingScreen
import com.trackme.android.ui.tracking.TrackingViewModel
import com.trackme.android.ui.tracking.TrackingViewModelFactory
import com.trackme.android.ui.tracks.TrackData
import com.trackme.android.ui.tracks.TracksScreen
import com.trackme.android.ui.tracks.TracksViewModel
import com.trackme.android.ui.tracks.TracksViewModelFactory
import java.time.LocalDateTime


@ExperimentalMaterialApi
@Composable
fun App(
    requestLocationTracking: () -> Unit,
    stopLocationTracking: () -> Unit,
    startOnTrack: FocusTrackRequest,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "tracks") {
        composable("tracks") {
            TracksScreenWrapper(requestLocationTracking, { track -> navController.navigate("tracking/${track.id}")})
        }

        composable("tracking/{trackId}",
                   arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) {
            TrackingScreenWrapper(trackId = it.arguments?.getLong("trackId")!!,
                                  stopLocationTracking)
        }
    }



    // Fixme navigate to track when started from activity
//    onCommit(startOnTrack) {
//        if (startOnTrack.trackId >= 0) {
//            navigator.popToTop()
//            navigator.push(Destination.Tracking(startOnTrack.trackId))
//        }
//    }

}

@ExperimentalMaterialApi
@Composable
@Preview(device = Devices.PIXEL_3, showSystemUi = true, showBackground = true)
fun AppPreview() {
    App({}, {}, FocusTrackRequest(-1))
}

@Composable
fun TrackingScreenWrapper(trackId: Long, stopLocationTracking: () -> Unit) {
    val viewModel = viewModel(
        modelClass = TrackingViewModel::class.java,
        factory = TrackingViewModelFactory(LocalContext.current.applicationContext)
    )

    LaunchedEffect(trackId) {
        viewModel.setTrackId(trackId)
    }

    val totalDistance by viewModel.totalDistance.observeAsState(0f)
    val trackStartedAt by viewModel.trackStartedAt.observeAsState()
    val activeTrack by viewModel.activeTrack.observeAsState()
    val trackEntries by viewModel.activeTrackEntries.observeAsState(emptyList())

    val startedAt = activeTrack?.let { track ->
        if (track.active) trackStartedAt ?: LocalDateTime.now()
        else null
    }

    TrackingScreen(
        onStopClick = stopLocationTracking,
        startedAt = startedAt,
        totalLength = totalDistance,
        currentSpeed = 0f,
        trackEntries = trackEntries,
    )
}

@ExperimentalMaterialApi
@Composable
fun TracksScreenWrapper(requestLocationTracking: () -> Unit, navigateToTrack: (track: TrackData) -> Unit) {
    val viewModel =
        viewModel(
            modelClass = TracksViewModel::class.java,
            factory = TracksViewModelFactory(LocalContext.current.applicationContext)
        )
    val tracks by viewModel.tracks.observeAsState()
    TracksScreen(
        tracks = tracks ?: emptyList(),
        onTrackClick = navigateToTrack,
        onTrackDelete = { track ->
            viewModel.removeTrack(track.id)
        },
        onNewClick = {
            requestLocationTracking()
        }
    )
}


