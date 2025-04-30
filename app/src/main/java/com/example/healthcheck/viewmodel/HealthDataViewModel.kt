package com.example.healthcheck.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.healthcheck.model.HealthData
import androidx.lifecycle.ViewModel
import com.example.healthcheck.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HealthDataViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var healthDataListener: ValueEventListener? = null
    private var currentHealthData: HealthData? = null
    private val firebaseRepository = FirebaseRepository()
    private val _alertMessage = MutableLiveData<String?>()
    val alertMessage: LiveData<String?> get() = _alertMessage

    fun getRealtimeHealthData(onDataChanged: (HealthData) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val userRef = database.getReference("healthData").child(userId)

        healthDataListener?.let { userRef.removeEventListener(it) } // Xóa listener cũ nếu có

        healthDataListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.getValue(HealthData::class.java)?.let { healthData ->
                    currentHealthData = healthData
                    onDataChanged(healthData)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Xử lý lỗi nếu muốn
            }

        }
        userRef.addValueEventListener(healthDataListener!!)
    }
    override fun onCleared() {
        super.onCleared()
        // Ngừng nghe khi ViewModel bị huỷ
        healthDataListener?.let {
            val userId = auth.currentUser?.uid ?: return
            database.getReference("healthData").child(userId).removeEventListener(it)
        }
    }
    fun saveCurrentHealthData(onResult: (Boolean, String?) -> Unit) {
        currentHealthData?.let {
            firebaseRepository.saveHealthDataHistory(it, onResult)
        } ?: onResult(false, "Không có dữ liệu hiện tại")
    }

    fun checkHealthData(heartRate: Int, spo2: Int, temperature: Double) {
        when {
            heartRate < 50 || heartRate > 120 -> {
                _alertMessage.value = "Nhịp tim bất thường: $heartRate bpm"
            }
            spo2 < 90 -> {
                _alertMessage.value = "Nồng độ oxy thấp: $spo2%"
            }
            temperature > 38.0f || temperature < 35.0f -> {
                _alertMessage.value = "Nhiệt độ cơ thể bất thường: $temperature°C"
            }
            else -> {
                _alertMessage.value = null // bình thường, không cảnh báo
            }
        }
    }

    fun resetAlert() {
        _alertMessage.value = null
    }


}
