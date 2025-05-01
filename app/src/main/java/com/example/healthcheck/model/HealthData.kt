package com.example.healthcheck.model

import com.google.firebase.database.PropertyName

data class HealthData(
    @get:PropertyName("nhip_tim")
    @set:PropertyName("nhip_tim")
    var heartRate: Int = 0,

    @get:PropertyName("spo2")
    @set:PropertyName("spo2")
    var spo2: Int = 0,

    @get:PropertyName("nhiet_do")
    @set:PropertyName("nhiet_do")
    var temperature: Double = 0.0,

    var timestamp: Long = 0
)

