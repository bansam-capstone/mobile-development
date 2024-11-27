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
            locationTitle.text = when {
                address.featureName != null || address.thoroughfare != null -> {
                    address.thoroughfare ?: address.featureName ?: "Nama jalan tidak tersedia"
                }
                address.subLocality != null -> {
                    address.subLocality ?: "Nama kecamatan tidak tersedia"
                }
                address.locality != null -> {
                    address.locality ?: "Nama kota tidak tersedia"
                }
                else -> {
                    address.getAddressLine(0) ?: "Alamat tidak tersedia"
                }
            }

            locationSubtitle.text = when {
                address.locality != null -> {
                    address.locality ?: "Kota tidak tersedia"
                }
                address.subLocality != null -> {
                    address.subLocality ?: "Kecamatan tidak tersedia"
                }
                address.countryName != null -> {
                    address.countryName ?: "Negara tidak tersedia"
                }
                else -> {
                    "Tidak tersedia"
                }
            }

            binding.itemListSuggestion.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onClick(suggestions[adapterPosition])
                }
            }
        }
    }
}
