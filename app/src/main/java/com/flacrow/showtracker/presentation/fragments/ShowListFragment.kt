package com.flacrow.showtracker.presentation.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.flacrow.showtracker.R
import com.flacrow.showtracker.ShowApp
import com.flacrow.showtracker.appComponent
import com.flacrow.showtracker.presentation.ViewModels.ShowListViewModel
import com.flacrow.showtracker.presentation.adapters.ShowListAdapter
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.databinding.FragmentShowListBinding
import com.flacrow.showtracker.presentation.adapters.LoadShowsStateAdapter
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ShowListFragment : Fragment(R.layout.fragment_show_list) {

    private var _binding: FragmentShowListBinding? = null

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
        getShowList()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getShowList() {
        lifecycleScope.launch {
            viewModel.getList().collect {
                adapter.submitData(it)
            }

        }
    }

    private fun navigate(show: Show) {
        Toast.makeText(this.context, show.title, Toast.LENGTH_LONG).show()
    }
}