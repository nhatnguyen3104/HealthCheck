package com.example.healthcheck.ui.history

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcheck.databinding.FragmentHistoryBinding
import com.example.healthcheck.model.HealthData
import com.example.healthcheck.repository.FirebaseRepository
import com.example.healthcheck.viewmodel.HistoryViewModel

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HistoryViewModel
    private val adapter = HistoryAdapter { healthData ->
        showDeleteConfirmation(healthData)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = HistoryViewModel()
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter

        showLoading(true)

        viewModel.startListeningHistory()

        viewModel.historyList.observe(viewLifecycleOwner) { historyList ->
            showLoading(false)
            adapter.submitList(historyList)

            binding.tvEmpty.visibility = if (historyList.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
    private fun showDeleteConfirmation(data: HealthData) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa bản ghi?")
            .setMessage("Bạn có chắc muốn xóa bản ghi sức khỏe này?")
            .setPositiveButton("Xóa") { _, _ ->
                viewModel.deleteHealthData(data) { success, error ->
                    if (success) {
                        Toast.makeText(requireContext(), "Đã xóa bản ghi", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Xóa thất bại: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }


}
