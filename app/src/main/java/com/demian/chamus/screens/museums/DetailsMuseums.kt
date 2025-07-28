package com.demian.chamus.screens.museums

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions

import com.demian.chamus.models.Museum
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumCategoriesSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumDescriptionSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumDiscountsSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumHeaderSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumHoursAndPricesSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumMapSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumMoreInfoSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.RoomCard
import com.demian.chamus.viewmodel.MuseumViewModel
import com.demian.chamus.viewmodel.QuotationViewModel
// No necesitas importar SharedQuotationViewModel, ya lo eliminamos
// import com.demian.chamus.viewmodel.SharedQuotationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsMuseums(
    museumId: Int,
    navController: NavController,
    viewModel: MuseumViewModel = viewModel(),
    quotationViewModel: QuotationViewModel = viewModel()  // Asegúrate que este parámetro esté presente
) {
    LaunchedEffect(museumId) {
        Log.d("DetailsMuseums", "Cargando detalles del museo con ID: $museumId")
        viewModel.loadMuseumDetails(museumId)
    }

    val museum by viewModel.selectedMuseum.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val quotationIsLoading by quotationViewModel.isLoading.collectAsState()
    val quotationErrorMessage by quotationViewModel.errorMessage.collectAsState()
    val quotationResponse by quotationViewModel.quotationResponse.collectAsState() // Observando el StateFlow del QuotationViewModel

    // *** CAMBIO CRUCIAL AQUÍ: NAVEGAR A LA NUEVA PANTALLA ***
    // Si quotationResponse se actualiza con una respuesta válida (después de fetchQuotationById), navega.
    LaunchedEffect(quotationResponse) {
        if (quotationResponse != null && quotationViewModel.wasQuotationSearched.value) { // Agrega un indicador para saber si fue búsqueda
            Log.d("DetailsMuseums", "Cotización encontrada por ID: ${quotationResponse!!.unique_id}. Navegando a SearchedQuotationDisplayScreen...")
            // --- CAMBIO CLAVE 2: YA NO NECESITAS ASIGNAR A sharedViewModel.lastQuotationResponse ---
            // sharedViewModel.lastQuotationResponse = quotationResponse // Eliminar esta línea
            navController.navigate("searched_quotation_display_screen") {
                launchSingleTop = true
                // popUpTo("museum_detail/{museumId}") { inclusive = false } // Considera esto para limpiar el back stack si lo necesitas
            }
            // Después de navegar, limpia el estado de búsqueda en el ViewModel para evitar re-navegaciones
            quotationViewModel.clearQuotationSearchState() // --- NUEVO MÉTODO A AGREGAR ---
        }
        // La condición 'else if (false)' no tiene sentido, la eliminamos.
        // Si quotationResponse es null y errorMessage no es null, se mostrará el mensaje de error en la UI.
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Museo", style = MaterialTheme.typography.headlineSmall, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            error != null -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(text = error ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
            }
            museum == null -> Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Museo no encontrado")
            }
            else -> MuseumDetailContent(
                museum = museum!!,
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                museumId = museumId,
                quotationViewModel = quotationViewModel,
                // --- CAMBIO CLAVE 3: ELIMINAR sharedViewModel de la llamada ---
                // sharedViewModel = sharedViewModel,
                quotationIsLoading = quotationIsLoading,
                quotationErrorMessage = quotationErrorMessage
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun MuseumDetailContent(
    museum: Museum,
    modifier: Modifier = Modifier,
    navController: NavController,
    museumId: Int,
    quotationViewModel: QuotationViewModel,
    // --- CAMBIO CLAVE 4: ELIMINAR sharedViewModel de los parámetros ---
    // sharedViewModel: SharedQuotationViewModel,
    quotationIsLoading: Boolean,
    quotationErrorMessage: String?
) {
    var quotationIdInput by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        item { MuseumHeaderSection(museum = museum) }
        if (museum.categories.isNotEmpty()) item { MuseumCategoriesSection(museum.categories) }
        item { MuseumMapSection(museumName = museum.nombre, latitude = museum.latitud, longitude = museum.longitud) }
        item { MuseumDescriptionSection(museum.descripcion) }
        item { MuseumHoursAndPricesSection(openingTime = museum.horaDeApertura, closingTime = museum.horaDeCierre, price = museum.precio) }
        item { MuseumDiscountsSection(discounts = museum.descuentosAsociados ?: emptyList()) }
        item { MuseumMoreInfoSection(museumUrl = museum.url) }

        // Sección para generar una nueva cotización
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("quotation_screen?museumId=$museumId") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) { Text("Generar Cotización para este Museo") }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Sección para buscar cotización por ID
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Buscar Cotización Existente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = quotationIdInput,
                    onValueChange = { quotationIdInput = it },
                    label = { Text("ID de la Cotización") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (quotationIdInput.isNotBlank()) {
                            Log.d("MuseumDetailContent", "Iniciando búsqueda con ID: $quotationIdInput")
                            scope.launch {
                                // --- CAMBIO CLAVE 5: LLAMAR A fetchQuotationById directamente y marcar que fue una búsqueda ---
                                quotationViewModel.fetchQuotationById(quotationIdInput)
                                quotationViewModel.setQuotationWasSearched(true) // Establece el indicador
                            }
                            quotationViewModel.setErrorMessage(null) // Limpiar errores anteriores al buscar
                        } else {
                            quotationViewModel.setErrorMessage("Por favor, introduce un ID de cotización.")
                        }
                    },
                    enabled = !quotationIsLoading,
                    modifier = Modifier.fillMaxWidth()
                ) { Text(if (quotationIsLoading) "Buscando..." else "Buscar Cotización") }

                if (quotationIsLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                quotationErrorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (museum.rooms.isNotEmpty()) {
            item {
                Text(
                    text = "Salas del Museo",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            items(museum.rooms) { room ->
                RoomCard(room = room, museumId = museum.id, navController = navController)
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    text = "No hay salas aún.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}