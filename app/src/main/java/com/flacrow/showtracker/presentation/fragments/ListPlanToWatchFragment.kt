package com.flacrow.showtracker.presentation.fragments

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.presentation.ViewModels.ListCachedShowsViewModel

class ListPlanToWatchFragment : BaseListFragment<ListCachedShowsViewModel>() {

    override val viewModel: ListCachedShowsViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onListElementClick(show: IShow) {
        Toast.makeText(requireContext(), show.title, Toast.LENGTH_SHORT).show()
    }
}