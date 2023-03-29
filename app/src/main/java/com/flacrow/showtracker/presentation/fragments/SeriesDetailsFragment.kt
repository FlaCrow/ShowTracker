package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.flacrow.core.utils.ConstantValues
import com.flacrow.core.utils.Extensions.setChildrenEnabled
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.presentation.adapters.SeasonsListAdapter
import com.flacrow.showtracker.presentation.adapters.SwitchableTypes
import com.flacrow.showtracker.presentation.viewModels.SeriesDetailsViewModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class SeriesDetailsFragment : BaseDetailedFragment<SeriesDetailsViewModel>() {

    override val viewModel: SeriesDetailsViewModel by viewModels {
        viewModelFactory
    }

    private val seasonsListAdapter =
        SeasonsListAdapter(onEpisodePickerValueChanged = { newValueFlow ->
            onEpisodePickerValueChanged(
                newValueFlow
            )
        }, onExpandButtonClicked = { seasonNumber ->
            onExpandButtonClicked(seasonNumber)
        })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: SeriesDetailsFragmentArgs by navArgs()
        seasonsListAdapter.setHasStableIds(true)
        if (requireActivity().getSharedPreferences(
                requireContext().packageName,
                Context.MODE_PRIVATE
            ).getBoolean(SwitchableTypes.UPDATE_ON_INTERACTION.name, true)
        ) {
            viewModel.updateData(args.seriesId)
        } else viewModel.getData(args.seriesId)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun updateShowUi(showDetailed: IShowDetailed) {
        if (binding.recyclerTabLayout.selectedTabPosition == 0) setAdapter(seasonsListAdapter)
        lifecycleScope.launch {
            viewModel.recyclerListStateFlow.collect {
                seasonsListAdapter.submitList(it)
            }
        }
        super.updateShowUi(showDetailed)
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }


    @OptIn(FlowPreview::class)
    private fun onEpisodePickerValueChanged(newValueFlow: Flow<Pair<Int, Int>>) {
        lifecycleScope.launch {
            newValueFlow.onEach {
                binding.statusGroup.setChildrenEnabled(false)
            }.debounce(500).collect { newValue ->
                viewModel.changeEpisodeWatchedValue(newValue.first, newValue.second)
                binding.statusGroup.setChildrenEnabled(true)
            }
        }
    }

    override fun configureTabLayout() {
        binding.recyclerTabLayout.addTab(
            binding.recyclerTabLayout.newTab()
                .setText(ConstantValues.TabNames.DETAILED_SEASON_TAB.tabName)
        )
        super.configureTabLayout()
        val args: SeriesDetailsFragmentArgs by navArgs()
        binding.recyclerTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.text) {
                    ConstantValues.TabNames.DETAILED_CAST_TAB.tabName -> {
                        setAdapter(creditsListAdapter)
                        viewModel.getCastData(args.seriesId, ConstantValues.TV_TYPE_STRING)
                    }
                    ConstantValues.TabNames.DETAILED_CREW_TAB.tabName -> {
                        setAdapter(creditsListAdapter)
                        viewModel.getCrewData(args.seriesId, ConstantValues.TV_TYPE_STRING)
                    }
                    ConstantValues.TabNames.DETAILED_SEASON_TAB.tabName -> {
                        setAdapter(seasonsListAdapter)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }


    private fun onExpandButtonClicked(seasonNumber: Int) {
        viewModel.expandRecycler(seasonNumber)
    }
}