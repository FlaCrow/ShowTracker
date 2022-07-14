package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.flacrow.showtracker.R
import com.flacrow.showtracker.api.Season
import com.flacrow.showtracker.databinding.SeasonsItemBinding

class SeasonsListAdapter(private val onEpisodePickerValueChanged: (Int, Int) -> Unit) :
    ListAdapter<Season, SeasonsListAdapter.SeasonsViewHolder>(DiffCallback()) {

    inner class SeasonsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.seasons_item, parent, false)
    ) {
        private val binding = SeasonsItemBinding.bind(itemView)
        fun bind(season: Season, position: Int) {
            binding.apply {
                Glide
                    .with(root.context)
                    .load("https://image.tmdb.org/t/p/w500/${season.poster_path}")
                    .error(R.drawable.ic_placeholder_image_50)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(seasonPosterIv)
                seasonNumTv.text =
                    root.context.getString(R.string.season_string, season.season_number)
                maxEpTv.text =
                    root.context.getString(
                        R.string.out_of,
                        season.episode_count
                    )
                airDateTv.text =
                    root.context.getString(
                        R.string.aired_string,
                        season.air_date ?: root.context.getString(R.string.no_info)
                    )
                epDonePicker.wrapSelectorWheel = false
                epDonePicker.maxValue = season.episode_count
                epDonePicker.value = season.epDone
                epDonePicker.setOnScrollListener { numberPicker, scrollState ->
                    if (scrollState == SCROLL_STATE_IDLE)
                        onEpisodePickerValueChanged.invoke(
                            position,
                            numberPicker.value
                        )
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonsViewHolder {
        return SeasonsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SeasonsViewHolder, position: Int) {
        getItem(position)?.let { season ->
            holder.bind(season, position)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Season>() {
        override fun areItemsTheSame(oldItem: Season, newItem: Season): Boolean {
            return oldItem.season_number == newItem.season_number
        }

        override fun areContentsTheSame(oldItem: Season, newItem: Season): Boolean {
            return oldItem == newItem
        }
    }
}