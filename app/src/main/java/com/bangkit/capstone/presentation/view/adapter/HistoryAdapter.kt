package com.bangkit.capstone.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.databinding.ItemListHistoryBinding
import com.bangkit.capstone.util.HistoryDiffCallback

class HistoryAdapter(
    private val onItemClick: (SearchHistoryEntity) -> Unit
) : ListAdapter<SearchHistoryEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    inner class HistoryViewHolder(private val binding: ItemListHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SearchHistoryEntity) {
            binding.historyTitle.text = item.title
            binding.historySubtitle.text = item.subtitle
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemListHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
