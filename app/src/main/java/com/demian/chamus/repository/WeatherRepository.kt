package com.demian.chamus.repository

import com.demian.chamus.BuildConfig
import com.demian.chamus.models.WeatherResponse
import com.demian.chamus.api.WeatherRetrofitClient
import java.io.IOException

class WeatherRepository {
    private val API_KEY = BuildConfig.WEATHER_API_KEY

    suspend fun getCurrentWeather(location: String): WeatherResponse? {
        return try {
            val response = WeatherRetrofitClient.weatherApiService.getCurrentWeather(API_KEY, location)

            if (response.isSuccessful) {
                response.body()
            } else {
                throw IOException("Error de API: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            throw IOException("Error de red: ${e.message}")
        }
    }
}