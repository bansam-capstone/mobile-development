package com.bangkit.capstone.presentation.view.adapter

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.databinding.ItemListSuggestionBinding

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
        val binding = ItemListSuggestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val address = suggestions[position]
        holder.bind(address)
    }

    override fun getItemCount(): Int = suggestions.size

    inner class SuggestionViewHolder(private val binding: ItemListSuggestionBinding) : RecyclerView.ViewHolder(binding.root) {
        private val locationTitle: TextView = binding.locationTitle
        private val locationSubtitle: TextView = binding.locationSubtitle

        fun bind(address: Address) {
            locationTitle.text = address.thoroughfare ?: address.getAddressLine(0) ?: "Nama Jalan Tidak Tersedia"
            locationSubtitle.text = address.countryName ?: address.locality ?: address.subLocality ?: "Tidak tersedia"

            binding.itemListSuggestion.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClick(suggestions[adapterPosition])
                }
            }
        }
    }
}
