package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.flacrow.core.utils.ConstantValues.MOVIE_TYPE_STRING
import com.flacrow.core.utils.Extensions.setImageWithGlide
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.IShow
import com.flacrow.showtracker.databinding.ShowentityItemBinding

class ShowListAdapter(private val navigate: (IShow) -> Unit) :
    PagingDataAdapter<IShow, ShowListAdapter.ViewHolder>(
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

    private class DiffCallback : DiffUtil.ItemCallback<IShow>() {
        override fun areItemsTheSame(oldItem: IShow, newItem: IShow): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: IShow, newItem: IShow): Boolean {
            return oldItem == newItem
        }
    }


    inner class ViewHolder(private val binding: ShowentityItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(showItem: IShow) {
            binding.apply {
                posterIv.setImageWithGlide("https://image.tmdb.org/t/p/w500/${showItem.posterUrl}")
                titleTv.text = showItem.title
                releasedDateTv.text =
                    root.context.getString(
                        R.string.released_in_string,
                        showItem.firstAirDate.let { if (it.isNullOrEmpty()) root.context.getString(R.string.no_info) else it })
                ratingPieView.percentage = showItem.rating * 10f
                if (showItem.mediaType == MOVIE_TYPE_STRING) {
                    mediaTypeIv.setImageResource(R.drawable.ic_baseline_movie_24)
                } else mediaTypeIv.setImageResource(R.drawable.ic_baseline_tv_24)
                binding.root.setOnClickListener { navigate.invoke(showItem) }
            }
        }
    }

}
