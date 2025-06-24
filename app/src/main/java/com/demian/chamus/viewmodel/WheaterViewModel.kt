package com.demian.chamus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demian.chamus.models.WeatherResponse
import com.demian.chamus.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    // Estados
    private val _weather = MutableStateFlow<WeatherResponse?>(null)
    val weather: StateFlow<WeatherResponse?> = _weather

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchWeather(location: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val weatherData = repository.getCurrentWeather(location)
                if (weatherData != null) {
                    _weather.value = weatherData
                } else {
                    _error.value = "No se encontraron datos para esta ubicación"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message ?: "Error desconocido"}"
            } finally {
                _loading.value = false
            }
        }
    }
}