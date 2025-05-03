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
import com.example.healthcheck.model.HealthData
import com.example.healthcheck.viewmodel.HealthDataViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
            viewModel.updateMeasureOption("all", isChecked)
        }

        binding.cbHeartRate.setOnCheckedChangeListener { _, isChecked ->
            // Nếu đang bật "Tất cả" và người dùng bỏ chọn một ô => bỏ chọn "Tất cả"
            if (viewModel.isAllSelected.value == true && !isChecked) {
                binding.cbSelectAll.isChecked = false
            }
            viewModel.updateMeasureOption("heartRate", isChecked)
        }

        binding.cbSpO2.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.isAllSelected.value == true && !isChecked) {
                binding.cbSelectAll.isChecked = false
            }
            viewModel.updateMeasureOption("spo2", isChecked)
        }

        binding.cbTemperature.setOnCheckedChangeListener { _, isChecked ->
            if (viewModel.isAllSelected.value == true && !isChecked) {
                binding.cbSelectAll.isChecked = false
            }
            viewModel.updateMeasureOption("temperature", isChecked)
        }

        viewModel.startRealtimeHealthDataListener()

        viewModel.healthData.observe(viewLifecycleOwner) { healthData ->
            showLoading(false)
            updateHealthUI(healthData)
        }

        viewModel.measureOptions.observe(viewLifecycleOwner) {options ->
            binding.cbSelectAll.isChecked = options.isAllSelected
            binding.cbHeartRate.isChecked = options.isAllSelected || options.measureHeartRate
            binding.cbSpO2.isChecked = options.isAllSelected || options.measureSpO2
            binding.cbTemperature.isChecked = options.isAllSelected || options.measureTemperature
            viewModel.healthData.value?.let { healthData ->
                updateHealthUI(healthData)
            }
        }
        viewModel.healthData.observe(viewLifecycleOwner) { healthData ->
            showLoading(false)
        }



        viewModel.alertMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                triggerAlert(it)
                viewModel.resetAlert()
            }
        }

        binding.btnSaveData.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val options = viewModel.measureOptions.value
            if (options == null || (!options.measureHeartRate && !options.measureSpO2 && !options.measureTemperature)) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một mục để lưu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập tên người đo", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val currentTime =
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            viewModel.saveCurrentHealthData(name, currentTime) { success, message ->
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

    private fun updateHealthUI(healthData: HealthData) {
        val options = viewModel.measureOptions.value ?: return
        val showHeartRate = options.isAllSelected || options.measureHeartRate
        val showSpO2 = options.isAllSelected || options.measureSpO2
        val showTemp = options.isAllSelected || options.measureTemperature

        binding.tvHeartRate.text = if (showHeartRate) "Nhịp tim: ${healthData.heartRate}" else ""
        binding.tvSpO2.text = if (showSpO2) "Nồng độ Oxy: ${healthData.spo2}" else ""
        binding.tvTemperature.text = if (showTemp) "Nhiệt độ: ${healthData.temperature}" else ""

        viewModel.checkHealthData(
            heartRate = if (showHeartRate) healthData.heartRate else null,
            spo2 = if (showSpO2) healthData.spo2 else null,
            temperature = if (showTemp) healthData.temperature else null
        )

        Log.d("HealthDataFragment", "Health Data received: $healthData")
    }

}
