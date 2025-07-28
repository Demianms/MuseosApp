package com.demian.chamus.viewmodel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demian.chamus.models.CotizacionGrupalRequest
import com.demian.chamus.models.CotizacionGrupalResponse
import com.demian.chamus.models.Discount
import com.demian.chamus.models.DiscountedPeopleGroup
import com.demian.chamus.models.Museum
import com.demian.chamus.repository.MuseumRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.math.roundToInt

class QuotationViewModel(private val repository: MuseumRepository = MuseumRepository()) : ViewModel() {

    // --- Estados de la UI expuestos como StateFlow ---
    private val _museums = MutableStateFlow<List<Museum>>(emptyList())
    val museums: StateFlow<List<Museum>> = _museums.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _quotationResponse = MutableStateFlow<CotizacionGrupalResponse?>(null)
    val quotationResponse: StateFlow<CotizacionGrupalResponse?> = _quotationResponse.asStateFlow()

    private val _selectedMuseum = MutableStateFlow<Museum?>(null)
    val selectedMuseum: StateFlow<Museum?> = _selectedMuseum.asStateFlow()

    // --- NUEVO: Indicador para saber si la cotización en _quotationResponse es resultado de una BÚSQUEDA ---
    private val _wasQuotationSearched = MutableStateFlow(false)
    val wasQuotationSearched: StateFlow<Boolean> = _wasQuotationSearched.asStateFlow()

    // --- Datos de entrada de la cotización (pueden seguir siendo mutableStateOf o var para simplificar la entrada de usuario) ---
    var selectedMuseumId: Int? by mutableStateOf(null)
    var appointmentDate: String by mutableStateOf("")
    var startHour: String by mutableStateOf("")
    var endHour: String by mutableStateOf("")

    val discountedPeopleGroups = androidx.compose.runtime.mutableStateListOf<DiscountedPeopleGroup>()

    var totalPeopleGeneral: String by mutableStateOf("0")
    var totalInfants: String by mutableStateOf("0")

    private val _availableDiscounts = MutableStateFlow<List<Discount>>(emptyList())
    val availableDiscounts: StateFlow<List<Discount>> = _availableDiscounts.asStateFlow()

    var _museumBasePrice: Double by mutableDoubleStateOf(0.0)

    // --- Cálculos derivados (optimizados para Compose) ---
    val calculatedTotalPeople: Int by derivedStateOf {
        var total = (totalPeopleGeneral.toIntOrNull() ?: 0) +
                (totalInfants.toIntOrNull() ?: 0)
        discountedPeopleGroups.forEach { group ->
            total += (group.count.toIntOrNull() ?: 0)
        }
        total
    }

    val calculatedPriceTotal: Double by derivedStateOf {
        val pricePerPerson = _museumBasePrice
        var totalAmount = 0.0

        val generalCount = totalPeopleGeneral.toIntOrNull() ?: 0

        totalAmount += (generalCount * pricePerPerson).roundToTwoDecimals()

        discountedPeopleGroups.forEach { group ->
            val count = group.count.toIntOrNull() ?: 0
            val discountFactor = group.discount?.valorDescuento?.toDoubleOrNull() ?: 0.0
            totalAmount += (count * pricePerPerson * (1.0 - discountFactor)).roundToTwoDecimals()
        }

        Log.d("CalculoTotal", "--- Iniciando cálculo total con grupos de descuento ---")
        Log.d("CalculoTotal", "Precio Base por Persona: $pricePerPerson")
        Log.d("CalculoTotal", "Personas Generales: $generalCount -> ${ (generalCount * pricePerPerson).roundToTwoDecimals()}")
        discountedPeopleGroups.forEachIndexed { index, group ->
            val groupCount = group.count.toIntOrNull() ?: 0
            val groupDiscountValue = group.discount?.valorDescuento?.toDoubleOrNull() ?: 0.0
            val groupPrice = (groupCount * pricePerPerson * (1.0 - groupDiscountValue)).roundToTwoDecimals()
            Log.d("CalculoTotal", "Grupo ${index + 1}: $groupCount personas con ${group.discount?.descripcionAplicacion} (${groupDiscountValue * 100}%) -> $groupPrice")
        }
        Log.d("CalculoTotal", "Resultado final (calculatedPriceTotal): $totalAmount")
        Log.d("CalculoTotal", "------------------------------------------")

        totalAmount
    }

    init {
        fetchMuseumsForSelection() // Carga todos los museos
    }

