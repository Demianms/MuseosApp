package com.demian.chamus.api

import com.demian.chamus.models.Museum
import retrofit2.http.GET

interface MuseumApiService {
    @GET("api/museums")
    suspend fun getMuseums(): List<Museum>
}