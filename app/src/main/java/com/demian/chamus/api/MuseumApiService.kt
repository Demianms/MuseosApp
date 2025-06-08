package com.demian.chamus.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import com.demian.chamus.models.Museum

interface MuseumApiService {
    @GET("api/museums") // Reemplaza con la ruta real de tu API de Laravel
    suspend fun getMuseums(): Response<List<Museum>>

    @GET("api/museums/{id}")
    suspend fun getMuseumById(@Path("id") museumId: Int): Response<Museum>

    // Añade otros métodos para POST, PUT, DELETE si los necesitas
    // @POST("api/museums")
    // suspend fun createMuseum(@Body museum: Museum): Response<Museum>
}