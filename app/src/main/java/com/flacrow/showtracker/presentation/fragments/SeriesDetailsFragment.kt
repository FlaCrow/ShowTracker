package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.flacrow.showtracker.databinding.FragmentShowListBinding
import com.flacrow.showtracker.presentation.ViewModels.SeriesDetailsViewModel
import com.flacrow.showtracker.presentation.ViewModels.ShowListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


class SeriesDetailsFragment : Fragment(R.layout.fragment_series_details) {

    private var _binding: FragmentSeriesDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SeriesDetailsViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSeriesDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SeriesDetailsFragmentArgs by navArgs()

        lifecycleScope.launch {
            viewModel.getData(args.seriesId)
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is SeriesDetailsViewModel.SeriesDetailsState.Success -> updateUi(
                        uiState.tvDetailed
                    )
                    is SeriesDetailsViewModel.SeriesDetailsState.Error
                    -> throw uiState.exception
                }
            }

        }

    }

    fun updateUi(tvDetailed: TvDetailed) {
        binding.seriesTitleTv.text = tvDetailed.title
        binding.seriesYearTv.text = tvDetailed.firstAirDate
        binding.overviewTv.text = tvDetailed.overview
        binding.taglineTv.text = tvDetailed.tagline
        binding.userscore.text = tvDetailed.rating.toString()
        Glide
            .with(requireContext())
            .load("https://image.tmdb.org/t/p/w500/${tvDetailed.backdropUrl}")
            .error(R.drawable.ic_placeholder_image_50)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.backdropIv)
    }
}