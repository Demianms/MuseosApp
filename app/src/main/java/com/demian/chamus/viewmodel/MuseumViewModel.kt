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

    private val _filter = MutableStateFlow("Todos")
    val filter: StateFlow<String> = _filter

    private var allMuseums: List<Museum> = emptyList()

    init {
        fetchMuseums()
    }

    private fun fetchMuseums() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val museums = repository.getMuseums()
                allMuseums = museums
                applyFilter()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error al cargar los museos"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setFilter(filter: String) {
        _filter.value = filter
        applyFilter()
    }

    private fun applyFilter() {
        _museums.value = when (_filter.value) {
            "Todos" -> allMuseums
            else -> allMuseums.filter {
                it.name.contains(_filter.value, ignoreCase = true) ||
                        it.descripcion.contains(_filter.value, ignoreCase = true)
            }
        }
    }
}