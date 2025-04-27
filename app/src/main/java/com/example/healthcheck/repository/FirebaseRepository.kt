package com.example.healthcheck.repository

import com.example.healthcheck.model.HealthData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseRepository {

    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun listenHealthData(onDataReceived: (HealthData) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val ref = database.getReference("realtime_data").child(userId)

        ref.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val data = snapshot.getValue(HealthData::class.java)
                data?.let { onDataReceived(it) }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }

    fun saveHealthDataHistory(data: HealthData, onResult: (Boolean, String?) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val historyRef = database.getReference("history").child(userId)

        val newRecord = historyRef.push()
        newRecord.setValue(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true, null)
                } else {
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
            onResult(list)
        }
    }

}
