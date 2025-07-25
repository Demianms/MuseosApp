package com.demian.chamus.screens.museums

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import com.demian.chamus.models.Museum
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumCategoriesSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumDescriptionSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumDiscountsSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumHeaderSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumHoursAndPricesSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumMapSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.MuseumMoreInfoSection
import com.demian.chamus.screens.museums.componentsDetailMuseum.RoomCard
import com.demian.chamus.viewmodel.MuseumViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsMuseums(
    museumId: Int,
    navController: NavController,
    viewModel: MuseumViewModel = viewModel(),
) {
    LaunchedEffect(museumId) {
        viewModel.loadMuseumDetails(museumId)
    }

    val museum by viewModel.selectedMuseum.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalles del Museo",
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = error ?: "Error desconocido",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            museum == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Museo no encontrado")
                }
            }
            else -> {
                // Delegamos la mayor parte de la lógica de UI a un Composable más pequeño
                MuseumDetailContent(
                    museum = museum!!,
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                )
            }
        }
    }
}

// Mantenemos MuseumDetailContent aquí, pero lo refactorizamos usando los nuevos componentes
@SuppressLint("DefaultLocale")
@Composable
fun MuseumDetailContent(museum: Museum, modifier: Modifier = Modifier, navController: NavController) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. Cabecera (Imagen y Título)
        item {
            MuseumHeaderSection(museum = museum)
        }

        // 2. Sección de Categorías
        if (museum.categories.isNotEmpty()) {
            item {
                MuseumCategoriesSection(museum.categories)
            }
        }

        // 3. Sección del Mapa
        item {
            MuseumMapSection(
                museumName = museum.nombre,
                latitude = museum.latitud,
                longitude = museum.longitud
            )
        }

        // 4. Sección de Descripción
        item {
            MuseumDescriptionSection(museum.descripcion)
        }

        // 5. Sección de Horarios y Precios
        item {
            MuseumHoursAndPricesSection(
                openingTime = museum.horaDeApertura,
                closingTime = museum.horaDeCierre,
                price = museum.precio
            )
        }

        // 6. Sección de Descuentos
        val discounts = museum.descuentosAsociados ?: emptyList()
        item {
            MuseumDiscountsSection(discounts)
        }

        // 7. Sección de Más Información (Sitio web)
        item {
            MuseumMoreInfoSection(museumUrl = museum.url)
        }

        // 8. Sección de Salas
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
                RoomCard( // RoomCard ya es un componente separado
                    room = room,
                    museumId = museum.id,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            item {
                Text(
                    text = "No hay salas aun.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}