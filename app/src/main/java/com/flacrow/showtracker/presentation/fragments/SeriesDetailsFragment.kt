package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.R
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.data.models.TvDetailed
import com.flacrow.showtracker.databinding.FragmentSeriesDetailsBinding
import com.flacrow.showtracker.presentation.ViewModels.MovieDetailsViewModel
import com.flacrow.showtracker.presentation.ViewModels.SeriesDetailsViewModel
import com.flacrow.showtracker.presentation.adapters.SeasonsListAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class SeriesDetailsFragment :
    BaseFragment<FragmentSeriesDetailsBinding, SeriesDetailsViewModel>(FragmentSeriesDetailsBinding::inflate) {

    override val viewModel: SeriesDetailsViewModel by viewModels {
        viewModelFactory
    }

    private val adapter =
        SeasonsListAdapter(
            onAddEpCounter = { position -> onAddEpCounterClick(position) },
            onSubEpCounter = { position -> onSubEpCounterClick(position) })

    override fun setupDependencies() {
        requireContext().appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SeriesDetailsFragmentArgs by navArgs()
        viewModel.getData(args.seriesId)
        setupRadioButtonListeners()
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is SeriesDetailsViewModel.SeriesDetailsState.Loading -> showProgressBar()

                    is SeriesDetailsViewModel.SeriesDetailsState.Success -> updateUi(
                        uiState.tvDetailed
                    )
                    is SeriesDetailsViewModel.SeriesDetailsState.Error
                    -> showError(uiState.exception)
                    is SeriesDetailsViewModel.SeriesDetailsState.Empty -> {}
                }
            }

        }

    }

    private fun setupRadioButtonListeners() {
        binding.statusGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.ptw_button -> {Toast.makeText(requireContext(),"ptw",Toast.LENGTH_SHORT).show()}
                R.id.watching_button -> {Toast.makeText(requireContext(),"ptw2",Toast.LENGTH_SHORT).show()}
                R.id.cmpl_button -> {Toast.makeText(requireContext(),"ptw3",Toast.LENGTH_SHORT).show()}
                else -> {}
            }
        }
    }

    private fun showError(exception: Throwable) {
        with(binding) {
            progressBar.isVisible = false
            errorDetailedSeriesTv.isVisible = true
            errorDetailedSeriesTv.text = exception.localizedMessage
        }
    }

    private fun showProgressBar() {
        binding.mainDetailView.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun updateUi(tvDetailed: TvDetailed) {
        setAdapter()
        adapter.submitList(tvDetailed.seasons)
        with(binding) {
            binding.errorDetailedSeriesTv.isVisible = false
            mainDetailView.isVisible = true
            progressBar.isVisible = false
            var buffer = ""
            seriesTitleTv.text = if (tvDetailed.firstAirDate.isEmpty()) tvDetailed.title
            else {
                requireContext().getString(
                    R.string.title_year_parenthesis,
                    tvDetailed.title,
                    tvDetailed.firstAirDate.dropLast(6)
                )
            }
            overviewTv.text =
                if (tvDetailed.overview.isEmpty()) requireContext().getText(R.string.no_overview)
                else tvDetailed.overview
            taglineTv.text = tvDetailed.tagline
            for (i in tvDetailed.genres.indices) {
                buffer += tvDetailed.genres.get(i).name + if (tvDetailed.genres.indices.last != i) {
                    ", "
                } else "."
            }
            genreTv.text = buffer
            userscoreTv.text = tvDetailed.rating.toString()
            Glide
                .with(requireContext())
                .load("https://image.tmdb.org/t/p/w500/${tvDetailed.backdropUrl}")
                .error(R.drawable.ic_placeholder_image_50)
                .centerInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backdropIv)
        }
    }


    private fun setAdapter() {
        binding.seasonsRecycler.adapter = adapter
    }

    private fun onAddEpCounterClick(position: Int) {
        viewModel.addCounter(position)
    }

    private fun onSubEpCounterClick(position: Int) {
        viewModel.subCounter(position)
    }


}