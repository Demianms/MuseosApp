package com.demian.chamus.screens.museums

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsMuseums(
    museumId: Int,
    navController: NavController,
    viewModel: MuseumViewModel = viewModel(),
    quotationViewModel: QuotationViewModel = viewModel()
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
    val quotationResponse by quotationViewModel.quotationResponse.collectAsState()

    val shouldNavigate by quotationViewModel.navigationTrigger.collectAsState()

    LaunchedEffect(shouldNavigate) {
        if (shouldNavigate) {
            Log.d("Navigation", "Navegando a searched_quotation_display_screen")
            Log.d("Navigation", "shouldNavigate value: $shouldNavigate")
            Log.d("Navigation", "Current back stack: ${navController.currentBackStackEntry?.destination?.route}")
            navController.navigate("searched_quotation_display_screen") {
                launchSingleTop = true
                // Limpiar el back stack si es necesario
                popUpTo("museum_detail/{museumId}") { inclusive = false }
            }
            quotationViewModel.resetNavigationTrigger()
        }
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
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text(
                    text = "Buscar Cotización Existente",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = quotationIdInput,
                    onValueChange = { quotationIdInput = it },
                    label = { Text("ID de Cotización") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (quotationIdInput.isNotBlank()) {
                            scope.launch {
                                quotationViewModel.fetchQuotationById(quotationIdInput.trim())
                            }
                        } else {
                            quotationViewModel.setErrorMessage("Ingrese un ID válido")
                        }
                    },
                    enabled = !quotationIsLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (quotationIsLoading) "Buscando..." else "Buscar Cotización")
                }

                if (quotationIsLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                quotationErrorMessage?.let { error ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
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