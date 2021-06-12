package com.trackme.android

import android.os.Parcelable
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.Crossfade
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trackme.android.ui.common.Navigation
import com.trackme.android.ui.tracking.TrackingScreen
import com.trackme.android.ui.tracking.TrackingViewModel
import com.trackme.android.ui.tracking.TrackingViewModelFactory
import com.trackme.android.ui.tracks.TracksScreen
import com.trackme.android.ui.tracks.TracksViewModel
import com.trackme.android.ui.tracks.TracksViewModelFactory
import kotlinx.android.parcel.Parcelize
import java.time.LocalDateTime

@ExperimentalMaterialApi
@Composable
fun App(
    onBackPressedDispatcher: OnBackPressedDispatcher,
    requestLocationTracking: () -> Unit,
    stopLocationTracking: () -> Unit,
    startOnTrack: FocusTrackRequest,
) {
    // TODO use navigation lib instead...
    val navigator: Navigation<Destination> by
        rememberSaveable(stateSaver = Navigation.saver(onBackPressedDispatcher)) {
            mutableStateOf(
                Navigation(onBackPressedDispatcher, Destination.Tracks)
            )
        }


    // Fixme navigate to track when started from activity
//    onCommit(startOnTrack) {
//        if (startOnTrack.trackId >= 0) {
//            navigator.popToTop()
//            navigator.push(Destination.Tracking(startOnTrack.trackId))
//        }
//    }

    CompositionLocalProvider(NavigationAmbient provides navigator) {
        Crossfade(targetState = navigator.current) {
            when (it) {
                Destination.Tracks -> TracksScreenWrapper(requestLocationTracking)
                is Destination.Tracking -> TrackingScreenWrapper(it.trackId, stopLocationTracking)
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
@Preview(device = Devices.PIXEL_3, showSystemUi = true, showBackground = true)
fun AppPreview() {
    App(OnBackPressedDispatcher(), {}, {}, FocusTrackRequest(-1))
}

@Composable
fun TrackingScreenWrapper(trackId: Long, stopLocationTracking: () -> Unit) {
    val viewModel = viewModel(
        modelClass = TrackingViewModel::class.java,
        factory = TrackingViewModelFactory(LocalContext.current.applicationContext)
    )

    // FixMe not sure what needs to be fixed...
//    onCommit(trackId) {
//        viewModel.setTrackId(trackId)
//    }

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
fun TracksScreenWrapper(requestLocationTracking: () -> Unit) {
    val viewModel =
        viewModel(
            modelClass = TracksViewModel::class.java,
            factory = TracksViewModelFactory(LocalContext.current.applicationContext)
        )
    val tracks by viewModel.tracks.observeAsState()
    val navigator = NavigationAmbient.current
    TracksScreen(
        tracks = tracks ?: emptyList(),
        onTrackClick = { track -> navigator.push(Destination.Tracking(track.id)) },
        onTrackDelete = { track ->
            viewModel.removeTrack(track.id)
        },
        onNewClick = {
            requestLocationTracking()
        }
    )
}

internal val NavigationAmbient = staticCompositionLocalOf<Navigation<Destination>> {
    error("No navigation created")
}

sealed class Destination : Parcelable {

    @Parcelize
    object Tracks : Destination()

    @Parcelize
    class Tracking(val trackId: Long) : Destination()
}


