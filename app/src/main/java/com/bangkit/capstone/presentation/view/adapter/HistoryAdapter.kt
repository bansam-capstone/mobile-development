package com.bangkit.capstone.presentation.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.R
import com.bangkit.capstone.data.local.entity.SearchHistoryEntity
import com.bangkit.capstone.util.HistoryDiffCallback

class HistoryAdapter(
    private val onItemClick: (SearchHistoryEntity) -> Unit
) : ListAdapter<SearchHistoryEntity, HistoryAdapter.HistoryViewHolder>(HistoryDiffCallback()) {

    inner class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.historyTitle)
        private val subtitleTextView: TextView = view.findViewById(R.id.historySubtitle)

        fun bind(item: SearchHistoryEntity) {
            titleTextView.text = item.title
            subtitleTextView.text = item.subtitle
            itemView.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
