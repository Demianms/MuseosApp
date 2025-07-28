package com.demian.chamus.screens

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight // <--- ¡ASEGÚRATE DE ESTA IMPORTACIÓN!
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.demian.chamus.models.CotizacionGrupalResponse
import com.demian.chamus.viewmodel.QuotationViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import android.util.Log
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import java.text.NumberFormat
import java.util.*

@Composable
fun DetailRowWithIcon(icon: ImageVector, label: String, value: String, valueStyle: androidx.compose.ui.text.TextStyle = LocalTextStyle.current) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(24.dp).padding(end = 8.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = value, style = valueStyle)
    }
}

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchedQuotationDisplayScreen(
    navController: NavController,
    quotationViewModel: QuotationViewModel = viewModel()
) {
    val quotationResponse: CotizacionGrupalResponse? by quotationViewModel.quotationResponse.collectAsState()
    val context = LocalContext.current
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("es", "MX")) }

    LaunchedEffect(quotationResponse) {
        Log.d("SearchedQuotationScreen", "-> Recomposition. Current quotationResponse: $quotationResponse")
        if (quotationResponse != null) {
            Log.d("SearchedQuotationScreen", "   - Unique ID (raíz): ${quotationResponse!!.unique_id}")
            Log.d("SearchedQuotationScreen", "   - Cotizacion (anidada) es nula? ${quotationResponse!!.cotizacion == null}")
            if (quotationResponse!!.cotizacion != null) {
                Log.d("SearchedQuotationScreen", "   - Museum name (anidado): ${quotationResponse!!.cotizacion?.museum?.nombre}")
                Log.d("SearchedQuotationScreen", "   - Price Total (anidado): ${quotationResponse!!.cotizacion?.price_total}")
            }
        } else {
            Log.d("SearchedQuotationScreen", "   - quotationResponse es actualmente NULO.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles de Cotización Buscada", style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        // Aquí llamamos al método que *debe* existir en QuotationViewModel
                        quotationViewModel.clearQuotationSearchState() // <--- ¡ASEGÚRATE DE QUE ESTE MÉTODO EXISTA EN TU QuotationViewModel!
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            quotationResponse?.let { nonNullQuotationResponse ->
                Log.d("SearchedQuotationScreen", "Mostrando detalles de la cotización encontrada.")
                Text(
                    text = "Cotización Encontrada",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(24.dp).padding(end = 8.dp))
                            Text(text = "ID Único:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = nonNullQuotationResponse.unique_id, style = LocalTextStyle.current)
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    nonNullQuotationResponse.unique_id.let { id ->
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

                        // Ahora accedemos a `cotizacion` de forma segura.
                        nonNullQuotationResponse.cotizacion?.let { cotizacionDetalle ->
                            Log.d("SearchedQuotationScreen", "Mostrando detalles de CotizacionDetalle.")
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            DetailRowWithIcon(Icons.Default.Place, "Museo:", cotizacionDetalle.museum?.nombre ?: "N/A")
                            DetailRowWithIcon(Icons.Default.CalendarMonth, "Fecha:",
                                cotizacionDetalle.appointment_date
                            )
                            DetailRowWithIcon(Icons.Default.Schedule, "Hora Inicio:",
                                cotizacionDetalle.start_hour
                            )
                            DetailRowWithIcon(Icons.Default.Schedule, "Hora Fin:",
                                cotizacionDetalle.end_hour
                            )
                            DetailRowWithIcon(Icons.Default.Groups, "Total Personas:", cotizacionDetalle.total_people.toString())
                            DetailRowWithIcon(Icons.Default.Group, "Personas sin Descuento:", cotizacionDetalle.totalPeopleWithoutDiscount.toString())
                            DetailRowWithIcon(Icons.Default.ChildCare, "Infantes:", cotizacionDetalle.total_infants.toString())
                            DetailRowWithIcon(Icons.Default.Group, "Personas con Descuento:", cotizacionDetalle.total_people_discount.toString())
                            DetailRowWithIcon(
                                Icons.Default.MonetizationOn,
                                "Total sin Descuento:",
                                cotizacionDetalle.totalWithoutDiscount.let { currencyFormat.format(it) }
                                    ?: "N/A"
                            )
                            DetailRowWithIcon(
                                Icons.Default.MonetizationOn,
                                "Total con Descuento:",
                                cotizacionDetalle.totalWithDiscount.let { currencyFormat.format(it) }
                                    ?: "N/A"
                            )
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))
                            DetailRowWithIcon(
                                Icons.Default.MonetizationOn,
                                "Precio Total Final:",
                                cotizacionDetalle.price_total.let { currencyFormat.format(it) } ?: "N/A",
                                valueStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        } ?: run {
                            Log.e("SearchedQuotationScreen", "cotizacionDetalle es NULO, no se pueden mostrar los detalles anidados.")
                            Text(
                                text = "No se pudieron cargar los detalles anidados de la cotización.",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                }
            } ?: run {
                Log.w("SearchedQuotationScreen", "quotationResponse es NULO, mostrando mensaje de error.")
                Text(
                    text = "No se pudieron cargar los detalles de la cotización.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Button(onClick = { navController.popBackStack() }) {
                    Text("Volver")
                }
            }
        }
    }
}