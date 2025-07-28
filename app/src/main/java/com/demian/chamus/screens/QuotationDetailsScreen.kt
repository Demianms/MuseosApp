package com.demian.chamus.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.demian.chamus.models.CotizacionDetalle
import com.demian.chamus.models.CotizacionGrupalResponse
import com.demian.chamus.models.MuseumBrief
import java.text.NumberFormat
import java.util.*
import kotlin.math.log


@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationDetailsScreen(
    navController: NavController,
    quotationResponse: CotizacionGrupalResponse?
) {
    val context = LocalContext.current
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de la Cotización") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    // Botón de copiar ID en el AppBar (más visible)
                    quotationResponse?.unique_id?.let { id ->
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (quotationResponse?.cotizacion == null) {
                Text("No se pudieron cargar los detalles")
            } else {
                val cotizacion = quotationResponse.cotizacion
                println(cotizacion)
                Text(
                    text = "¡Cotización Realizada con Éxito!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Fila para el ID Único con botón de copiar
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
                                text = "ID Único de Cotización:",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            quotationResponse.unique_id?.let { Text(text = it) }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    quotationResponse.unique_id.let { id ->
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Quotation ID", id)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "ID copiado: $id", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar ID")
                            }
                        }
                        HorizontalDivider()

                        val cotizacion = quotationResponse.cotizacion

                        Text(
                            text = "Información de la Visita",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        DetailRowD(
                            icon = Icons.Default.Place,
                            label = "Museo:",
                            value = cotizacion.museum?.nombre ?: "No especificado"
                        )

                        DetailRowD(
                            icon = Icons.Default.CalendarMonth,
                            label = "Fecha de la Cita:",
                            value = cotizacion.appointment_date
                        )

                        DetailRowD(
                            icon = Icons.Default.Schedule,
                            label = "Horario:",
                            value = "${cotizacion.start_hour.take(5)} - ${cotizacion.end_hour.take(5)}"
                        )
                        HorizontalDivider()

                        Text(
                            text = "Detalles de Asistentes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        DetailRowD(
                            icon = Icons.Default.Groups,
                            label = "Total Personas:",
                            value = cotizacion.total_people.toString()
                        )

                        DetailRowD(
                            icon = Icons.Default.Group,
                            label = "Con Descuento (INAPAM):",
                            value = cotizacion.total_people_discount.toString()
                        )

                        DetailRowD(
                            icon = Icons.Default.Group,
                            label = "Sin Descuento:",
                            value = cotizacion.totalPeopleWithoutDiscount.toString()
                        )

                        DetailRowD(
                            icon = Icons.Default.ChildCare,
                            label = "Infantes:",
                            value = cotizacion.total_infants.toString()
                        )
                        HorizontalDivider()

                        Text(
                            text = "Resumen Económico",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        DetailRowD(
                            icon = Icons.Default.MonetizationOn,
                            label = "Total con Descuento:",
                            value = currencyFormat.format(cotizacion.totalWithDiscount)
                        )

                        DetailRowD(
                            icon = Icons.Default.MonetizationOn,
                            label = "Total sin Descuento:",
                            value = currencyFormat.format(cotizacion.totalWithoutDiscount)
                        )

                        DetailRowD(
                            icon = Icons.Default.MonetizationOn,
                            label = "PRECIO TOTAL:",
                            value = currencyFormat.format(cotizacion.price_total),
                            valueStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Botón para volver a los detalles del museo
                Button(
                    onClick = {
                        val museumId = cotizacion.museum_id
                        if (museumId != null && museumId > 0) {
                            try {
                                navController.navigate("museum_detail/$museumId") {
                                    launchSingleTop = true
                                    restoreState = true

                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error al navegar: ${e.message}", Toast.LENGTH_SHORT).show()
                                Log.e("Navigation", "Error al navegar a museum_detail", e)
                            }
                        } else {
                            Toast.makeText(context, "ID de museo inválido", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver al Museo")
                }

            }
        }
    }
}

@Composable
fun DetailRowD(
    icon: ImageVector,
    label: String,
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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

@Preview(showBackground = true)
@Composable
fun PreviewQuotationDetailsScreen() {
    val sampleMuseumBrief = MuseumBrief(
        id = 1,
        nombre = "Museo de Historia Natural"
    )
    val sampleCotizacionDetalle = CotizacionDetalle(
        id = 101,
        unique_id = "ABC-12345",
        museum_id = 1,
        museum = sampleMuseumBrief,
        appointment_date = "2025-08-15",
        start_hour = "10:00:00",
        end_hour = "12:00:00",
        total_people = 10,
        total_people_discount = 3,
        totalPeopleWithoutDiscount = 5,
        total_infants = 2,
        totalWithDiscount = 120.00,
        totalWithoutDiscount = 400.00,
        price_total = 520.00,
        status = "Completed",
        createdAt = "2025-08-14T10:00:00Z",
        updatedAt = "2025-08-14T10:00:00Z"
    )
    val sampleResponse = CotizacionGrupalResponse(
        message = "Cotización generada con éxito.",
        unique_id = "ABC-12345",
        cotizacion = sampleCotizacionDetalle
    )
    QuotationDetailsScreen(rememberNavController(), sampleResponse)
}