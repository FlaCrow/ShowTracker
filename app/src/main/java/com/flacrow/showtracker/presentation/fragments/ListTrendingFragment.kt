package com.flacrow.showtracker.presentation.fragments

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.presentation.viewModels.ListTrendingViewModel
import com.flacrow.showtracker.utils.ConstantValues.MOVIE_TYPE_STRING
import com.flacrow.showtracker.utils.ConstantValues.TV_TYPE_STRING

class ListTrendingFragment : BaseListFragment<ListTrendingViewModel>() {

    override val playAnimations = true
    override val viewModel: ListTrendingViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onListElementClick(show: IShow) {
        when (show.mediaType) {
            TV_TYPE_STRING ->
                findNavController().navigate(
                    ListTrendingFragmentDirections.actionShowListFragmentToSeriesDetailsFragment(
                        show.id
                    )
                )
            MOVIE_TYPE_STRING ->
                findNavController().navigate(
                    ListTrendingFragmentDirections.actionShowListFragmentToMovieDetailsFragment(show.id)
                )

        }
    }
}