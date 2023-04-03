package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flacrow.core.utils.Config.IMAGE_BASE_URL
import com.flacrow.core.utils.ConstantValues.STATUS_COMPLETED
import com.flacrow.core.utils.ConstantValues.STATUS_PLAN_TO_WATCH
import com.flacrow.core.utils.ConstantValues.STATUS_WATCHING
import com.flacrow.core.utils.Extensions.setImageWithGlide
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.DateItem
import com.flacrow.showtracker.data.models.DetailedRecyclerItem
import com.flacrow.showtracker.data.models.Episode
import com.flacrow.showtracker.data.models.SeasonLocal
import com.flacrow.showtracker.databinding.EpisodeItemBinding
import com.flacrow.showtracker.databinding.SeasonsItemBinding
import com.flacrow.showtracker.databinding.WatchHistoryItemBinding
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


class SeasonsListAdapter(
    private val onEpisodePickerValueChanged: (Flow<Pair<Int, Int>>) -> Unit,
    private val onExpandButtonClicked: (Int) -> Unit,
) : ListAdapter<DetailedRecyclerItem, RecyclerView.ViewHolder>(DiffCallback()) {
    inner class SeasonsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.seasons_item, parent, false)
    ) {
        private val binding = SeasonsItemBinding.bind(itemView)
        fun bind(season: SeasonLocal) {
            binding.apply {
                seasonPosterIv.setImageWithGlide(
                    "${IMAGE_BASE_URL}/t/p/w185/${season.posterUrl}",
                    com.flacrow.core.R.drawable.ic_placeholder_image_24
                )
                seasonNumTv.text =
                    root.context.getString(R.string.season_string, season.seasonNumber)
                maxEpTv.text = root.context.getString(
                    R.string.out_of, season.episodeCount.toString()
                )
                airDateTv.text = root.context.getString(
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
                    STATUS_COMPLETED -> epDoneImmutableTv.text = season.episodeCount.toString()
                    else -> {
                        epDoneImmutableTv.text = season.episodeDone.toString()
                        detailsButton.isVisible = true
                    }
                }
                val flow = callbackFlow {
                    epDonePicker.setOnValueChangedListener { _, _, newValue ->
                        if (getItem(layoutPosition) is SeasonLocal) trySend(
                            Pair(
                                getItemId(
                                    layoutPosition
                                ).toInt(), newValue
                            )
                        )
                    }
                    awaitClose { epDonePicker.setOnValueChangedListener(null) }
                }
                onEpisodePickerValueChanged.invoke(flow)
                detailsButton.setOnClickListener {
                    onExpandButtonClicked.invoke(getItemId(layoutPosition).toInt())
                }
            }

        }
    }

    inner class WatchHistoryViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.watch_history_item, parent, false)
    ) {
        private val binding = WatchHistoryItemBinding.bind(itemView)

        fun bind(dateItem: DateItem) {
            binding.episodeHistoryTv.text = binding.root.context.getString(
                R.string.watch_history_string,
                dateItem.position.toString(),
                dateItem.getLongFormattedString()
            )
        }

    }

    inner class EpisodeViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.episode_item, parent, false)
    ) {
        private val binding = EpisodeItemBinding.bind(itemView)

        fun bind(episodeItem: Episode) {
            with(binding) {
                episodeStillIv.isGone = episodeItem.stillUrl.isNullOrEmpty()
                episodeStillIv.setImageWithGlide(
                    "${IMAGE_BASE_URL}/t/p/w185/${episodeItem.stillUrl}",
                    com.flacrow.core.R.drawable.ic_placeholder_image_24
                )
                episodeNameTv.text = episodeItem.epName
                epRatingPie.percentage = episodeItem.epRating * 10
                watchedDateTv.text = episodeItem.epDateWatched?:  root.context.getString(R.string.watched_never_string)
                airedDateTv.text = episodeItem.epDateAired
                overviewTv.text = episodeItem.epOverview.ifEmpty { root.context.getString(R.string.no_overview_available_string) }
                epRatingPie.isGone = episodeItem.epVoteCount == 0
                epRatingPie.percentage = episodeItem.epRating * 10
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.seasons_item -> SeasonsViewHolder(parent)
            R.layout.watch_history_item -> WatchHistoryViewHolder(parent)
            R.layout.episode_item -> EpisodeViewHolder(parent)
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SeasonLocal -> R.layout.seasons_item
            is DateItem -> R.layout.watch_history_item
            is Episode -> R.layout.episode_item
            else -> throw IllegalArgumentException()
        }
    }

    //Temporary?
    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return when (item) {
            is SeasonLocal -> (item.seasonNumber).toLong()
            is DateItem -> item.date.time
            is Episode -> item.epId.toLong()
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            R.layout.seasons_item -> (holder as SeasonsViewHolder).bind(getItem(position) as SeasonLocal)
            R.layout.watch_history_item -> (holder as WatchHistoryViewHolder).bind(getItem(position) as DateItem)
            R.layout.episode_item -> (holder as EpisodeViewHolder).bind(getItem(position) as Episode)
            else -> throw IllegalArgumentException()
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<DetailedRecyclerItem>() {
        override fun areItemsTheSame(
            oldItem: DetailedRecyclerItem,
            newItem: DetailedRecyclerItem,
        ): Boolean {
            return if (oldItem::class != newItem::class) false
            else if (oldItem is SeasonLocal && newItem is SeasonLocal) oldItem.seasonNumber == newItem.seasonNumber
            else false

        }

        override fun areContentsTheSame(
            oldItem: DetailedRecyclerItem,
            newItem: DetailedRecyclerItem,
        ): Boolean {
            return oldItem == newItem
        }
    }
}