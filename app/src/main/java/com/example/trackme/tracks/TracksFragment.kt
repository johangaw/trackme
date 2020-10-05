package com.example.trackme.tracks

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trackme.R
import com.example.trackme.common.ui.SettingsAmbient
import com.example.trackme.common.ui.SpeedUnit
import com.example.trackme.tracking.LocationTrackerService
import com.example.trackme.tracks.ui.TracksScreen
import kotlinx.coroutines.launch

class TracksFragment : Fragment() {

    private val viewModel: TracksViewModel by viewModels { TracksViewModelFactory(requireActivity().application) }

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
        return ComposeView(requireContext()).apply {
            setContent {
                val tracks by viewModel.tracks.observeAsState()

                Providers(SettingsAmbient provides SettingsAmbient.current.copy(speedUnit = SpeedUnit.KILOMETERS_PER_HOUR)) {
                    TracksScreen(
                        tracks = tracks ?: emptyList(),
                        onTrackClick = { showTrack(it.id) },
                        onNewClick = {
                            lifecycleScope.launch {
                                val newTrack = viewModel.newTrack()
                                requestLocationTracking(newTrack.id)
                            }
                        }
                    )
                }

            }
        }
    }

    private fun showTrack(trackId: Long) {
        val paramName = resources.getString(R.string.tracking_route_track_id_param)
        val params = Bundle().apply {
            putLong(paramName, trackId)
        }
        findNavController().navigate(R.id.tracking_route, params)
    }

    private fun requestLocationTracking(trackId: Long) {
        if (!hasLocationPermission) {
            requestLocationPermission(trackId)
        } else {
            startLocationTracking(trackId)
        }
    }

    private fun startLocationTracking(trackId: Long) {
        lifecycleScope.launch {
            val serviceIntent = Intent(requireContext(), LocationTrackerService::class.java)
            serviceIntent.putExtra(LocationTrackerService.EXTRA_TRACK_ID, trackId)
            ContextCompat.startForegroundService(requireContext(), serviceIntent)
            showTrack(trackId)
        }
    }

    private fun requestLocationPermission(trackId: Long) {
        getPermissionRequest(trackId).launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun getPermissionRequest(trackId: Long): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                startLocationTracking(trackId)
            }
        }
    }

}