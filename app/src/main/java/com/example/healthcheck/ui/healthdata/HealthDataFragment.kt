package com.example.healthcheck.ui.healthdata

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.healthcheck.databinding.FragmentHealthDataBinding
import com.example.healthcheck.viewmodel.HealthDataViewModel

class HealthDataFragment : Fragment() {

    private var _binding: FragmentHealthDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HealthDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HealthDataViewModel::class.java]

        showLoading(true)
        viewModel.startRealtimeHealthDataListener()

        viewModel.healthData.observe(viewLifecycleOwner) { healthData ->
            showLoading(false)
            binding.tvHeartRate.text = "Nhịp tim: ${healthData.heartRate} bpm"
            binding.tvSpO2.text = "SpO2: ${healthData.spo2} %"
            binding.tvTemperature.text = "Nhiệt độ: ${healthData.temperature} °C"

            viewModel.checkHealthData(
                healthData.heartRate,
                healthData.spo2,
                healthData.temperature
            )
            Log.d("HealthDataFragment", "Health Data received: $healthData")
        }

        viewModel.alertMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                triggerAlert(it)
                viewModel.resetAlert()
            }
        }

        binding.btnSaveData.setOnClickListener {
            viewModel.saveCurrentHealthData { success, message ->
                val toastMessage = if (success) "Đã lưu dữ liệu" else "Lưu thất bại: $message"
                Toast.makeText(requireContext(), toastMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun triggerAlert(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Cảnh báo sức khỏe")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
