package com.bangkit.capstone.presentation.view.adapter

import android.location.Address
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.R

class SuggestionsAdapter(
    private val onClick: (Address) -> Unit
) : RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>() {

    private val suggestions = mutableListOf<Address>()

    fun submitList(addressList: List<Address>) {
        suggestions.clear()
        suggestions.addAll(addressList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_suggestion, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val address = suggestions[position]
        holder.bind(address)
    }

    override fun getItemCount(): Int = suggestions.size

    inner class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationTitle: TextView = itemView.findViewById(R.id.locationTitle)
        private val locationSubtitle: TextView = itemView.findViewById(R.id.locationSubtitle)

        fun bind(address: Address) {
            locationTitle.text = address.featureName ?: "Unknown Location"
            locationSubtitle.text = address.getAddressLine(0)

            itemView.setOnClickListener {
                onClick(address)
            }
        }
    }
}
