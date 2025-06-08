package com.demian.chamus.screens.museums

// === Importaciones de AndroidX y Kotlin ===
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

// === Importaciones de Jetpack Compose UI ===
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale


private val ColorFondoApp = Color(0xFFE8F0F7)
private val ColorFondoEncabezado = Color(0xFFFFFFFF)
private val ColorAzulOscuroPrincipal = Color(0xFF003366)
private val ColorGrisTarjetaInactiva = Color(0xFFB0B0B0)
private val ColorAzulMedioEtiqueta = Color(0xFF4D6D9A)
private val ColorAzulClaroEtiqueta = Color(0xFF66CCFF)
private val ColorAzulMuyOscuroEtiqueta = Color(0X01A33)
private val ColorVerdeActivo = Color(0xFF4CAF50)
private val ColorRojoInactivo = Color(0xFFCC0000)
private val ColorTextoSobreOscuro = Color(0xFFFFFFFF)
private val ColorTextoSobreClaro = Color(0xFF000000)
private val ColorAzulCieloInfo = Color(0xFF87CEEB)

// =======================================================
// PANTALLA PRINCIPAL: ListMuseumsScreen
// =======================================================
@Composable
fun ListMuseumsScreen(
    museumViewModel: MuseumViewModel = viewModel() // Obtener una instancia del ViewModel
) {
    // Observa el LiveData del ViewModel para reaccionar a los cambios
    val museumsState = museumViewModel.museums.observeAsState(initial = emptyList())
    val errorMessageState = museumViewModel.errorMessage.observeAsState()
    val isLoadingState = museumViewModel.isLoading.observeAsState(initial = false)

    // Los valores actuales del estado para usarlos en la UI
    val museums = museumsState.value
    val errorMessage = errorMessageState.value
    val isLoading = isLoadingState.value

    // Puedes usar LaunchedEffect si no inicializas la carga en el init del ViewModel
    // LaunchedEffect(Unit) {
    //     museumViewModel.fetchMuseums()
    // }

    Scaffold(
        containerColor = ColorFondoApp,
        contentColor = ColorTextoSobreClaro,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()) // Permite el scroll si hay muchos elementos
            ) {
                // Sección del Encabezado (Header)
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

                    // Filtros (los estás manejando estáticamente por ahora)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Filter("Arte", ColorAzulOscuroPrincipal)
                        Spacer(Modifier.width(8.dp))
                        Filter("Historia", ColorAzulMedioEtiqueta)
                        Spacer(Modifier.width(8.dp))
                        Filter("Cultura", ColorAzulClaroEtiqueta)
                        Spacer(Modifier.width(8.dp))
                        Filter("Todos", ColorAzulMuyOscuroEtiqueta)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sección del Contenido Principal (Lista de Museos o Mensajes de Estado)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally // Para centrar el indicador de carga y mensajes
                ) {
                    // === LÓGICA DE CARGA Y ERRORES ===
                    if (isLoading) {
                        // Muestra un indicador de progreso mientras se cargan los datos
                        CircularProgressIndicator(
                            modifier = Modifier.padding(32.dp),
                            color = ColorAzulOscuroPrincipal
                        )
                        Text(text = "Cargando museos...", color = ColorTextoSobreClaro)
                    } else if (errorMessage != null) {
                        // Muestra un mensaje de error si algo salió mal
                        Text(
                            text = errorMessage,
                            color = ColorRojoInactivo,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )
                        // Opcional: un botón para reintentar la carga
                        // Button(onClick = { museumViewModel.fetchMuseums() }) {
                        //     Text("Reintentar")
                        // }
                    } else if (museums.isEmpty()) {
                        // Muestra este mensaje si la lista está vacía y no hay errores ni carga
                        Spacer(modifier = Modifier.height(16.dp))
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
                                    text = "No hay museos para mostrar", // Mensaje actualizado
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ColorTextoSobreOscuro
                                )
                            }
                        }
                    } else {
                        // === MOSTRAR LOS MUSEOS REALES DE LA API ===
                        museums.forEach { museum ->
                            MuseumCard(
                                name = museum.nombre, // Usar 'nombre' de tu modelo Museum
                                isActive = museum.isActive, // Propiedad calculada en Museum
                                imageUrl = museum.imageUrl, // Propiedad calculada en Museum
                                onClick = {
                                    // TODO: Implementar navegación a la pantalla de detalles del museo
                                    // Por ejemplo, usando un NavController:
                                    // navController.navigate("museum_detail/${museum.id}")
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

// =======================================================
// COMPONENTES REUTILIZABLES (MuseumCard y Filter)
// =======================================================

@Composable
fun MuseumCard(
    name: String,
    isActive: Boolean,
    imageUrl: String?,
    onClick: () -> Unit // Lambda para el evento de clic en la tarjeta
) {
    val cardBackgroundColor = if (isActive) ColorAzulOscuroPrincipal else ColorGrisTarjetaInactiva
    val textColor = ColorTextoSobreOscuro
    val statusTagColor = if (isActive) ColorVerdeActivo else ColorRojoInactivo

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() }, // Hacer la tarjeta clicable
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp // Sombra para dar profundidad
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Carga de imagen asíncrona con Coil
            if (imageUrl != null && imageUrl.isNotBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de fondo del museo: $name",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop // Escala la imagen para llenar el espacio
                )
                // Overlay oscuro sobre la imagen para mejorar la legibilidad del texto
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            } else {
                // Fallback si no hay URL de imagen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardBackgroundColor)
                )
            }

            // Contenido de la tarjeta (estado y nombre del museo)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Etiqueta de estado (ACTIVO/INACTIVO)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd) // Alineado a la esquina superior derecha
                        .padding(12.dp)
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
                // Nombre del museo
                Text(
                    text = name,
                    color = textColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart) // Alineado a la esquina inferior izquierda
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun Filter(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ColorFondoEncabezado, // Fondo blanco para los filtros
        border = BorderStroke(1.dp, color), // Borde de color dinámico
        modifier = Modifier.clickable { /* TODO: Implementar lógica de filtro */ }
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}}