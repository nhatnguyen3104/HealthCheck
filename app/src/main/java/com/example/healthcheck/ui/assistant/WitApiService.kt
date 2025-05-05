package com.example.healthcheck.ui.assistant

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface WitApiService {
    @Headers(
        "Authorization: Bearer Q2SL6EBFXICBJPOTI722B66UUJG277NG",
        "Content-Type: application/json"
    )
    @GET("message")
    suspend fun getWitResponse(
        @Query("q") query: String
    ): WitResponse
}