package com.example.bookie

import retrofit2.http.GET
import retrofit2.http.Query

interface BookApiService {
    @GET("volumes?q=subject:fiction")
    suspend fun getBooks(
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String
    ): BookResponse
}

