package com.example.healthcheck.repository

import android.util.Log
import com.example.healthcheck.model.HealthData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "FirebaseRepository"

    fun listenHealthData(onDataReceived: (HealthData) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("FirebaseRepository", "User ID is null. User not authenticated?")
            return
        }

        val ref = database.getReference("du_lieu_suc_khoe").child(userId)

        ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val data = snapshot.getValue(HealthData::class.java)
                Log.d("FirebaseRepository", "Health data received: $data")
                data?.let { onDataReceived(it) }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                Log.e("FirebaseRepository", "Failed to read data: ${error.message}")
            }
        })
    }

    fun saveHealthDataHistory(data: HealthData, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false, "User not authenticated")
            return
        }

        val historyRef = database.getReference("history").child(userId)
        val newRecord = historyRef.push()

        newRecord.setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseRepository", "Health data saved to history.")
                    onResult(true, null)
                } else {
                    Log.e("FirebaseRepository", "Save failed: ${task.exception?.message}")
                    onResult(false, task.exception?.message)
                }
            }
    }

    fun getHealthDataHistory(onResult: (List<HealthData>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val historyRef = database.getReference("history").child(userId)

        historyRef.get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<HealthData>()
            for (child in snapshot.children) {
                val data = child.getValue(HealthData::class.java)
                data?.let { list.add(it) }
            }
            Log.d("FirebaseRepository", "Fetched ${list.size} health records.")
            onResult(list)
        }.addOnFailureListener {
            Log.e("FirebaseRepository", "Failed to fetch history: ${it.message}")
        }
    }
    fun listenHealthDataHistory(onDataChanged: (List<HealthData>) -> Unit): ValueEventListener? {
        val userId = auth.currentUser?.uid ?: return null
        val historyRef = database.getReference("history").child(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<HealthData>()
                for (child in snapshot.children) {
                    val data = child.getValue(HealthData::class.java)
                    data?.let { list.add(it) }
                }
                onDataChanged(list.reversed()) // Mới nhất lên trước
            }

            override fun onCancelled(error: DatabaseError) {
                // Log hoặc xử lý nếu cần
            }
        }

        historyRef.addValueEventListener(listener)
        return listener
    }
//    fun addHealthHistoryListener(listener: ValueEventListener): ValueEventListener? {
//        val userId = auth.currentUser?.uid ?: return null
//        val historyRef = database.getReference("history").child(userId)
//        historyRef.addValueEventListener(listener)
//        return listener
//    }

//    fun removeHealthHistoryListener(listener: ValueEventListener) {
//        val userId = auth.currentUser?.uid ?: return
//        val historyRef = database.getReference("history").child(userId)
//        historyRef.removeEventListener(listener)
//    }

    fun addHealthHistoryListener(onDataChanged: (List<HealthData>) -> Unit): ValueEventListener? {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "addHealthHistoryListener: User not authenticated")
            return null
        }

        val historyRef = database.getReference("history").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<HealthData>()
                for (child in snapshot.children) {
                    val data = child.getValue(HealthData::class.java)
                    if (data != null) {
                        list.add(data)
                        Log.d(TAG, "Fetched item: $data")
                    } else {
                        Log.w(TAG, "Null data found in snapshot")
                    }
                }
                Log.d(TAG, "Total history fetched: ${list.size}")
                onDataChanged(list.reversed()) // Mới nhất lên đầu
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Listener cancelled: ${error.message}")
            }
        }

        historyRef.addValueEventListener(listener)
        Log.d(TAG, "History listener added")
        return listener
    }

    fun removeHealthHistoryListener(listener: ValueEventListener) {
        val userId = auth.currentUser?.uid ?: return
        val historyRef = database.getReference("history").child(userId)
        historyRef.removeEventListener(listener)
        Log.d(TAG, "History listener removed")
    }
}

