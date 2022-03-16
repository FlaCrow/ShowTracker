package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flacrow.showtracker.R
import com.flacrow.showtracker.databinding.LoadingItemBinding

class LoadShowsStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadShowsStateAdapter.LoadStateViewHolder>() {

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(parent, retry)
    }


    //ViewHolder
    class LoadStateViewHolder(parent: ViewGroup, private val retry: () -> Unit) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.loading_item, parent, false)
    ) {
        private val binding = LoadingItemBinding.bind(itemView)

        fun bind(loadState: LoadState) {
            with(binding) {
                if (loadState is LoadState.Error) {
                    errorTv.text = loadState.error.localizedMessage
                    retryTv.setOnClickListener { retry() }
                }
                errorTv.isVisible = loadState is LoadState.Error
                retryTv.isVisible = loadState is LoadState.Error
                progressBar.isVisible = loadState is LoadState.Loading
            }
        }

    }

}