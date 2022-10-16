package com.flacrow.showtracker.presentation.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.databinding.FragmentShowListBinding
import com.flacrow.showtracker.presentation.adapters.LoadShowsStateAdapter
import com.flacrow.showtracker.presentation.adapters.ShowListAdapter
import com.flacrow.showtracker.presentation.viewModels.BaseViewModel
import com.flacrow.showtracker.utils.ConstantValues.ANIMATION_DURATION
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


abstract class BaseListFragment<VModel : BaseViewModel> :
    BaseFragment<FragmentShowListBinding, VModel>(FragmentShowListBinding::inflate) {

    abstract val playAnimations: Boolean
    private val updateListener = { state: CombinedLoadStates ->
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

    private var valueAnimator: ValueAnimator? = null


    protected val adapter =
        ShowListAdapter { show -> onListElementClick(show) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setAdapter()
        setupMenu()
        setupTabListeners()
        //if (viewModel.isDataNull()) {
        getShowList("")
        // }
    }


    private fun setAdapter() {
        binding.showlistRecycler.adapter =
            adapter.withLoadStateFooter(LoadShowsStateAdapter { adapter.retry() })
        adapter.addLoadStateListener(updateListener)
    }


    private fun setupTabListeners() {
        binding.searchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.setSelectedTab(tab.position)
                getShowList((binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView).query.toString())
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_main)

        val settingsButton = binding.toolbar.menu.findItem(R.id.action_settings)
        settingsButton.setOnMenuItemClickListener {
            findNavController().navigate(R.id.settingsFragment)
            true
        }

        val searchView =
            binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView
        valueAnimator = ValueAnimator.ofFloat(
            0f,
            48f * requireContext().resources.displayMetrics.density
        )
        valueAnimator?.apply {
            duration = ANIMATION_DURATION
            addUpdateListener {
                val tabLayoutParams = binding.searchTabs.layoutParams
                tabLayoutParams.height = (it.animatedValue as Float).roundToInt()
                binding.searchTabs.layoutParams = tabLayoutParams
            }

            binding.searchTabs.getTabAt(viewModel.tabSelected.value)?.select()
            searchView.setOnQueryTextFocusChangeListener { _, isInFocus ->

                if (playAnimations) {
                    binding.searchTabs.getTabAt(0)?.view?.isClickable = isInFocus
                    binding.searchTabs.getTabAt(1)?.view?.isClickable = isInFocus
                    when (isInFocus) {
                        true -> {
                            binding.searchTabs.isVisible = isInFocus
                            valueAnimator?.start()
                        }

                        false -> {
                            valueAnimator?.reverse()
                        }
                    }
                }
            }
        }

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    getShowList(query ?: "")
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText?.isEmpty() == true) {
                        getShowList("")
                    }
                    return false
                }


            }
        )
    }


    override fun onDestroyView() {
        valueAnimator?.removeAllUpdateListeners()
        adapter.removeLoadStateListener(updateListener)
        super.onDestroyView()
    }


    protected open fun getShowList(query: String) {
        lifecycleScope.launch {
            viewModel.getShowList(query).collect {
                adapter.submitData(it)
            }

        }
    }

    protected abstract fun onListElementClick(show: IShow)
}