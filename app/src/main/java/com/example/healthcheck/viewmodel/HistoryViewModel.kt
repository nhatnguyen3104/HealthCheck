package com.example.healthcheck.viewmodel

import androidx.lifecycle.ViewModel
import com.example.healthcheck.model.HealthData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var historyListener: ValueEventListener? = null

    fun startListeningHistory(onHistoryChanged: (List<HealthData>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val historyRef = database.getReference("history").child(userId)

        historyListener?.let { historyRef.removeEventListener(it) }

        historyListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val historyList = mutableListOf<HealthData>()
                for (childSnapshot in snapshot.children) {
                    childSnapshot.getValue(HealthData::class.java)?.let {
                        historyList.add(it)
                    }
                }
                onHistoryChanged(historyList.reversed()) // Muốn mới nhất trước thì reversed
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO: Xử lý lỗi nếu muốn
            }
        }

        historyRef.addValueEventListener(historyListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        historyListener?.let {
            val userId = auth.currentUser?.uid ?: return
            database.getReference("history").child(userId).removeEventListener(it)
        }
    }
}

