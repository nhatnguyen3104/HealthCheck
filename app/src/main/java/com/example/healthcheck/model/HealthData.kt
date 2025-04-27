package com.example.healthcheck.model

data class HealthData(
    val heartRate: Int = 0,
    val spo2: Int = 0,
    val temperature: Double = 0.0
)
