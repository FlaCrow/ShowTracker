package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.databinding.FragmentDetailsBinding
import com.flacrow.showtracker.presentation.viewModels.BaseDetailedViewModel
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import kotlinx.coroutines.launch

abstract class BaseDetailedFragment<VModel : BaseDetailedViewModel> :
    BaseFragment<FragmentDetailsBinding, VModel>(FragmentDetailsBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is BaseDetailedViewModel.ShowsDetailsState.Loading -> showProgressBar()

                    is BaseDetailedViewModel.ShowsDetailsState.Success -> updateUi(
                        uiState.showDetailed
                    )
                    is BaseDetailedViewModel.ShowsDetailsState.Error,
                    -> showError(uiState.exception)
                    is BaseDetailedViewModel.ShowsDetailsState.Empty -> {}
                }
            }
        }

    }

    private fun setupRadioButtonListeners() {
        binding.statusGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                R.id.ptw_button -> {
                    viewModel.saveWatchStatus(STATUS_PLAN_TO_WATCH)
                }
                R.id.watching_button -> {
                    viewModel.saveWatchStatus(STATUS_WATCHING)
                }
                R.id.cmpl_button -> {
                    viewModel.saveWatchStatus(STATUS_COMPLETED)
                }
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

    protected open fun updateUi(tvDetailed: IShowDetailed) {
//        setAdapter()
//        adapter.submitList(tvDetailed.seasons)
        with(binding) {
            binding.errorDetailedSeriesTv.isVisible = false
            mainDetailView.isVisible = true
            progressBar.isVisible = false
            binding.statusGroup.check(tvDetailed.watchStatus)
            setupRadioButtonListeners()

            var buffer = ""
            seriesTitleTv.text = if (tvDetailed.firstAirDate.isNullOrEmpty()) tvDetailed.title
            else {
                requireContext().getString(
                    R.string.title_year_parenthesis,
                    tvDetailed.title,
                    tvDetailed.firstAirDate?.dropLast(6)
                )
            }
            overviewTv.text =
                tvDetailed.overview.ifEmpty { requireContext().getText(R.string.no_overview) }
            taglineTv.text = tvDetailed.tagline
            for (i in tvDetailed.genres.indices) {
                buffer += tvDetailed.genres.get(i).name + if (tvDetailed.genres.indices.last != i) {
                    ", "
                } else "."
            }
            genreTv.text = buffer
            userscore.percentage = tvDetailed.rating * 10f
            Glide
                .with(requireContext())
                .load("https://image.tmdb.org/t/p/w500/${tvDetailed.backdropUrl}")
                .error(R.drawable.ic_placeholder_image_50)
                .centerInside()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(backdropIv)
        }
    }


}