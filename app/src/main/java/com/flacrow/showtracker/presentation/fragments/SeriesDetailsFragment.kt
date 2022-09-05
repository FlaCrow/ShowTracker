package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.presentation.adapters.SeasonsListAdapter
import com.flacrow.showtracker.presentation.viewModels.SeriesDetailsViewModel
import com.flacrow.showtracker.utils.Extensions.setChildrenEnabled
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class SeriesDetailsFragment :
    BaseDetailedFragment<SeriesDetailsViewModel>() {

    override val viewModel: SeriesDetailsViewModel by viewModels {
        viewModelFactory
    }

    private val adapter =
        SeasonsListAdapter(
            onEpisodePickerValueChanged = { position, newValueFlow ->
                onEpisodePickerValueChanged(
                    position, newValueFlow
                )
            }, onExpandButtonClicked = { position ->
                onExpandButtonClicked(position)
            })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: SeriesDetailsFragmentArgs by navArgs()
        if (isOnline(requireContext())) {
            viewModel.updateData(args.seriesId)
        } else viewModel.getData(args.seriesId)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun updateUi(tvDetailed: IShowDetailed) {
        setAdapter()
        lifecycleScope.launch {
            viewModel.seasonListStateFlow.collect {
                adapter.submitList(it)
            }
        }
        super.updateUi(tvDetailed)
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    private fun setAdapter() {
        binding.seasonsRecycler.adapter = adapter
    }

    @OptIn(FlowPreview::class)
    private fun onEpisodePickerValueChanged(position: Int, newValueFlow: Flow<Int>) {
        lifecycleScope.launch {
            newValueFlow.onEach {
                binding.statusGroup.setChildrenEnabled(false)
            }.debounce(500)
                .collect { newValue ->
                    viewModel.changeEpisodeWatchedValue(position, newValue)
                    binding.statusGroup.setChildrenEnabled(true)
                }
        }
    }

    private fun onExpandButtonClicked(position: Int) {
        viewModel.expandRecycler(position)
    }

    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }
}