package com.flacrow.showtracker.presentation.fragments

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.flacrow.showtracker.R
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.presentation.ViewModels.ShowListViewModel
import com.flacrow.showtracker.presentation.adapters.ShowListAdapter
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.databinding.FragmentShowListBinding
import com.flacrow.showtracker.presentation.adapters.LoadShowsStateAdapter
import com.flacrow.showtracker.utils.ConstantValues
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


class ShowListFragment : Fragment(R.layout.fragment_show_list) {

    private var _binding: FragmentShowListBinding? = null
    private var hasInitiatedInitialCall = false

    private val valueAnimator =
        ValueAnimator.ofInt(
            0,
            (30 * (context?.resources?.displayMetrics?.density
                ?: ConstantValues.DEFAULT_DENSITY)).toInt()
        )

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val adapter =
        ShowListAdapter { show -> navigate(show) }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: ShowListViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentShowListBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAdapter()
        setupMenu()
        setupTabListeners()
        if (!hasInitiatedInitialCall) {
            getShowList()
            hasInitiatedInitialCall = true
        }
    }

    private fun setupTabListeners() {
        binding.searchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(context, "onTabSelected", Toast.LENGTH_SHORT).show()
                viewModel.setSelectedTab(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                Toast.makeText(context, "onTabUnselected", Toast.LENGTH_SHORT).show()
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Toast.makeText(context, "onTabReselected", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_main)
        val searchView = binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView
        valueAnimator.apply {
            duration = ConstantValues.ANIMATION_DURATION
            addUpdateListener {
                val tabLayoutParams = binding.searchTabs.layoutParams
                tabLayoutParams.height = it.animatedValue as Int
                binding.searchTabs.layoutParams = tabLayoutParams
            }
            searchView.setOnQueryTextFocusChangeListener { _, isInFocus ->
                binding.searchTabs.getTabAt(0)?.view?.isClickable = isInFocus
                binding.searchTabs.getTabAt(1)?.view?.isClickable = isInFocus
                binding.searchTabs.getTabAt(viewModel.tabSelected.value)?.select()
                when (isInFocus) {
                    true -> {
                        binding.searchTabs.isVisible = isInFocus
                        valueAnimator.start()
                    }

                    false -> {
                        valueAnimator.reverse()
                    }
                }
            }
        }

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    lifecycleScope.launch {
                        viewModel.getMovieOrTvList(
                            viewModel.tabSelected.value,
                            query ?: ""
                        )
                            .collect {
                                adapter.submitData(it)
                            }
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }


            }
        )
    }
//        binding.toolbar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.action_search -> {
//                    val searchView = it.actionView as SearchView
//                    valueAnimator.apply {
//                        duration = ConstantValues.ANIMATION_DURATION
//                        addUpdateListener {
//                            val tabLayoutParams = binding.searchTabs.layoutParams
//                            tabLayoutParams.height = it.animatedValue as Int
//                            binding.searchTabs.layoutParams = tabLayoutParams
//                        }
//                        searchView.setOnQueryTextFocusChangeListener { _, isInFocus ->
//                            binding.searchTabs.getTabAt(0)?.view?.isClickable = isInFocus
//                            binding.searchTabs.getTabAt(1)?.view?.isClickable = isInFocus
//                            when (isInFocus) {
//                                true -> {
//                                    binding.searchTabs.isVisible = isInFocus
//                                    valueAnimator.start()
//                                }
//
//                                false -> {
//                                    valueAnimator.reverse()
//                                }
//                            }
//                        }
//                    }
//
//                    searchView.setOnQueryTextListener(
//                        object : SearchView.OnQueryTextListener {
//                            override fun onQueryTextSubmit(query: String?): Boolean {
//                                lifecycleScope.launch {
//                                    viewModel.getMovieOrTvList(
//                                        viewModel.tabSelected.value,
//                                        query ?: ""
//                                    )
//                                        .collect {
//                                            adapter.submitData(it)
//                                        }
//                                }
//                                return true
//                            }
//
//                            override fun onQueryTextChange(newText: String?): Boolean {
//                                return false
//                            }
//
//
//                        }
//                    )
//                    true
//                }
//                else -> false
//            }
//        }
//    }

    private fun setAdapter() {
        binding.showlistRecycler.adapter =
            adapter.withLoadStateFooter(LoadShowsStateAdapter { adapter.retry() })
        adapter.addLoadStateListener { state ->
            with(binding) {
                showlistRecycler.isVisible = state.refresh != LoadState.Loading
                progressBar.isVisible = state.refresh == LoadState.Loading
                errorTv.isVisible = if (state.refresh is LoadState.Error) {
                    showlistRecycler.isVisible = false
                    errorTv.text = (state.refresh as LoadState.Error).error.localizedMessage
                    true
                } else false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        valueAnimator.removeAllUpdateListeners()
        _binding = null
    }


    private fun getShowList() {
        lifecycleScope.launch {
            viewModel.getTrendingList().collect {
                adapter.submitData(it)
            }

        }
    }

    private fun navigate(show: Show) {
        Toast.makeText(this.context, show.title, Toast.LENGTH_LONG).show()
        when (show.mediaType) {
            "tv" ->
                findNavController().navigate(
                    ShowListFragmentDirections.actionShowListFragmentToSeriesDetailsFragment(show.id)
                )
            "movie" -> Toast.makeText(this.context, show.title, Toast.LENGTH_LONG).show()

        }
    }
}