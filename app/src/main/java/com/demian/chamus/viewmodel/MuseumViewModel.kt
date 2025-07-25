package com.demian.chamus.viewmodel

import com.demian.chamus.models.Museum
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.demian.chamus.repository.MuseumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class MuseoConCategoria(
    val museum: Museum,
    val categoria: Categoria
)

class MuseumViewModel : ViewModel() {
    private val repository = MuseumRepository()

    private val _museums = MutableStateFlow<List<Museum>>(emptyList())
    val museums: StateFlow<List<Museum>> = _museums

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _filter = MutableStateFlow("Todos")
    val filter: StateFlow<String> = _filter

    private var allMuseosCategorizados: List<MuseoConCategoria> = emptyList()

    private val _museosFiltrados = MutableStateFlow<List<MuseoConCategoria>>(emptyList())
    val museosFiltrados: StateFlow<List<MuseoConCategoria>> = _museosFiltrados

    init {
        loadMuseums()
    }

    private fun loadMuseums() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val museums = repository.getMuseums()
                _museums.value = museums

                // Categorizar cada museo
                allMuseosCategorizados = museums.map {
                    MuseoConCategoria(it, categorizarMuseo(it))
                }

                applyFilter() // aplicar filtro actual

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar los museos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private val _selectedMuseum = MutableStateFlow<Museum?>(null)
    val selectedMuseum: StateFlow<Museum?> = _selectedMuseum.asStateFlow()

    fun loadMuseumDetails(museumId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _selectedMuseum.value = null

            try {
                val museum = repository.getMuseumById(museumId)

                println("Museo cargado: $museum")
                _selectedMuseum.value = museum
            } catch (e: Exception) {
                _error.value = "Error al cargar detalles del museo (ID: $museumId): ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: String) {
        _filter.value = filter
        applyFilter() // aplicar filtrado en tiempo real
    }

    // Nueva función: aplica el filtro
    private fun applyFilter() {
        _museosFiltrados.value = when (_filter.value) {
            "Arte" -> allMuseosCategorizados.filter { it.categoria == Categoria.Arte }
            "Historia" -> allMuseosCategorizados.filter { it.categoria == Categoria.Historia }
            "Cultura" -> allMuseosCategorizados.filter { it.categoria == Categoria.Cultura }
            else -> allMuseosCategorizados
        }
    }

    // Función auxiliar para categorizar los museos por nombre
    private fun categorizarMuseo(museo: Museum): Categoria {
        return when {
            museo.nombre.contains("Arte", ignoreCase = true) -> Categoria.Arte
            museo.nombre.contains("Antropología", ignoreCase = true) -> Categoria.Historia
            museo.nombre.contains("Cultura", ignoreCase = true) -> Categoria.Cultura
            else -> Categoria.Cultura
        }
    }



}
