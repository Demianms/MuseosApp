package com.demian.chamus.api

import com.demian.chamus.models.Category
import com.demian.chamus.models.CotizacionGrupalRequest
import com.demian.chamus.models.CotizacionGrupalResponse
import com.demian.chamus.models.Museum
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MuseumApiService {
    @GET("api/museums")
    suspend fun getMuseums(): List<Museum>

    @GET("api/museums/{id}")
    suspend fun getMuseumById(@Path("id") museumId: Int): Museum

    @GET("api/categories")
    suspend fun getCategories() : List<Category>

    @POST("api/cotizaciones")
    suspend fun createCotizacion(@Body request: CotizacionGrupalRequest): Response<CotizacionGrupalResponse>

    @GET("api/cotizaciones/{unique_id}/")
    suspend fun getQuotationByUniqueId(@Path("unique_id") uniqueId: String): Response<CotizacionGrupalResponse>
}