    // --- Métodos públicos para permitir que la UI solicite cambios de estado ---
    private fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    private fun setQuotationResponse(response: CotizacionGrupalResponse?) {
        _quotationResponse.value = response
    }

    // --- NUEVO MÉTODO: Para establecer el indicador de búsqueda ---
    fun setQuotationWasSearched(isSearched: Boolean) {
        _wasQuotationSearched.value = isSearched
        Log.d("QuotationViewModel", "wasQuotationSearched set to: $isSearched")
    }

    // --- Métodos para obtener datos ---
    private fun fetchMuseumsForSelection() {
        viewModelScope.launch {
            setLoading(true)
            setErrorMessage(null)
            try {
                val fetchedMuseums = repository.getMuseums()
                _museums.value = fetchedMuseums // Actualiza la lista de museos
                Log.d("QuotationViewModel", "Museos cargados: ${fetchedMuseums.size}")

                // Si hay un initialMuseumId pendiente, configúralo ahora que los museos están cargados
                if (selectedMuseumId != null && _selectedMuseum.value == null) {
                    fetchedMuseums.find { it.id == selectedMuseumId }?.let { museum ->
                        _selectedMuseum.value = museum
                        _museumBasePrice = museum.precio.toDoubleOrNull() ?: 0.0
                        _availableDiscounts.value = museum.descuentosAsociados.orEmpty()
                        setupInitialDiscountGroup(museum.descuentosAsociados.orEmpty())
                        Log.d("QuotationViewModel", "Museo pre-seleccionado encontrado y configurado: ${museum.nombre}")
                    }
                }
            } catch (e: IOException) {
                setErrorMessage("Error de red al cargar museos. Por favor, revisa tu conexión.")
                Log.e("QuotationViewModel", "Error de red al cargar museos", e)
            } catch (e: HttpException) {
                setErrorMessage("Error en la respuesta del servidor al cargar museos: ${e.code()} - ${e.message()}")
                Log.e("QuotationViewModel", "Error HTTP al cargar museos: ${e.code()}", e)
            } catch (e: Exception) {
                setErrorMessage("Ocurrió un error inesperado al cargar museos: ${e.message}")
                Log.e("QuotationViewModel", "Error desconocido al cargar museos", e)
            } finally {
                setLoading(false)
            }
        }
    }

    fun setInitialMuseumId(museumId: Int?) {
        // Solo actualiza si el ID es diferente o si no hay un museo seleccionado aún
        if (museumId != null && museumId != -1 && museumId != selectedMuseumId) {
            selectedMuseumId = museumId
            _availableDiscounts.value = emptyList()
            discountedPeopleGroups.clear()
            _museumBasePrice = 0.0
            _selectedMuseum.value = null

            viewModelScope.launch {
                setLoading(true)
                setErrorMessage(null)
                try {
                    val museumDetails = repository.getMuseumById(museumId)
                    _selectedMuseum.value = museumDetails
                    _museumBasePrice = museumDetails.precio.toDoubleOrNull() ?: 0.0
                    _availableDiscounts.value = museumDetails.descuentosAsociados.orEmpty()

                    setupInitialDiscountGroup(museumDetails.descuentosAsociados.orEmpty())

                    Log.d("QuotationViewModel", "Museo pre-seleccionado: ${museumDetails.nombre}, Precio base: $_museumBasePrice")
                    Log.d("QuotationViewModel", "Descuentos disponibles: ${_availableDiscounts.value.map { it.descripcionAplicacion + " (" + (it.valorDescuento.toDoubleOrNull()?.times(100) ?: 0.0) + "%)" }}")

                } catch (e: IOException) {
                    setErrorMessage("Error de red al cargar detalles del museo. Por favor, revisa tu conexión.")
                    Log.e("QuotationViewModel", "Error de red al cargar detalles para ID $museumId", e)
                    _museumBasePrice = 0.0
                    _availableDiscounts.value = emptyList()
                    discountedPeopleGroups.clear()
                    _selectedMuseum.value = null
                } catch (e: HttpException) {
                    setErrorMessage("Error en el servidor (${e.code()}) al cargar detalles del museo: ${e.message()}")
                    Log.e("QuotationViewModel", "Error HTTP al cargar detalles para ID $museumId: ${e.code()}", e)
                    _museumBasePrice = 0.0
                    _availableDiscounts.value = emptyList()
                    discountedPeopleGroups.clear()
                    _selectedMuseum.value = null
                } catch (e: Exception) {
                    setErrorMessage("Ocurrió un error inesperado al cargar detalles del museo: ${e.message}")
                    Log.e("QuotationViewModel", "Error desconocido al cargar detalles para ID $museumId", e)
                    _museumBasePrice = 0.0
                    _availableDiscounts.value = emptyList()
                    discountedPeopleGroups.clear()
                    _selectedMuseum.value = null
                } finally {
                    setLoading(false)
                }
            }
        } else if (museumId == -1) { // Caso para resetear si se pasa -1
            selectedMuseumId = null
            _selectedMuseum.value = null
            _museumBasePrice = 0.0
            _availableDiscounts.value = emptyList()
            discountedPeopleGroups.clear()
            totalPeopleGeneral = "0"
            totalInfants = "0"
            appointmentDate = ""
            startHour = ""
            endHour = ""
            clearQuotationState() // Limpiar el estado de la cotización
            Log.d("QuotationViewModel", "Estado de cotización reseteado por ID -1.")
        }
    }

