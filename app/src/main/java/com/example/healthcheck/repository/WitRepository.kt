package com.example.healthcheck.repository

import com.example.healthcheck.view.WitApiService
import com.example.healthcheck.view.WitResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WitRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://api.wit.ai/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(WitApiService::class.java)

    suspend fun askWit(query: String): WitResponse {
        return api.getWitResponse(query)
    }
}
