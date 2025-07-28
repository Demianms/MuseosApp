package com.demian.chamus.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demian.chamus.models.CotizacionDetalle
import com.demian.chamus.models.CotizacionGrupalResponse
import com.demian.chamus.viewmodel.QuotationViewModel
import java.text.NumberFormat
import java.util.Locale

@SuppressLint("DefaultLocale")
@Composable
fun SearchedQuotationDisplayScreen(
    navController: NavController,
    quotationViewModel: QuotationViewModel = viewModel()
) {
    val isLoading by quotationViewModel.isLoading.collectAsState()
    val errorMessage by quotationViewModel.errorMessage.collectAsState()
    val quotationResponse by quotationViewModel.quotationResponse.collectAsState()
    val wasSearched by quotationViewModel.wasQuotationSearched.collectAsState()
    val effectiveCotizacion = quotationResponse?.getEffectiveCotizacion()

    // Utilidades
    val context = LocalContext.current
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }

    DisposableEffect(Unit) {
        onDispose {
            // Limpiar el estado al salir de la pantalla
            if (wasSearched) {
                quotationViewModel.clearQuotationSearchState()
            }
        }
    }

    // Scaffold principal
    Scaffold(
        topBar = {
            QuotationDetailsTopBar(
                navController = navController,
                quotationId = quotationResponse?.unique_id ?: effectiveCotizacion?.unique_id
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> LoadingState()
                    !wasSearched -> EmptyState { navController.popBackStack() }
                    errorMessage != null -> ErrorState(errorMessage!!) { navController.popBackStack() }
                    effectiveCotizacion == null -> ErrorState("Información incompleta") { navController.popBackStack() }
                    else -> SuccessState(
                        cotizacion = effectiveCotizacion,
                        uniqueId = quotationResponse?.unique_id ?: effectiveCotizacion.unique_id,
                        currencyFormat = currencyFormat,
                        onNavigateToMuseum = { museumId ->
                            navController.navigate("museum_detail/$museumId") {
                                popUpTo("searched_quotation_display_screen") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuotationDetailsTopBar(
    navController: NavController,
    quotationId: String?
) {
    val context = LocalContext.current

    TopAppBar(
        title = { Text("Detalles de Cotización") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
        },
        actions = {
            quotationId?.let { id ->
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Quotation ID", id)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "ID copiado", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copiar ID")
                }
            }
        }
    )
}

@Composable
private fun SuccessState(
    quotationResponse: CotizacionGrupalResponse,
    cotizacion: CotizacionDetalle,
    currencyFormat: NumberFormat,
    onNavigateToMuseum: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Cotización Encontrada",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        QuotationDetailsCard(
            uniqueId = quotationResponse.unique_id ?: cotizacion.unique_id,
            cotizacion = cotizacion,
            currencyFormat = currencyFormat,
            onNavigateToMuseum = onNavigateToMuseum
        )
    }
}

@Composable
private fun LoadingState() {
    CircularProgressIndicator()
}

@Composable
private fun ErrorState(errorMessage: String, onBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
private fun EmptyState(onBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "No se encontraron datos de cotización",
            style = MaterialTheme.typography.bodyLarge
        )
        Button(onClick = onBack) {
            Text("Volver")
        }
    }
}

@Composable
private fun SuccessState(
    cotizacion: CotizacionDetalle,
    uniqueId: String,
    currencyFormat: NumberFormat,
    onNavigateToMuseum: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Cotización Encontrada",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        QuotationDetailsCard(
            uniqueId = uniqueId,
            cotizacion = cotizacion,
            currencyFormat = currencyFormat,
            onNavigateToMuseum = onNavigateToMuseum
        )
    }
}


@Composable
private fun QuotationDetailsCard(
    uniqueId: String,
    cotizacion: CotizacionDetalle,
    currencyFormat: NumberFormat,
    onNavigateToMuseum: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sección de ID
            QuotationIdSection(uniqueId)

            // Información de la visita
            QuotationVisitInfoSection(cotizacion)

            // Detalles de asistentes
            QuotationAttendeesSection(cotizacion)

            // Resumen económico
            QuotationFinancialSummary(cotizacion, currencyFormat)

            // Botón para ver museo
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onNavigateToMuseum(cotizacion.museum_id) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Museo Relacionado")
            }
        }
    }
}

@Composable
private fun QuotationIdSection(uniqueId: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Text(
            text = "ID Único:",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = uniqueId)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun QuotationVisitInfoSection(cotizacion: CotizacionDetalle) {
    Text(
        text = "Información de la Visita",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    DetailRowWithIcon(
        icon = Icons.Default.Place,
        label = "Museo:",
        value = cotizacion.museum?.nombre ?: "No especificado"
    )

    DetailRowWithIcon(
        icon = Icons.Default.CalendarMonth,
        label = "Fecha:",
        value = cotizacion.appointment_date
    )

    DetailRowWithIcon(
        icon = Icons.Default.Schedule,
        label = "Horario:",
        value = "${cotizacion.start_hour.take(5)} - ${cotizacion.end_hour.take(5)}"
    )

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun QuotationAttendeesSection(cotizacion: CotizacionDetalle) {
    Text(
        text = "Asistentes",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    DetailRowWithIcon(
        icon = Icons.Default.Groups,
        label = "Total Personas:",
        value = cotizacion.total_people.toString()
    )

    DetailRowWithIcon(
        icon = Icons.Default.Group,
        label = "Con Descuento:",
        value = cotizacion.total_people_discount.toString()
    )

    DetailRowWithIcon(
        icon = Icons.Default.Group,
        label = "Sin Descuento:",
        value = cotizacion.totalPeopleWithoutDiscount.toString()
    )

    DetailRowWithIcon(
        icon = Icons.Default.ChildCare,
        label = "Infantes:",
        value = cotizacion.total_infants.toString()
    )

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun QuotationFinancialSummary(
    cotizacion: CotizacionDetalle,
    currencyFormat: NumberFormat
) {
    Text(
        text = "Resumen Económico",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )

    DetailRowWithIcon(
        icon = Icons.Default.MonetizationOn,
        label = "Total con Descuento:",
        value = currencyFormat.format(cotizacion.totalWithDiscount)
    )

    DetailRowWithIcon(
        icon = Icons.Default.MonetizationOn,
        label = "Total sin Descuento:",
        value = currencyFormat.format(cotizacion.totalWithoutDiscount)
    )

    DetailRowWithIcon(
        icon = Icons.Default.MonetizationOn,
        label = "PRECIO TOTAL:",
        value = currencyFormat.format(cotizacion.price_total),
        valueStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun DetailRowWithIcon(
    icon: ImageVector,
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp).padding(end = 8.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, style = valueStyle)
    }
}