    private fun setupInitialDiscountGroup(discounts: List<Discount>) {
        if (discountedPeopleGroups.isEmpty()) {
            val inapamDiscount = discounts.find { discount ->
                val containsInapamText = discount.descripcionAplicacion?.lowercase()?.contains("inapam") == true
                val is100PercentDiscount = (discount.valorDescuento.toDoubleOrNull() ?: 0.0) == 1.0
                containsInapamText && is100PercentDiscount
            }
            // Agrega el grupo INAPAM si existe, de lo contrario un grupo vacío
            discountedPeopleGroups.add(DiscountedPeopleGroup(count = "0", discount = inapamDiscount))
        }
    }

    // --- Métodos para gestionar grupos de descuento ---
    fun addDiscountedPeopleGroup() {
        discountedPeopleGroups.add(DiscountedPeopleGroup())
    }

    fun removeDiscountedPeopleGroup(group: DiscountedPeopleGroup) {
        discountedPeopleGroups.remove(group)
        if (discountedPeopleGroups.isEmpty()) { // Asegura que siempre haya al menos un grupo si se eliminan todos
            discountedPeopleGroups.add(DiscountedPeopleGroup())
        }
    }

    fun updateDiscountedPeopleGroupCount(group: DiscountedPeopleGroup, newCount: String) {
        val index = discountedPeopleGroups.indexOf(group)
        if (index != -1) {
            discountedPeopleGroups[index] = group.copy(count = newCount)
        }
    }

    fun updateDiscountedPeopleGroupDiscount(group: DiscountedPeopleGroup, newDiscount: Discount?) {
        val index = discountedPeopleGroups.indexOf(group)
        if (index != -1) {
            discountedPeopleGroups[index] = group.copy(discount = newDiscount)
        }
    }

