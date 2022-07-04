package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.R
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.databinding.FragmentMovieDetailsBinding
import com.flacrow.showtracker.databinding.FragmentSeriesDetailsBinding
import com.flacrow.showtracker.presentation.ViewModels.MovieDetailsViewModel
import com.flacrow.showtracker.presentation.ViewModels.ShowListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class MovieDetailsFragment :
    BaseFragment<FragmentMovieDetailsBinding, MovieDetailsViewModel>(FragmentMovieDetailsBinding::inflate) {

    override val viewModel: MovieDetailsViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val args: MovieDetailsFragmentArgs by navArgs()
        viewModel.getData(args.movieId)
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is MovieDetailsViewModel.MovieDetailsState.Loading -> showProgressBar()

                    is MovieDetailsViewModel.MovieDetailsState.Success -> updateUi(
                        uiState.movieDetailed
                    )
                    is MovieDetailsViewModel.MovieDetailsState.Error
                    -> throw uiState.exception
                    is MovieDetailsViewModel.MovieDetailsState.Empty -> {}
                }
            }

        }
    }

    private fun showProgressBar() {
        binding.mainDetailView.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun updateUi(movieDetailed: MovieDetailed) {
        with(binding) {
            mainDetailView.isVisible = true
            progressBar.isVisible = false
            var buffer = ""
            movieTitleTv.text = if (movieDetailed.firstAirDate.isEmpty()) movieDetailed.title
            else {
                requireContext().getString(
                    R.string.title_year_parenthesis,
                    movieDetailed.title,
                    movieDetailed.firstAirDate.dropLast(6)
                )
            }
            overviewTv.text = if (movieDetailed.overview.isEmpty()) requireContext().getText(R.string.no_overview)
                else movieDetailed.overview
            taglineTv.text = movieDetailed.tagline
            for (i in movieDetailed.genres.indices) {
                buffer += movieDetailed.genres.get(i).name + if (movieDetailed.genres.indices.last != i) {
                    ", "
                } else "."
            }
            genreTv.text = buffer
            userscoreTv.text = movieDetailed.rating.toString()
            movieDetailed.backdropUrl
            Glide
                .with(requireContext())
                .load("https://image.tmdb.org/t/p/w500/${movieDetailed.backdropUrl}")
                .error(R.drawable.ic_placeholder_image_50)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backdropIv)
        }
    }


}
