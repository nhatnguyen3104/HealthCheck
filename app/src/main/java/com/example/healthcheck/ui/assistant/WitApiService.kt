package com.example.healthcheck.ui.assistant

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
//import com.example.healthcheck.

interface WitApiService {
    @Headers(
        "Authorization: Bearer xxxx",
        "Content-Type: application/json"
    )
    @GET("message")
    suspend fun getWitResponse(
        @Query("q") query: String
    ): WitResponse
}

//interface WitApiService {
//    @GET("message")
//    suspend fun getWitResponse(
//        @Query("q") query: String,
//        @Header("Authorization") authHeader: String = BuildConfig.WIT_API_KEY
//    ): WitResponse
//}


