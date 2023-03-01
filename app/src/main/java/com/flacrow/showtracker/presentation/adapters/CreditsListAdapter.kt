package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flacrow.core.utils.Extensions.setImageWithGlide
import com.flacrow.showtracker.R
import com.flacrow.showtracker.data.models.CastCredits
import com.flacrow.showtracker.data.models.CreditsRecyclerItem
import com.flacrow.showtracker.data.models.CrewCredits
import com.flacrow.showtracker.databinding.ActorItemBinding

class CreditsListAdapter :
    PagingDataAdapter<CreditsRecyclerItem, RecyclerView.ViewHolder>(DiffCallback()) {

    private class DiffCallback : DiffUtil.ItemCallback<CreditsRecyclerItem>() {

        override fun areItemsTheSame(
            oldItem: CreditsRecyclerItem,
            newItem: CreditsRecyclerItem
        ): Boolean {
            return if (oldItem::class != newItem::class) false
            else if (oldItem is CrewCredits && newItem is CrewCredits) oldItem.personId == newItem.personId
            else false
        }


        override fun areContentsTheSame(
            oldItem: CreditsRecyclerItem,
            newItem: CreditsRecyclerItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditsViewHolder {
        return CreditsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CreditsViewHolder) holder.bind(getItem(position))
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        Glide.with(holder.itemView.context).clear(holder.itemView.findViewById<ImageView>(R.id.person_photo_iv))
        super.onViewRecycled(holder)
    }
    


    inner class CreditsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.actor_item, parent, false)
    ) {
        private val binding = ActorItemBinding.bind(itemView)

        fun bind(creditsItem: CreditsRecyclerItem?) {
            with(binding) {
                when (creditsItem) {
                    is CrewCredits -> {
                        personNameTv.text = creditsItem.name
                        if (creditsItem.photoUrl != null) personPhotoIv.setImageWithGlide(
                            "https://image.tmdb.org/t/p/w92/${creditsItem.photoUrl}",
                            com.flacrow.core.R.drawable.ic_baseline_person_outline_24
                        )
                        else personPhotoIv.setImageResource(com.flacrow.core.R.drawable.ic_baseline_person_outline_24)
                        knownForTv.text = creditsItem.department
                        roleTv.text = creditsItem.role
                    }
                    is CastCredits -> {
                        personNameTv.text = creditsItem.name
                        if (creditsItem.photoUrl != null) personPhotoIv.setImageWithGlide(
                            "https://image.tmdb.org/t/p/w92/${creditsItem.photoUrl}",
                            com.flacrow.core.R.drawable.ic_baseline_person_outline_24
                        )
                        else personPhotoIv.setImageResource(com.flacrow.core.R.drawable.ic_baseline_person_outline_24)
                        knownForTv.text = creditsItem.department
                        roleTv.text = creditsItem.role
                    }
                }

            }
        }

    }
}