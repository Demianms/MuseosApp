package com.demian.chamus.repository

import android.util.Log
import com.demian.chamus.api.RetrofitClient // Asegúrate de que esta importación sea correcta para tu RetrofitClient
import com.demian.chamus.models.Category
import com.demian.chamus.models.CotizacionGrupalRequest
import com.demian.chamus.models.CotizacionGrupalResponse
import com.demian.chamus.models.Museum
import retrofit2.Response // Importa Response de Retrofit

class MuseumRepository {
    // Asume que RetrofitClient.api es una instancia de tu ApiService
    // Si tu constructor de MuseumRepository toma ApiService, ajústalo aquí:
    // private val apiService: ApiService = RetrofitClient.api

    suspend fun getMuseums(): List<Museum> {
        return RetrofitClient.api.getMuseums()
    }

    suspend fun getMuseumById(id: Int): Museum {
        return RetrofitClient.api.getMuseumById(id)
    }

    suspend fun getCategories(): List<Category> {
        return try {
            val categories = RetrofitClient.api.getCategories()
            Log.d("MuseumRepository", "Categorías obtenidas de API: ${categories.size}")
            categories
        } catch (e: Exception) {
            Log.e("MuseumRepository", "Error al obtener categorías", e)
            emptyList()
        }
    }

    // --- CAMBIO CRUCIAL AQUÍ ---
    // Ahora devuelve Response<CotizacionGrupalResponse> para que el ViewModel maneje el éxito/error
    suspend fun createCotizacion(request: CotizacionGrupalRequest): Response<CotizacionGrupalResponse> {
        return RetrofitClient.api.createCotizacion(request)
    }

    // Este ya estaba correcto
    suspend fun getQuotationById(uniqueId: String): Response<CotizacionGrupalResponse> {
        return RetrofitClient.api.getQuotationByUniqueId(uniqueId)
    }
}