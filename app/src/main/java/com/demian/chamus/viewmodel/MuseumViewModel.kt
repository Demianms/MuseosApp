package com.demian.chamus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demian.chamus.models.Museum
import com.demian.chamus.repository.MuseumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MuseumViewModel : ViewModel() {
    private val repository = MuseumRepository()

    private val _museums = MutableStateFlow<List<Museum>>(emptyList())
    val museums: StateFlow<List<Museum>> = _museums

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // El filtro se mantiene, pero no se utiliza activamente para filtrar la lista mostrada.
    // Esto es si deseas conservar el estado del botón seleccionado visualmente,
    // pero sin aplicar un filtrado real en los datos.
    private val _filter = MutableStateFlow("Todos")
    val filter: StateFlow<String> = _filter

    // allMuseums seguirá almacenando todos los museos obtenidos del repositorio
    private var allMuseums: List<Museum> = emptyList()

    init {
        // Carga los museos al iniciar el ViewModel
        loadMuseums()
    }

    // Función pública para cargar/recargar los museos.
    // Usada por el pull-to-refresh.
    fun loadMuseums() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Obtiene todos los museos del repositorio
                val museums = repository.getMuseums()
                allMuseums = museums
                // Directamente asigna todos los museos a _museums.value
                // ya que no hay filtro aplicado en este caso
                _museums.value = allMuseums
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar los museos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Esta función setFilter ahora solo actualizará el estado del filtro si lo necesitas
    // para propósitos visuales (ej. cambiar el color del botón seleccionado),
    // pero no desencadenará un filtrado de la lista de museos.
    fun setFilter(filter: String) {
        _filter.value = filter
        // No llamamos a applyFilter aquí, ya que no queremos filtrar la lista.
        // Si necesitas que los museos se recarguen de nuevo al cambiar el filtro,
        // tendrías que llamar a loadMuseums(), pero eso no sería "sin la función de las categorías"
        // sino una recarga basada en un nuevo criterio.
    }

    // La función applyFilter ya no es necesaria si no hay lógica de filtrado activa.
    // Si la mantienes, podría ser una función vacía o simplemente no usarse.
    // Para mayor claridad, la quitaría si no va a hacer nada.
    // private fun applyFilter() {
    //    // Si _museums.value siempre debe ser allMuseums, esta función es redundante
    // }
}