package com.demian.chamus.screens.museums

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.demian.chamus.viewmodel.MuseumViewModel
import androidx.compose.ui.layout.ContentScale

// Importaciones necesarias para SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

private val ColorFondoApp = Color(0xFFE8F0F7)
private val ColorFondoEncabezado = Color(0xFFFFFFFF)
private val ColorAzulOscuroPrincipal = Color(0xFF003366)
private val ColorGrisTarjetaInactiva = Color(0xFFB0B0B0)
private val ColorAzulMedioEtiqueta = Color(0xFF4D6D9A)
private val ColorAzulClaroEtiqueta = Color(0xFF66CCFF)
private val ColorAzulMuyOscuroEtiqueta = Color(0xFF001A33)
private val ColorVerdeActivo = Color(0xFF4CAF50)
private val ColorRojoInactivo = Color(0xFFCC0000)
private val ColorTextoSobreOscuro = Color(0xFFFFFFFF)
private val ColorTextoSobreClaro = Color(0xFF000000)
private val ColorAzulCieloInfo = Color(0xFF87CEEB)

@Composable
fun ListMuseumsScreen(viewModel: MuseumViewModel = viewModel()) {
    val museums = viewModel.museums.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value

    // 1. Crear el estado de SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
    // Usamos un rememberCoroutineScope para lanzar la recarga
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        containerColor = ColorFondoApp,
        contentColor = ColorTextoSobreClaro,
        content = { innerPadding ->
            // 2. Envolver el contenido con SwipeRefresh
            SwipeRefresh(
                state = swipeRefreshState,
                onRefresh = {
                    // Cuando el usuario tira para recargar, llamamos a la función de recarga del ViewModel
                    // Es buena práctica envolver esto en un coroutineScope
                    coroutineScope.launch {
                        viewModel.loadMuseums() // Asume que tienes una función para recargar los datos
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize() // Asegúrate de que la columna interna también llene el espacio
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ColorFondoEncabezado)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Encuentra tu museo favorito",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = ColorAzulOscuroPrincipal,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Filter("Arte", ColorAzulOscuroPrincipal, viewModel)
                            Spacer(Modifier.width(8.dp))
                            Filter("Historia", ColorAzulMedioEtiqueta, viewModel)
                            Spacer(Modifier.width(8.dp))
                            Filter("Cultura", ColorAzulClaroEtiqueta, viewModel)
                            Spacer(Modifier.width(8.dp))
                            Filter("Todos", ColorAzulMuyOscuroEtiqueta, viewModel)
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        when {
                            isLoading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                            error != null -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = error,
                                        color = Color.Red,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            museums.isEmpty() -> {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = ColorAzulCieloInfo,
                                    contentColor = ColorTextoSobreOscuro
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier.padding(16.dp)
                                    ) {
                                        Text(
                                            text = "No hay museos disponibles",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = ColorTextoSobreOscuro
                                        )
                                    }
                                }
                            }
                            else -> {
                                museums.forEach { museum ->
                                    MuseumCard(
                                        name = museum.name,
                                        isActive = museum.estado == "active",
                                        imageUrl = museum.imageUrl,
                                        description = museum.descripcion,
                                        price = museum.precio,
                                        numberOfRooms = museum.numberOfRooms,
                                        onClick = { /* Aquí podrías navegar a los detalles del museo */ }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun MuseumCard(
    name: String,
    isActive: Boolean,
    imageUrl: String?,
    description: String,
    price: Double,
    numberOfRooms: Int,
    onClick: () -> Unit
) {
    val cardBackgroundColor = if (isActive) ColorAzulOscuroPrincipal else ColorGrisTarjetaInactiva
    val textColor = ColorTextoSobreOscuro
    val statusTagColor = if (isActive) ColorVerdeActivo else ColorRojoInactivo

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de fondo del museo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardBackgroundColor)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusTagColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) "ACTIVO" else "INACTIVO",
                        color = ColorTextoSobreOscuro,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = name,
                        color = textColor,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = description.take(100) + if (description.length > 100) "..." else "",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Precio: $$price",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Salas: $numberOfRooms",
                        color = textColor,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun Filter(text: String, color: Color, viewModel: MuseumViewModel) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ColorFondoEncabezado,
        border = BorderStroke(1.dp, color),
        modifier = Modifier.clickable { viewModel.setFilter(text) }
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}