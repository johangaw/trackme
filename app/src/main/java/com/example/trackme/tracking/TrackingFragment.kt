package com.example.trackme.tracking

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.trackme.R
import com.example.trackme.tracking.ui.TrackingScreen
import kotlinx.coroutines.launch

class TrackingFragment : Fragment() {

    private val viewModel: TrackingViewModel by viewModels { TrackingViewModelFactory(requireActivity().application) }
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if(granted) {
            startLocationTracking()
        }
    }

    private val hasLocationPermission: Boolean
        get() {
            return ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val paramName = resources.getString(R.string.tracking_route_track_id_param)
        val trackingId = arguments?.getLong(paramName)
        require(trackingId != null) { "TrackId param is required in TrackingFragment"}
        viewModel.selectTrack(trackingId)

        return ComposeView(requireContext()).apply {
            setContent {
                val totalDistance by viewModel.totalDistance.observeAsState()
                val trackStartedAt by viewModel.trackStartedAt.observeAsState()

                TrackingScreen(
                    onStartClick = {
                        requestLocationTracking()
                    },
                    onStopClick = {
                        stopLocationTracking()
                        viewModel.stopTracking()
                    },
                    startedAt = trackStartedAt,
                    totalLength = totalDistance ?: 0F,
                    currentSpeed = 0.0
                )
            }
        }
    }

    private fun stopLocationTracking() {
        requireActivity().stopService(Intent(requireContext(), LocationTrackerService::class.java))
    }

    private fun requestLocationTracking() {
        if (!hasLocationPermission) {
            requestLocationPermission()
        } else {
            startLocationTracking()
        }
    }

    private fun startLocationTracking() {
        lifecycleScope.launch {
            val track = viewModel.newTrack()
            val serviceIntent = Intent(requireContext(), LocationTrackerService::class.java)
            serviceIntent.putExtra(LocationTrackerService.EXTRA_TRACK_ID, track.id)
            ContextCompat.startForegroundService(requireContext(), serviceIntent)
            viewModel.startTracking()
        }
    }

    private fun requestLocationPermission() {
        permissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
}