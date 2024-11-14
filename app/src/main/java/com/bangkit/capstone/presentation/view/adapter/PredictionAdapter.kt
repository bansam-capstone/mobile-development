package com.bangkit.capstone.presentation.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.capstone.R
import com.bangkit.capstone.data.remote.response.LocationResponse
import com.bangkit.capstone.databinding.ItemListPredictionBinding

class PredictionAdapter(
    private val locationAddressMap: Map<String, String>,
    private val onItemClick: (LocationResponse) -> Unit
) : RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder>() {

    private var predictions: List<LocationResponse> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val binding = ItemListPredictionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PredictionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        holder.bind(predictions[position], locationAddressMap, onItemClick)
    }

    override fun getItemCount(): Int = predictions.size

    fun setData(newPredictions: List<LocationResponse>) {
        predictions = newPredictions
        notifyDataSetChanged()
    }

    inner class PredictionViewHolder(private val binding: ItemListPredictionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(prediction: LocationResponse, locationAddressMap: Map<String, String>, onItemClick: (LocationResponse) -> Unit) {
            binding.tvAddress.text = locationAddressMap[prediction.location] ?: "Alamat tidak tersedia"

            when (prediction.riskLevel) {
                "Aman" -> {
                    binding.tvStatus.text = "Aman"
                    binding.statusIcon.setImageResource(R.drawable.ic_prediction_safe)
                    binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.success_900))
                    binding.llStatus.setBackgroundResource(R.drawable.bg_status_safe)
                }
                "Waspada" -> {
                    binding.tvStatus.text = "Waspada"
                    binding.llStatus.setBackgroundResource(R.drawable.bg_status_warning)
                    binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.warning_900))
                    binding.statusIcon.setImageResource(R.drawable.ic_prediction_warning)
                }
                "Bahaya" -> {
                    binding.tvStatus.text = "Bahaya"
                    binding.llStatus.setBackgroundResource(R.drawable.bg_status_danger)
                    binding.tvStatus.setTextColor(binding.root.context.getColor(R.color.danger_900))
                    binding.statusIcon.setImageResource(R.drawable.ic_prediction_danger)
                }
                else -> {
                    binding.tvStatus.text = "Tidak Diketahui"
                }
            }

            binding.root.setOnClickListener { onItemClick(prediction) }
        }
    }
}
