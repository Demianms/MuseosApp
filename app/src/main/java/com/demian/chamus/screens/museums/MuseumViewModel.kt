package com.demian.chamus.screens.museums

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.demian.chamus.api.RetrofitClient
import com.demian.chamus.models.Museum // El modelo Museum actualizado

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class MuseumViewModel : ViewModel() {

    // LiveData para la lista de museos. La UI observará esto.
    private val _museums = MutableLiveData<List<Museum>>()
    val museums: LiveData<List<Museum>> = _museums

    // LiveData para el mensaje de error. La UI observará esto.
    private val _errorMessage = MutableLiveData<String?>() // Puede ser nulo si no hay error
    val errorMessage: LiveData<String?> = _errorMessage

    // LiveData para el estado de carga. La UI observará esto.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        // Puedes cargar los museos automáticamente cuando el ViewModel se crea
        fetchMuseums()
    }

    private fun fetchMuseums() {
        // Si ya estamos cargando, no intentamos cargar de nuevo
        if (_isLoading.value == true) return

        _isLoading.value = true // Indicar que estamos cargando
        _errorMessage.value = null // Limpiar cualquier mensaje de error anterior

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getMuseums()

                if (response.isSuccessful) {
                    val museumsList = response.body()
                    _museums.value = museumsList ?: emptyList() // Si es nulo, poner lista vacía
                    println("¡La lista de museos llegó! Cantidad: ${museumsList?.size}")
                    // Opcional: imprimir el número de salas del primer museo si existe
                    museumsList?.firstOrNull()?.rooms?.let {
                        println("Primer museo tiene ${it.size} salas.")
                    }
                } else {
                    val errorCode = response.code()
                    val errorMessage = response.message()
                    _errorMessage.value = "Error al obtener museos: $errorCode - ${response.errorBody()?.string() ?: errorMessage}"
                    _museums.value = emptyList() // Limpiar la lista en caso de error
                    println("Error al obtener museos: $errorCode - ${response.errorBody()?.string() ?: errorMessage}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error de red: ${e.localizedMessage ?: "Desconocido"}"
                _museums.value = emptyList() // Limpiar la lista en caso de error de red
                println("Error de red: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false // Indicar que la carga ha terminado (éxito o error)
            }
        }
    }
}