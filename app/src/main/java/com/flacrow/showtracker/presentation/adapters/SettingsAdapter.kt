package com.flacrow.showtracker.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flacrow.showtracker.R
import com.flacrow.showtracker.databinding.SettingsItemBinding


class SettingsAdapter(
    private val onActionItemClicked: (ActionTypes) -> Unit,
    private val onItemSwitched: (SwitchableTypes, Boolean) -> Unit
) :
    ListAdapter<SettingsPageItem, SettingsAdapter.SettingsViewHolder>(DiffCallback()) {


    //viewholder
    inner class SettingsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.settings_item, parent, false)
    ) {
        private val binding = SettingsItemBinding.bind(itemView)
        fun bind(item: SettingsPageItem) {

            when (item.type) {
                is SettingsItem.SwitchableItem -> {
                    binding.settingsItemSwitch.isVisible = true
                    binding.settingsItemSwitch.isChecked = item.type.state
                    binding.settingsItemSwitch.setOnCheckedChangeListener { _, isChecked ->
                        onItemSwitched(
                            item.type.switchable,
                            isChecked
                        )
                    }
                    binding.root.isClickable = false
                }
                is SettingsItem.ActionItem -> {

                    binding.root.isClickable = true
                    binding.root.setOnClickListener { onActionItemClicked.invoke(item.type.action) }
                }
            }
            binding.settingsItemTv.text = item.title
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<SettingsPageItem>() {
        override fun areItemsTheSame(
            oldItem: SettingsPageItem,
            newItem: SettingsPageItem
        ): Boolean = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: SettingsPageItem,
            newItem: SettingsPageItem
        ): Boolean = oldItem == newItem

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}