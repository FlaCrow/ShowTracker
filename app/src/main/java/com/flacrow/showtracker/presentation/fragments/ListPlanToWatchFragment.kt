package com.flacrow.showtracker.presentation.fragments

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.presentation.ViewModels.ListCachedShowsViewModel
import com.flacrow.showtracker.utils.ConstantValues
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ListPlanToWatchFragment : BaseListFragment<ListCachedShowsViewModel>() {

    override val viewModel: ListCachedShowsViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onListElementClick(show: IShow) {
        when (show.mediaType) {
            ConstantValues.TV_TYPE_STRING ->
                findNavController().navigate(
                    ListPlanToWatchFragmentDirections.actionListPlanToWatchFragmentToSeriesDetailsFragment(
                        show.id
                    )
                )
            ConstantValues.MOVIE_TYPE_STRING ->
                findNavController().navigate(
                    ListPlanToWatchFragmentDirections.actionListPlanToWatchFragmentToMovieDetailsFragment(
                        show.id
                    )
                )
        }
    }

    override fun getShowList(query: String) {
        lifecycleScope.launch {
            viewModel.getShowList(query).collect {
                adapter.submitData(it.filter { show -> show.watchStatus == STATUS_PLAN_TO_WATCH })
            }

        }
    }

}