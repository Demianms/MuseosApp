package com.demian.chamus.repository

import com.demian.chamus.models.Museum
import com.demian.chamus.api.RetrofitClient

class MuseumRepository {
    suspend fun getMuseums(): List<Museum> {
        return RetrofitClient.api.getMuseums()
    }
}