package com.example.healthcheck.view

data class WitResponse(
    val text: String,
    val intents: List<Intent>?,
    val entities: Map<String, List<Entity>>?
)

data class Intent(val id: String, val name: String, val confidence: Double)
data class Entity(val confidence: Double, val value: String)


