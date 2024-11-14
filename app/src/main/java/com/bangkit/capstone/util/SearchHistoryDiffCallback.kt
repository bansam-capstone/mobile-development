package com.bangkit.capstone.util

import androidx.recyclerview.widget.DiffUtil
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity

class HistoryDiffCallback : DiffUtil.ItemCallback<SearchHistoryEntity>() {
    override fun areItemsTheSame(oldItem: SearchHistoryEntity, newItem: SearchHistoryEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SearchHistoryEntity, newItem: SearchHistoryEntity): Boolean {
        return oldItem == newItem
    }
}