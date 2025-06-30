package com.demian.chamus.repository

import Museum
import com.demian.chamus.api.RetrofitClient

class MuseumRepository {
    suspend fun getMuseums(): List<Museum> {
        return RetrofitClient.api.getMuseums()
    }

    suspend fun getMuseumById(id: Int): Museum {
        return RetrofitClient.api.getMuseumById(id)
    }
}