package com.demian.chamus.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demian.chamus.models.Category
import com.demian.chamus.models.Museum
import com.demian.chamus.repository.MuseumRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MuseumViewModel : ViewModel() {
    private val repository = MuseumRepository()

    // museos
    private val _museums = MutableStateFlow<List<Museum>>(emptyList())
    val museums: StateFlow<List<Museum>> = _museums.asStateFlow()

    // categorías
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Filtro
    private val _currentFilter = MutableStateFlow<Category?>(null)
    val currentFilter: StateFlow<Category?> = _currentFilter.asStateFlow()

    // Museos filtrados
    val filteredMuseums: StateFlow<List<Museum>> =
        combine(_museums, _currentFilter) { allMuseums, currentFilter ->
            if (currentFilter == null) {
                allMuseums
            } else {
                allMuseums.filter { museum ->
                    museum.categories.any { category ->
                        category.id == currentFilter.id
                    }
                }
            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        if (_categories.value.isNotEmpty()) return // Evita recargas innecesarias

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Ejecuta en paralelo realmente
                val museumsJob = async { loadMuseums() }
                val categoriesJob = async { loadCategories() }

                // Espera ambos
                museumsJob.await()
                categoriesJob.await()

                Log.d("MuseumViewModel", "Datos iniciales cargados")
                resetFilter()
            } catch (e: Exception) {
                _error.value = "Error: ${e.message ?: "Desconocido"}"
                Log.e("MuseumViewModel", "Error en loadInitialData", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadMuseums() {
        try {
            val fetchedMuseums = repository.getMuseums()
            _museums.value = fetchedMuseums
            Log.d("MuseumViewModel", "Museos cargados: ${fetchedMuseums.size}")
        } catch (e: Exception) {
            _error.value = "Error al cargar museos: ${e.message}"
            throw e
        }
    }

    private suspend fun loadCategories() {
        try {
            val fetchedCategories = repository.getCategories()
            _categories.value = fetchedCategories
            Log.d("MuseumViewModel", "Categorías cargadas: ${fetchedCategories.size}")
            fetchedCategories.forEach {
                Log.d("MuseumViewModel", "Categoría: ${it.nombre} (ID: ${it.id})")
            }
        } catch (e: Exception) {
            _error.value = "Error al cargar categorías: ${e.message}"
            Log.e("MuseumViewModel", "Error en loadCategories", e)
            throw e
        }
    }

    // Funciones para manejar el filtrado
    fun setFilter(category: Category) {
        _currentFilter.value = category
        Log.d("MuseumViewModel", "Filtro establecido a: ${category.nombre}")
    }

    fun resetFilter() {
        _currentFilter.value = null
        Log.d("MuseumViewModel", "Filtro reseteado (Todos)")
    }

    // Detalles del museo
    private val _selectedMuseum = MutableStateFlow<Museum?>(null)
    val selectedMuseum: StateFlow<Museum?> = _selectedMuseum.asStateFlow()

    fun loadMuseumDetails(museumId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _selectedMuseum.value = null

            try {
                val museum = repository.getMuseumById(museumId)
                _selectedMuseum.value = museum
            } catch (e: Exception) {
                _error.value = "Error al cargar detalles del museo (ID: $museumId): ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}