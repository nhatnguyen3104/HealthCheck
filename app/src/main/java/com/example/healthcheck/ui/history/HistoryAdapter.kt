package com.example.healthcheck.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcheck.databinding.ItemHistoryBinding
import com.example.healthcheck.model.HealthData

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList = listOf<HealthData>()

    fun submitList(list: List<HealthData>) {
        historyList = list
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: HealthData) {
            binding.tvHeartRate.text = "Nhịp tim: ${data.heartRate}"
            binding.tvSpO2.text = "SpO2: ${data.spo2}"
            binding.tvTemperature.text = "Nhiệt độ: ${data.temperature}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(historyList[position])
    }

    override fun getItemCount(): Int = historyList.size
}
