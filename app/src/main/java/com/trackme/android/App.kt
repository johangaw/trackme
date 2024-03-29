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
import androidx.navigation.navDeepLink
import com.trackme.android.data.Route
import com.trackme.android.ui.details.TrackDetailsScreen
import com.trackme.android.ui.details.TrackingViewModel
import com.trackme.android.ui.details.TrackingViewModelFactory
import com.trackme.android.ui.details.map.TrackMapScreen
import com.trackme.android.ui.tracks.TracksScreen
import com.trackme.android.ui.tracks.TracksViewModel
import com.trackme.android.ui.tracks.TracksViewModelFactory
import java.time.LocalDateTime

@ExperimentalMaterialApi
@Composable
fun App(
    requestLocationTracking: (onTrackingStarted: (newTrackId: Long) -> Unit) -> Unit,
    stopLocationTracking: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Route.TrackList.route) {
        composable(Route.TrackList.route) {
            TracksScreenWrapper(requestLocationTracking,
                                { trackId ->
                                    navController.navigate(Route.TrackDetails.createLink(trackId))
                                })
        }

        composable(Route.TrackDetails.route,
                   arguments = listOf(navArgument(Route.TrackDetails.trackIdParam) {
                       type = NavType.LongType
                   }),
                   deepLinks = listOf(navDeepLink { uriPattern = Route.TrackDetails.deepLinkRoute })
        ) {
            TrackDetailsScreenWrapper(trackId = it.arguments?.getLong(Route.TrackDetails.trackIdParam)!!,
                                      stopLocationTracking = stopLocationTracking,
                                      navigateToTrackMap = { trackId ->
                                          navController.navigate(Route.TrackMap.createLink(trackId))
                                      }
            )
        }

        composable(Route.TrackMap.route,
                   arguments = listOf(navArgument(Route.TrackMap.trackIdParam) {
                       type = NavType.LongType
                   }),
                   deepLinks = listOf(navDeepLink { uriPattern = Route.TrackMap.deepLinkRoute })
        ) {
            TrackMapWrapper(trackId = it.arguments?.getLong(Route.TrackMap.trackIdParam)!!)
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview(device = Devices.PIXEL_3, showSystemUi = true, showBackground = true)
fun AppPreview() {
    App({}, {})
}

@Composable
fun TrackMapWrapper(trackId: Long) {
    val viewModel = viewModel(
        modelClass = TrackingViewModel::class.java,
        factory = TrackingViewModelFactory(LocalContext.current.applicationContext)
    )

    LaunchedEffect(trackId) {
        viewModel.setTrackId(trackId)
    }

    val trackEntries by viewModel.activeTrackEntries.observeAsState(emptyList())
    TrackMapScreen(
        track = trackEntries
    )
}

@Composable
fun TrackDetailsScreenWrapper(
    trackId: Long,
    stopLocationTracking: () -> Unit,
    navigateToTrackMap: (trackId: Long) -> Unit,
) {
    val viewModel = viewModel(
        modelClass = TrackingViewModel::class.java,
        factory = TrackingViewModelFactory(LocalContext.current.applicationContext)
    )

    LaunchedEffect(trackId) {
        viewModel.setTrackId(trackId)
    }

    val totalDistance by viewModel.selectedRangeTotalDistance.observeAsState(0f)
    val averageSpeed by viewModel.selectedRangeAverageSpeed.observeAsState(0f)
    val trackStartedAt by viewModel.selectedRangeStartedAt.observeAsState()
    val activeTrack by viewModel.activeTrack.observeAsState()
    val trackEntries by viewModel.activeTrackEntries.observeAsState(emptyList())
    val selectedTrackEntries by viewModel.selectedTrackEntries.observeAsState(emptyList())

    val startedAt = activeTrack?.let { track ->
        if (track.active) trackStartedAt ?: LocalDateTime.now()
        else null
    }

    TrackDetailsScreen(
        onStopClick = stopLocationTracking,
        startedAt = startedAt,
        totalDistance = totalDistance,
        averageSpeed = averageSpeed,
        trackEntries = trackEntries,
        selectedTrackEntries = selectedTrackEntries,
        onSelectTrackRange = viewModel::setSelectedRange,
        onMapClick = { navigateToTrackMap(trackId) }
    )
}

@ExperimentalMaterialApi
@Composable
fun TracksScreenWrapper(
    requestLocationTracking: (onTrackingStarted: (newTrackId: Long) -> Unit) -> Unit,
    navigateToTrack: (trackId: Long) -> Unit,
) {
    val viewModel =
        viewModel(
            modelClass = TracksViewModel::class.java,
            factory = TracksViewModelFactory(LocalContext.current.applicationContext)
        )
    val tracks by viewModel.tracks.observeAsState()
    TracksScreen(
        tracks = tracks ?: emptyList(),
        onTrackClick = { trackData -> navigateToTrack(trackData.id) },
        onTrackDelete = { track ->
            viewModel.removeTrack(track.id)
        },
        onNewClick = {
            requestLocationTracking { navigateToTrack(it) }
        }
    )
}


