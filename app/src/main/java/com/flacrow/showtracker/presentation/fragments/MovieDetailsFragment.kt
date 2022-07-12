package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.R
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.MovieDetailed
import com.flacrow.showtracker.databinding.FragmentMovieDetailsBinding
import com.flacrow.showtracker.presentation.ViewModels.MovieDetailsViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MovieDetailsFragment :
    BaseFragment<FragmentMovieDetailsBinding, MovieDetailsViewModel>(FragmentMovieDetailsBinding::inflate) {

    override val viewModel: MovieDetailsViewModel by viewModels {
        viewModelFactory
    }

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                    -> showError(uiState.exception)
                    is MovieDetailsViewModel.MovieDetailsState.Empty -> {}
                }
            }
        }
    }


    private fun setupRadioButtonListeners() {
        binding.statusGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.ptw_button -> {
                    viewModel.addToPTW()
                    Toast.makeText(requireContext(), "ptw", Toast.LENGTH_SHORT).show()
                }
                R.id.watching_button -> {
                    viewModel.addToWatching()
                    Toast.makeText(requireContext(), "ptw2", Toast.LENGTH_SHORT).show()
                }
                R.id.cmpl_button -> {
                    viewModel.addToCMPL()
                    Toast.makeText(requireContext(), "ptw3", Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun showError(exception: Throwable) {
        with(binding) {
            progressBar.isVisible = false
            errorDetailedMovieTv.isVisible = true
            errorDetailedMovieTv.text = exception.localizedMessage
        }
    }

    private fun showProgressBar() {
        binding.mainDetailView.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun updateUi(movieDetailed: MovieDetailed) {
        with(binding) {
            binding.errorDetailedMovieTv.isVisible = true
            mainDetailView.isVisible = true
            progressBar.isVisible = false
            binding.statusGroup.check(movieDetailed.watchStatus)
            setupRadioButtonListeners()

            var buffer = ""
            movieTitleTv.text = if (movieDetailed.firstAirDate.isEmpty()) movieDetailed.title
            else {
                requireContext().getString(
                    R.string.title_year_parenthesis,
                    movieDetailed.title,
                    movieDetailed.firstAirDate.dropLast(6)
                )
            }
            overviewTv.text =
                if (movieDetailed.overview.isEmpty()) requireContext().getText(R.string.no_overview)
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
