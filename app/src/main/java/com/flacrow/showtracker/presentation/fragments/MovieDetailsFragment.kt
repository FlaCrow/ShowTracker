package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.presentation.viewModels.MovieDetailsViewModel

class MovieDetailsFragment :
    BaseDetailedFragment<MovieDetailsViewModel>() {

    override val viewModel: MovieDetailsViewModel by viewModels {
        viewModelFactory
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: MovieDetailsFragmentArgs by navArgs()
        viewModel.getData(args.movieId)
        super.onViewCreated(view, savedInstanceState)
    }
    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }
}
