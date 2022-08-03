package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.SeasonLocal
import com.flacrow.showtracker.databinding.SeasonsItemBinding
import com.flacrow.showtracker.databinding.WatchHistoryItemBinding
import com.flacrow.showtracker.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.showtracker.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.showtracker.utils.ConstantValues.STATUS_WATCHING
import com.flacrow.showtracker.utils.Extensions.setImageWithGlide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SeasonsListAdapter(
    private val onEpisodePickerValueChanged: (Int, Flow<Int>) -> Unit,
    private val onExpandButtonClicked: (Int) -> Unit,
) :
    ListAdapter<SeasonAdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {
    inner class SeasonsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.seasons_item, parent, false)
    ) {
        private val binding = SeasonsItemBinding.bind(itemView)
        fun bind(season: SeasonLocal, position: Int) {
            binding.apply {
                seasonPosterIv.setImageWithGlide("https://image.tmdb.org/t/p/w500/${season.posterUrl}")
                seasonNumTv.text =
                    root.context.getString(R.string.season_string, season.seasonNumber)
                maxEpTv.text =
                    root.context.getString(
                        R.string.out_of,
                        season.episodeCount.toString()
                    )
                airDateTv.text =
                    root.context.getString(
                        R.string.aired_string,
                        season.dateAired ?: root.context.getString(R.string.no_info)
                    )
                epDonePicker.isVisible = season.watchStatus == STATUS_WATCHING
                epDoneImmutableTv.isVisible = !epDonePicker.isVisible
                epDonePicker.wrapSelectorWheel = false
                epDonePicker.maxValue = season.episodeCount
                epDonePicker.value = season.episodeDone
                when (season.watchStatus) {
                    STATUS_PLAN_TO_WATCH -> {
                        epDoneImmutableTv.text = "0"
                        detailsButton.isVisible = false
                    }
                    STATUS_COMPLETED ->
                        epDoneImmutableTv.text = season.episodeCount.toString()
                    else -> {
                        epDoneImmutableTv.text = season.episodeDone.toString()
                        detailsButton.isVisible = true
                    }
                }
                val flow = callbackFlow {
                    epDonePicker.setOnValueChangedListener { _, _, newValue ->
                        trySend(newValue)
                    }
                    awaitClose { epDonePicker.setOnValueChangedListener(null) }
                }
                onEpisodePickerValueChanged.invoke(position, flow)
                detailsButton.setOnClickListener {
                    onExpandButtonClicked.invoke(position)
                }
            }

        }
    }

    inner class WatchHistoryViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.watch_history_item, parent, false)
        ) {
        private val binding = WatchHistoryItemBinding.bind(itemView)

        fun bind(dateItem: DateItem) {
            binding.episodeHistoryTv.text = binding.root.context.getString(
                R.string.watch_history_string,
                dateItem.position.toString(),
                dateItem.date.toString()
            )
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.seasons_item -> SeasonsViewHolder(parent)
            R.layout.watch_history_item -> WatchHistoryViewHolder(parent)
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SeasonLocal -> R.layout.seasons_item
            is DateItem -> R.layout.watch_history_item
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.seasons_item ->
                (holder as SeasonsViewHolder).bind(getItem(position) as SeasonLocal, position)
            R.layout.watch_history_item ->
                (holder as WatchHistoryViewHolder).bind(getItem(position) as DateItem)
            else ->
                throw IllegalArgumentException()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SeasonAdapterItem>() {
        override fun areItemsTheSame(
            oldItem: SeasonAdapterItem,
            newItem: SeasonAdapterItem,
        ): Boolean {
            return if (oldItem::class != newItem::class) false
            else if (oldItem is SeasonLocal && newItem is SeasonLocal) oldItem.seasonNumber == newItem.seasonNumber
            else false

        }

        override fun areContentsTheSame(
            oldItem: SeasonAdapterItem,
            newItem: SeasonAdapterItem,
        ): Boolean {
            return oldItem == newItem
        }
    }
}