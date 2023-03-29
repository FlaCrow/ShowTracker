package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import com.flacrow.core.utils.ConstantValues.MOVIE_TYPE_STRING
import com.flacrow.core.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.core.utils.ConstantValues.TV_TYPE_STRING
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.presentation.viewModels.ListCachedShowsViewModel
import kotlinx.coroutines.launch

class ListCompletedFragment : BaseListFragment<ListCachedShowsViewModel>() {

    override val playAnimations = false
    override val viewModel: ListCachedShowsViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchTabs.isVisible = true
    }

    override fun onListElementClick(show: IShow, extras: FragmentNavigator.Extras) {
        when (show.mediaType) {
            TV_TYPE_STRING -> findNavController().navigate(
                ListCompletedFragmentDirections.actionListCompletedFragmentToSeriesDetailsFragment(
                    show.id
                ), extras
            )
            MOVIE_TYPE_STRING -> findNavController().navigate(
                ListCompletedFragmentDirections.actionListCompletedFragmentToMovieDetailsFragment(
                    show.id
                ), extras
            )
        }
    }

    override fun getShowList(query: String) {
        lifecycleScope.launch {
            viewModel.getShowList(query).collect {
                adapter.submitData(it.filter { show -> show.watchStatus == STATUS_COMPLETED })
            }

        }
    }

}