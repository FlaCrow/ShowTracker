package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.data.models.Show
import com.flacrow.showtracker.databinding.ShowentityItemBinding

class ShowListAdapter(private val navigate: (Show) -> Unit) :
    PagingDataAdapter<Show, ShowListAdapter.ViewHolder>(
        DiffCallback()
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            ShowentityItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    private class DiffCallback : DiffUtil.ItemCallback<Show>() {
        override fun areItemsTheSame(oldItem: Show, newItem: Show): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Show, newItem: Show): Boolean {
            return oldItem == newItem
        }
    }


    inner class ViewHolder(private val binding: ShowentityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(showItem: Show) {
            binding.apply {
                Glide
                    .with(root.context)
                    .load("https://image.tmdb.org/t/p/w500/${showItem.poster}")
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(posterIv)
                titleTv.text = showItem.title
                binding.root.setOnClickListener { navigate.invoke(showItem) }
            }
        }
    }

}
