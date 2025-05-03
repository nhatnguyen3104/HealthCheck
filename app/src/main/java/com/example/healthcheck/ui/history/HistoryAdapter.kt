package com.example.healthcheck.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcheck.databinding.ItemHistoryBinding
import com.example.healthcheck.model.HealthData

class HistoryAdapter(
    private val onDeleteClicked: (HealthData) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList = listOf<HealthData>()


    fun submitList(list: List<HealthData>) {
        historyList = list
        notifyDataSetChanged()
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: HealthData) {
            binding.tvHeartRate.text =
                if (data.measureHeartRate) "Nhịp tim: ${data.heartRate}" else ""
            binding.tvSpO2.text =
                if (data.measureSpO2) "Nồng độ Oxy: ${data.spo2}" else ""
            binding.tvTemperature.text =
                if (data.measureTemperature) "Nhiệt độ: ${data.temperature}" else ""
            binding.tvName.text = "Người đo: ${data.name ?: "Không rõ"}"
            binding.tvTime.text = "Thời gian: ${data.time ?: "Không rõ"}"

            var hasWarning = false
            val warnings = mutableListOf<String>()

            if (data.measureHeartRate && (data.heartRate ?: 0 < 60 || data.heartRate ?: 0 > 100)) {
                binding.tvHeartRate.setTextColor(Color.RED)
                warnings.add("⚠️ Cảnh báo: Nhịp tim không bình thường")
                hasWarning = true
            } else {
                binding.tvHeartRate.setTextColor(Color.BLACK)
            }

            if (data.measureSpO2 && (data.spo2 ?: 100 < 95)) {
                binding.tvSpO2.setTextColor(Color.RED)
                warnings.add("⚠️ Cảnh báo: Nồng độ Oxy thấp")
                hasWarning = true
            } else {
                binding.tvSpO2.setTextColor(Color.BLACK)
            }

            if (data.measureTemperature && ((data.temperature ?: 0.0) < 36.0 || (data.temperature ?: 0.0) > 37.5)) {
                binding.tvTemperature.setTextColor(Color.RED)
                warnings.add("⚠️ Cảnh báo: Nhiệt độ bất thường")
                hasWarning = true
            } else {
                binding.tvTemperature.setTextColor(Color.BLACK)
            }

//            if (warnings.isNotEmpty()) {
//                binding.tvWarningLabel.text = warnings.joinToString("\n")
//                binding.tvWarningLabel.visibility = View.VISIBLE
//            } else {
//                binding.tvWarningLabel.visibility = View.GONE
//            }

            binding.tvWarningLabel.visibility = if (warnings.isNotEmpty()) View.VISIBLE else View.GONE
            binding.tvWarningLabel.text = warnings.joinToString("\n")

            binding.tvWarning.visibility = if (hasWarning) View.VISIBLE else View.GONE

            binding.btnDelete.setOnClickListener {
                onDeleteClicked(data)
            }
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
