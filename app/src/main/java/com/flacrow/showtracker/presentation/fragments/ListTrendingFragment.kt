package com.flacrow.showtracker.presentation.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import com.flacrow.core.utils.ConstantValues.MOVIE_TYPE_STRING
import com.flacrow.core.utils.ConstantValues.TV_TYPE_STRING
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.presentation.viewModels.ListTrendingViewModel

class ListTrendingFragment : BaseListFragment<ListTrendingViewModel>() {

    override val playAnimations = true
    override val viewModel: ListTrendingViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onListElementClick(show: IShow, extras: FragmentNavigator.Extras) {
        when (show.mediaType) {
            TV_TYPE_STRING -> findNavController().navigate(
                ListTrendingFragmentDirections.actionShowListFragmentToSeriesDetailsFragment(
                    show.id
                ), extras
            )
            MOVIE_TYPE_STRING -> findNavController().navigate(
                ListTrendingFragmentDirections.actionShowListFragmentToMovieDetailsFragment(show.id),
                extras
            )

        }
    }
}