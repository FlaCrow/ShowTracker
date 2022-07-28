package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.presentation.adapters.SeasonAdapterItem
import com.flacrow.showtracker.presentation.adapters.SeasonsListAdapter
import com.flacrow.showtracker.presentation.viewModels.SeriesDetailsViewModel
import kotlinx.coroutines.flow.Flow


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
        viewModel.getData(args.seriesId)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun updateUi(tvDetailed: IShowDetailed) {
        setAdapter()
        if (tvDetailed is TvDetailed)
            viewModel.saveSeasonAdapterList(tvDetailed.seasons.toMutableList())
            adapter.submitList(viewModel.list)
        super.updateUi(tvDetailed)
    }

    private fun setAdapter() {
        binding.seasonsRecycler.adapter = adapter
    }

    private fun onEpisodePickerValueChanged(position: Int, newValueFlow: Flow<Int>) {
        viewModel.changeEpisodeWatchedValue(position, newValueFlow)
    }

    private fun onExpandButtonClicked(position: Int) {
        viewModel.expandRecycler(position)
        adapter.notifyDataSetChanged()
    }


}