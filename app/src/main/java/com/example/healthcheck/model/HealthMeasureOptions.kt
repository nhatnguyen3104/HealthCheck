package com.example.healthcheck.model

data class HealthMeasureOptions(
    var measureHeartRate: Boolean = false,
    var measureSpO2: Boolean = false,
    var measureTemperature: Boolean = false,
    val isAllSelected: Boolean = false
)

