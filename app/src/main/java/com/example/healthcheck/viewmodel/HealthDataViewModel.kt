package com.example.healthcheck.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthcheck.model.HealthData
import com.example.healthcheck.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HealthDataViewModel : ViewModel() {

    private val firebaseRepository = FirebaseRepository()
    private var currentHealthData: HealthData? = null

    private val _healthData = MutableLiveData<HealthData?>()
    val healthData: LiveData<HealthData> get() = _healthData as LiveData<HealthData>

    private val _alertMessage = MutableLiveData<String?>()
    val alertMessage: LiveData<String?> get() = _alertMessage

    fun startRealtimeHealthDataListener() {
        firebaseRepository.listenHealthData { data ->
            Log.d("HealthDataViewModel", "Received from repo: $data")
            currentHealthData = data
            _healthData.postValue(data)
        }
    }

    fun saveCurrentHealthData(onResult: (Boolean, String?) -> Unit) {
        currentHealthData?.let {
            firebaseRepository.saveHealthDataHistory(it, onResult)
        } ?: onResult(false, "Không có dữ liệu hiện tại")
    }

    private var lastAlertMessage: String? = null
    private var lastAlertedData: Triple<Int, Int, Double>? = null

    fun checkHealthData(heartRate: Int?, spo2: Int?, temperature: Double?) {
        if (heartRate == null || spo2 == null || temperature == null) return

        val currentData = Triple(heartRate, spo2, temperature)

        val message = when {
            heartRate < 50 || heartRate > 120 -> "Nhịp tim bất thường: $heartRate bpm"
            spo2 < 90 -> "Nồng độ oxy thấp: $spo2%"
            temperature > 38.0 || temperature < 35.0 -> "Nhiệt độ cơ thể bất thường: $temperature°C"
            else -> null
        }

        // Chỉ gửi alert nếu dữ liệu hoặc thông điệp cảnh báo thay đổi
        if (message != null && (currentData != lastAlertedData || message != lastAlertMessage)) {
            _alertMessage.value = message
            lastAlertMessage = message
            lastAlertedData = currentData
        }
    }


    fun resetAlert() {
        _alertMessage.value = null
    }
}
