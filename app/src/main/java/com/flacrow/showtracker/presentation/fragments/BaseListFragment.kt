package com.flacrow.showtracker.presentation.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.paging.LoadState
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.presentation.adapters.ShowListAdapter
import com.flacrow.showtracker.databinding.FragmentShowListBinding
import com.flacrow.showtracker.presentation.ViewModels.BaseViewModel
import com.flacrow.showtracker.presentation.adapters.LoadShowsStateAdapter
import com.flacrow.showtracker.utils.ConstantValues
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */


abstract class BaseListFragment<VModel : BaseViewModel> :
    BaseFragment<FragmentShowListBinding, VModel>(FragmentShowListBinding::inflate) {

    private val valueAnimator =
        ValueAnimator.ofInt(
            0,
            (30 * (context?.resources?.displayMetrics?.density
                ?: ConstantValues.DEFAULT_DENSITY)).toInt()
        )


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


    private fun setupTabListeners() {
        binding.searchTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Toast.makeText(context, "onTabSelected", Toast.LENGTH_SHORT).show()
                viewModel.setSelectedTab(tab.position)
                getShowList((binding.toolbar.menu.findItem(R.id.action_search).actionView as SearchView).query.toString())
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
        super.onDestroyView()
        valueAnimator.removeAllUpdateListeners()
    }


    protected open fun getShowList(query: String) {
        lifecycleScope.launch {
            viewModel.getShowList(query).collect {
                adapter.submitData(it)
            }

        }
    }

    abstract fun onListElementClick(show: IShow)
}