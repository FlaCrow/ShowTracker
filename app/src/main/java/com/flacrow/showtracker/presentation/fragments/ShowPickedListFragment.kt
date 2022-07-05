package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.databinding.FragmentShowListBinding
import com.flacrow.showtracker.presentation.ViewModels.SeriesDetailsViewModel
import com.flacrow.showtracker.presentation.ViewModels.ShowPickedListViewModel

class ShowPickedListFragment :
    BaseFragment<FragmentShowListBinding, ShowPickedListViewModel>(FragmentShowListBinding::inflate) {

    override val viewModel: ShowPickedListViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

}