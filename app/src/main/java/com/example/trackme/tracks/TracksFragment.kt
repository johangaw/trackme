package com.example.trackme.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.trackme.R
import com.example.trackme.data.Track
import com.example.trackme.tracks.ui.TracksScreen
import kotlinx.coroutines.launch

class TracksFragment : Fragment() {

    private val viewModel: TracksViewModel by viewModels { TracksViewModelFactory(requireActivity().application) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                val tracks by viewModel.tracks.observeAsState()

                TracksScreen(
                    tracks = tracks ?: emptyList(),
                    onTrackClick = this@TracksFragment::showTrack,
                    onNewClick = {
                        lifecycleScope.launch {
                            viewModel.newTrack()
                        }
                    }
                )
            }
        }
    }

    private fun showTrack(track: Track) {
        val paramName = resources.getString(R.string.tracking_route_track_id_param)
        val params = Bundle().apply {
            putLong(paramName, track.id)
        }
        findNavController().navigate(R.id.tracking_route, params)
    }
}