    // --- Métodos para crear y buscar cotizaciones ---
    fun createQuotation() {
        viewModelScope.launch {
            setLoading(true)
            setErrorMessage(null)
            setQuotationResponse(null)
            setQuotationWasSearched(false) // No es una búsqueda

            val currentSelectedMuseumId = selectedMuseumId
            val currentAppointmentDate = appointmentDate
            val currentStartHour = startHour
            val currentEndHour = endHour

            if (currentSelectedMuseumId == null ||
                currentAppointmentDate.isBlank() ||
                currentStartHour.isBlank() ||
                currentEndHour.isBlank() ||
                calculatedTotalPeople <= 0
            ) {
                setErrorMessage("Por favor, complete la fecha, hora, seleccione un museo y asegúrese de que haya al menos una persona.")
                setLoading(false)
                return@launch
            }

            try {
                var totalPeopleWithDiscountForRequest = 0
                discountedPeopleGroups.forEach { group ->
                    if (group.discount != null) {
                        totalPeopleWithDiscountForRequest += (group.count.toIntOrNull() ?: 0)
                    }
                }

                val totalAmountGeneralPeople = (totalPeopleGeneral.toIntOrNull() ?: 0) * _museumBasePrice
                var totalAmountDiscountedPeople = 0.0
                discountedPeopleGroups.forEach { group ->
                    val count = group.count.toIntOrNull() ?: 0
                    val discountFactor = group.discount?.valorDescuento?.toDoubleOrNull() ?: 0.0
                    totalAmountDiscountedPeople += (count * _museumBasePrice * (1.0 - discountFactor))
                }

                val request = CotizacionGrupalRequest(
                    museum_id = currentSelectedMuseumId,
                    appointment_date = currentAppointmentDate,
                    start_hour = currentStartHour,
                    end_hour = currentEndHour,
                    total_people = calculatedTotalPeople,
                    total_people_discount = totalPeopleWithDiscountForRequest,
                    totalPeopleWithoutDiscount = totalPeopleGeneral.toIntOrNull() ?: 0,
                    total_infants = totalInfants.toIntOrNull() ?: 0,
                    totalWithDiscount = totalAmountDiscountedPeople.roundToTwoDecimals(),
                    totalWithoutDiscount = totalAmountGeneralPeople.roundToTwoDecimals(),
                    price_total = calculatedPriceTotal
                )

                val response: Response<CotizacionGrupalResponse> = repository.createCotizacion(request)
                if (response.isSuccessful) {
                    setQuotationResponse(response.body())
                    Log.d("QuotationVM", "Cotización creada: ${response.body()?.unique_id}")
                } else {
                    setErrorMessage("Error al crear cotización: ${response.code()}")
                }
            } catch (e: Exception) {
                setErrorMessage("Error: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }

    fun fetchQuotationById(quotationId: String) {
        viewModelScope.launch {
            setLoading(true)
            setErrorMessage(null)
            setQuotationResponse(null)
            setQuotationWasSearched(false) // Resetear antes de la búsqueda

            if (quotationId.isBlank()) {
                setErrorMessage("El ID de la cotización no puede estar vacío.")
                setLoading(false)
                return@launch
            }

            try {
                val response: Response<CotizacionGrupalResponse> = repository.getQuotationById(quotationId)
                if (response.isSuccessful) {
                    val receivedResponse = response.body()
                    setQuotationResponse(receivedResponse)
                    // Establecer a true SÓLO si la búsqueda fue exitosa y hay datos
                    if (receivedResponse != null) {
                        setQuotationWasSearched(true)
                    } else {
                        setErrorMessage("Cotización no encontrada o respuesta vacía.")
                    }
                    Log.d("QuotationViewModel", "Cotización encontrada: ${receivedResponse?.unique_id}, wasQuotationSearched: ${wasQuotationSearched.value}")
                } else {
                    val errorBody = response.errorBody()?.string()
                    setErrorMessage("Error al buscar cotización: ${response.code()} - ${errorBody ?: "Respuesta de error vacía"}")
                    Log.d("QuotationViewModel", "Error en la respuesta de búsqueda: ${response.code()} - $errorBody")
                    setQuotationWasSearched(false) // La búsqueda no fue exitosa
                }
            } catch (e: IOException) {
                setErrorMessage("Error de red al buscar cotización. Por favor, revisa tu conexión.")
                Log.d("QuotationViewModel", "Error de red al buscar cotización", e)
                setQuotationWasSearched(false) // La búsqueda no fue exitosa
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                setErrorMessage("Error en el servidor (${e.code()}) al buscar cotización: ${errorBody ?: "Error desconocido"}")
                Log.d("QuotationViewModel", "Error HTTP al buscar cotización: ${e.code()} - $errorBody", e)
                setQuotationWasSearched(false) // La búsqueda no fue exitosa
            } catch (e: Exception) {
                setErrorMessage("Ocurrió un error inesperado al buscar cotización: ${e.message}")
                Log.d("QuotationViewModel", "Error desconocido al buscar cotización", e)
                setQuotationWasSearched(false) // La búsqueda no fue exitosa
            } finally {
                setLoading(false)
            }
        }
    }

    fun clearQuotationState() {
        setQuotationResponse(null)
        setErrorMessage(null)
        selectedMuseumId = null
        _selectedMuseum.value = null
        appointmentDate = ""
        startHour = ""
        endHour = ""
        discountedPeopleGroups.clear()
        discountedPeopleGroups.add(DiscountedPeopleGroup())
        totalPeopleGeneral = "0"
        totalInfants = "0"
        _museumBasePrice = 0.0
        _availableDiscounts.value = emptyList()
        setQuotationWasSearched(false) // Limpiar el indicador al limpiar todo el estado
    }

    // --- NUEVO MÉTODO: Limpiar el estado de la cotización BUSCADA ---
    fun clearQuotationSearchState() {
        setQuotationResponse(null) // Limpia la respuesta de cotización
        setErrorMessage(null)     // Limpia el mensaje de error
        setQuotationWasSearched(false) // Restablece el indicador de búsqueda
        Log.d("QuotationViewModel", "clearQuotationSearchState ejecutado. Estado de búsqueda limpio.")
    }
}

fun Double.roundToTwoDecimals(): Double {
    return (this * 100).roundToInt() / 100.0
}