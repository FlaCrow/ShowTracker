package com.flacrow.showtracker.presentation.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.flacrow.core.utils.Config.IMAGE_BASE_URL
import com.flacrow.core.utils.ConstantValues
import com.flacrow.core.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.core.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.core.utils.ConstantValues.STATUS_WATCHING
import com.flacrow.core.utils.Extensions.setImageWithGlide
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.IShowDetailed
import com.flacrow.showtracker.databinding.FragmentDetailsBinding
import com.flacrow.showtracker.presentation.adapters.CreditsListAdapter
import com.flacrow.showtracker.presentation.viewModels.BaseDetailedViewModel
import kotlinx.coroutines.launch

abstract class BaseDetailedFragment<VModel : BaseDetailedViewModel> :
    BaseFragment<FragmentDetailsBinding, VModel>(FragmentDetailsBinding::inflate) {

    val creditsListAdapter = CreditsListAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is BaseDetailedViewModel.ShowsDetailsState.Loading -> showProgressBar()

                    is BaseDetailedViewModel.ShowsDetailsState.Success -> {
                        updateShowUi(
                            uiState.showDetailed
                        )
                        if (uiState.exception != null) Toast.makeText(
                            context,
                            uiState.exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is BaseDetailedViewModel.ShowsDetailsState.Error,
                    -> showError(uiState.exception)
                    is BaseDetailedViewModel.ShowsDetailsState.Empty -> {}

                }
            }
        }
        configureTabLayout()
    }

    protected open fun configureTabLayout() {
        binding.recyclerTabLayout.addTab(
            binding.recyclerTabLayout.newTab()
                .setText(ConstantValues.TabNames.DETAILED_CAST_TAB.tabName)
        )
        binding.recyclerTabLayout.addTab(
            binding.recyclerTabLayout.newTab()
                .setText(ConstantValues.TabNames.DETAILED_CREW_TAB.tabName)
        )
    }

    private fun setupRadioButtonListeners() {
        binding.statusGroup.setOnCheckedChangeListener { _, id ->
            when (id) {
                STATUS_PLAN_TO_WATCH -> {
                    viewModel.saveWatchStatus(STATUS_PLAN_TO_WATCH)
                }
                STATUS_WATCHING -> {
                    viewModel.saveWatchStatus(STATUS_WATCHING)
                }
                STATUS_COMPLETED -> {
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
        binding.itemsRecycler.isVisible = false
        binding.progressBar.isVisible = true
    }

    protected open fun updateShowUi(tvDetailed: IShowDetailed) {
//        setAdapter()
//        adapter.submitList(tvDetailed.seasons)
        with(binding) {
            binding.errorDetailedSeriesTv.isVisible = false
            mainDetailView.isVisible = true
            binding.itemsRecycler.isVisible = true
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
            taglineTv.text = tvDetailed.tagline.ifEmpty { getString(R.string.no_tagline_string) }
            for (i in tvDetailed.genres.indices) {
                buffer += tvDetailed.genres[i].name + if (tvDetailed.genres.indices.last != i) {
                    ", "
                } else "."
            }
            genreTv.text = buffer
            userscore.percentage = tvDetailed.rating * 10f
            backdropIv.setImageWithGlide(
                "${IMAGE_BASE_URL}/t/p/w500/${tvDetailed.backdropUrl}",
                com.flacrow.core.R.drawable.ic_placeholder_image_24
            )
            lifecycleScope.launch {
                viewModel.creditsPagingDataState.collect { creditsState ->
                    when (creditsState) {
                        is BaseDetailedViewModel.CreditsState.Success ->
                            creditsListAdapter.submitList(creditsState.creditsRecyclerItem)
                        is BaseDetailedViewModel.CreditsState.Error -> Toast.makeText(
                            context,
                            creditsState.exception.localizedMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                        BaseDetailedViewModel.CreditsState.Empty -> {}
                    }
                }
            }
        }
    }

    protected fun setAdapter(adapter: RecyclerView.Adapter<ViewHolder>?) {
        binding.itemsRecycler.adapter = adapter
    }

}