package com.example.healthcheck.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.healthcheck.model.HealthData
import com.example.healthcheck.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryViewModel : ViewModel() {

    private val repository = FirebaseRepository()

    private val _historyList = MutableLiveData<List<HealthData>>()
    val historyList: LiveData<List<HealthData>> get() = _historyList

    private val _historyLiveData = MutableLiveData<List<HealthData>>()
    val historyLiveData: LiveData<List<HealthData>> get() = _historyLiveData

    private var historyListener: ValueEventListener? = null

    private val _healthHistory = MutableLiveData<List<HealthData>>()
    val healthHistory: LiveData<List<HealthData>> get() = _healthHistory

    fun startListeningHistory() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            repository.listenHealthHistory(uid) { list ->
                _historyList.postValue(list)
                Log.d("HistoryViewModel", "History updated: ${list.size} items")
            }
        } else {
            _historyList.postValue(emptyList())
        }
    }



    override fun onCleared() {
        super.onCleared()
        historyListener?.let {
            repository.removeHealthHistoryListener(it)
            Log.d("HistoryViewModel", "Listener cleared in ViewModel")
        }
    }
    fun fetchHealthHistory(uid: String) {
        repository.listenHealthHistory(uid) { historyList ->
            _healthHistory.postValue(historyList)
        }
    }
    fun deleteHealthData(data: HealthData, onResult: (Boolean, String?) -> Unit) {
        data.key?.let {
            repository.deleteHealthDataByKey(it, onResult)
        } ?: onResult(false, "Không có khóa dữ liệu")
    }


}
