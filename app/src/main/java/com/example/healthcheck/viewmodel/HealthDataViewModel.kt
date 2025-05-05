package com.example.healthcheck.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthcheck.model.HealthData
import com.example.healthcheck.model.HealthMeasureOptions
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

    val measureOptions = MutableLiveData(HealthMeasureOptions())


    fun startRealtimeHealthDataListener() {
        firebaseRepository.listenHealthData { data ->
            Log.d("HealthDataViewModel", "Received from repo: $data")
            currentHealthData = data
            _healthData.postValue(data)
        }
    }

    /*fun saveCurrentHealthData(name: String, time: String,onResult: (Boolean, String?) -> Unit) {
        currentHealthData?.let {data ->
            firebaseRepository.saveHealthDataHistory(data, name, time, onResult)
        } ?: onResult(false, "Không có dữ liệu hiện tại")
    }*/
    fun saveCurrentHealthData(name: String, time: String, onResult: (Boolean, String?) -> Unit) {
        val options = measureOptions.value ?: HealthMeasureOptions()

        currentHealthData?.let { data ->
            val dataToSave = data.copy(
                name = name,
                time = System.currentTimeMillis().toString(), // bạn có thể định dạng nếu cần
                measureHeartRate = options.measureHeartRate,
                measureSpO2 = options.measureSpO2,
                measureTemperature = options.measureTemperature
            )

            firebaseRepository.saveHealthDataHistory(dataToSave, name, time, onResult)
        } ?: onResult(false, "Không có dữ liệu hiện tại")
    }


    private var lastAlertMessage: String? = null
    private var lastAlertedData: Triple<Int, Int, Double>? = null

    /*fun checkHealthData(heartRate: Int?, spo2: Int?, temperature: Double?) {
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
    }*/


    fun checkHealthData(heartRate: Int?, spo2: Int?, temperature: Double?) {
        if (heartRate == null && spo2 == null && temperature == null) return

        // Tạo list chứa thông báo bất thường
        val alertMessages = mutableListOf<String>()

        heartRate?.let {
            if (it != 0 && (it < 50 || it > 120)) {
                alertMessages.add("Nhịp tim bất thường: $it bpm")
            }
        }

        spo2?.let {
            if (it < 90 && it !=0) {
                alertMessages.add("Nồng độ oxy thấp: $it%")
            }
        }

        temperature?.let {
            if (it != 0.0 && (it < 30.0 || it > 38.0)) {
                alertMessages.add("Nhiệt độ bất thường: $it°C")
            }
        }

        val message = if (alertMessages.isNotEmpty()) alertMessages.joinToString("\n") else null
        val currentData = Triple(heartRate ?: -1, spo2 ?: -1, temperature ?: -1.0)

        // Chỉ gửi alert nếu dữ liệu hoặc thông điệp cảnh báo thay đổi
        if (message != null && (currentData != lastAlertedData || message != lastAlertMessage)) {
            _alertMessage.value = message
            lastAlertMessage = message
            lastAlertedData = currentData
        }
    }



    fun updateMeasureOption(option: String, checked: Boolean) {
        val current = measureOptions.value ?: HealthMeasureOptions()

        val updated = when (option) {
            "all" -> HealthMeasureOptions(
                measureHeartRate = checked,
                measureSpO2 = checked,
                measureTemperature = checked
            )
            "heartRate" -> current.copy(measureHeartRate = checked)
            "spo2" -> current.copy(measureSpO2 = checked)
            "temperature" -> current.copy(measureTemperature = checked)
            else -> current
        }

        val allSelected = updated.measureHeartRate && updated.measureSpO2 && updated.measureTemperature
        isAllSelected.value = allSelected
        measureOptions.value = updated
    }


    val isAllSelected = MutableLiveData(false)




    fun resetAlert() {
        _alertMessage.value = null
    }
}
