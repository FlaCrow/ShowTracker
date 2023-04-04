package com.flacrow.showtracker.presentation.fragments

import android.graphics.Color
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
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.coroutines.launch

abstract class BaseDetailedFragment<VModel : BaseDetailedViewModel> :
    BaseFragment<FragmentDetailsBinding, VModel>(FragmentDetailsBinding::inflate) {

    val creditsListAdapter = CreditsListAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment_container
            duration = 300
            scrimColor = Color.TRANSPARENT
        }
    }

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
                            context, uiState.exception.localizedMessage, Toast.LENGTH_SHORT
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

    protected open fun updateShowUi(showDetailed: IShowDetailed) {
//        setAdapter()
//        adapter.submitList(tvDetailed.seasons)
        with(binding) {
            showCard.transitionName = showDetailed.title
            errorDetailedSeriesTv.isVisible = false
            mainDetailView.isVisible = true
            itemsRecycler.isVisible = true
            progressBar.isVisible = false
            statusGroup.check(showDetailed.watchStatus)
            setupRadioButtonListeners()

            var buffer = ""
            seriesTitleTv.text = if (showDetailed.firstAirDate.isNullOrEmpty()) showDetailed.title
            else {
                requireContext().getString(
                    R.string.title_year_parenthesis,
                    showDetailed.title,
                    showDetailed.firstAirDate?.dropLast(6)
                )
            }
            overviewTv.text =
                showDetailed.overview.ifEmpty { requireContext().getText(R.string.no_overview) }
            taglineTv.text = showDetailed.tagline.ifEmpty { getString(R.string.no_tagline_string) }
            for (i in showDetailed.genres.indices) {
                buffer += showDetailed.genres[i].name + if (showDetailed.genres.indices.last != i) {
                    ", "
                } else "."
            }
            genreTv.text = buffer
            userscore.percentage = showDetailed.rating * 10f
            backdropIv.setImageWithGlide(
                "${IMAGE_BASE_URL}/t/p/w500/${showDetailed.backdropUrl}",
                com.flacrow.core.R.drawable.ic_placeholder_image_24
            )
            lifecycleScope.launch {
                viewModel.creditsPagingDataState.collect { creditsState ->
                    when (creditsState) {
                        is BaseDetailedViewModel.CreditsState.Success -> creditsListAdapter.submitList(
                            creditsState.creditsRecyclerItem
                        )
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