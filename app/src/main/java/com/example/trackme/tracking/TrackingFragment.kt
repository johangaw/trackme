package com.example.trackme.tracking

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.trackme.R
import com.example.trackme.tracking.ui.TrackingScreen
import java.time.LocalDateTime

class TrackingFragment : Fragment() {

    private var trackingId: Long? = null
    private val viewModel: TrackingViewModel by viewModels {
        val paramName = resources.getString(R.string.tracking_route_track_id_param)
        trackingId = arguments?.getLong(paramName)
        require(trackingId != null) { "TrackId param is required in TrackingFragment" }
        TrackingViewModelFactory(requireActivity().application, trackingId!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val totalDistance by viewModel.totalDistance.observeAsState()
                val trackStartedAt by viewModel.trackStartedAt.observeAsState()
                val activeTrack by viewModel.activeTrack.observeAsState()
                val trackEntries by viewModel.activeTrackEntries.observeAsState()

                val startedAt = activeTrack?.let { track ->
                    if (track.active) trackStartedAt ?: LocalDateTime.now()
                    else null
                }

                TrackingScreen(
                    onStopClick = this@TrackingFragment::stopLocationTracking,
                    startedAt = startedAt,
                    totalLength = totalDistance ?: 0f,
                    currentSpeed = 0f,
                    trackEntries = trackEntries ?: emptyList(),
                )
            }
        }
    }

    private fun stopLocationTracking() {
        requireActivity().stopService(Intent(requireContext(), LocationTrackerService::class.java))
    }

}