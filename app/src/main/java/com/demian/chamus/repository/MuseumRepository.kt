package com.demian.chamus.repository

import android.util.Log
import com.demian.chamus.models.Museum
import com.demian.chamus.api.RetrofitClient
import com.demian.chamus.models.Category

class MuseumRepository {
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
            emptyList() // Retorna lista vacía para evitar crash
        }
    }
}