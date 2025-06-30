package com.demian.chamus.api

import Museum
import retrofit2.http.GET
import retrofit2.http.Path

interface MuseumApiService {
    @GET("api/museums")
    suspend fun getMuseums(): List<Museum>

    @GET("api/museums/{id}")
    suspend fun getMuseumById(@Path("id") museumId: Int): Museum
}