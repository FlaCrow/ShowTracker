package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.flacrow.core.utils.ConstantValues
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.presentation.viewModels.MovieDetailsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener

class MovieDetailsFragment :
    BaseDetailedFragment<MovieDetailsViewModel>() {


    override val viewModel: MovieDetailsViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: MovieDetailsFragmentArgs by navArgs()
        viewModel.getData(args.movieId)
        setAdapter(creditsListAdapter)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun configureTabLayout() {
        super.configureTabLayout()
        binding.recyclerTabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.text) {
                        ConstantValues.TabNames.DETAILED_CAST_TAB.tabName -> {
                            viewModel.getCastData()
                        }
                        ConstantValues.TabNames.DETAILED_CREW_TAB.tabName -> {
                            viewModel.getCrewData()
                        }
                    }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }


}
