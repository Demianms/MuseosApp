package com.demian.chamus.screens

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.demian.chamus.viewmodel.QuotationViewModel
import java.util.Calendar
import kotlin.math.roundToInt

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotationScreen(
    navController: NavController,
    initialMuseumId: Int?,
    quotationViewModel: QuotationViewModel = viewModel()
) {

    LaunchedEffect(Unit) {
        quotationViewModel.clearQuotationState()
    }

    LaunchedEffect(initialMuseumId) {
        quotationViewModel.setInitialMuseumId(initialMuseumId)
    }

    val context = LocalContext.current
    val isLoading by quotationViewModel.isLoading.collectAsState()
    val errorMessage by quotationViewModel.errorMessage.collectAsState()
    val quotationResponse by quotationViewModel.quotationResponse.collectAsState()

    val museums by quotationViewModel.museums.collectAsState()
    val selectedMuseumId = quotationViewModel.selectedMuseumId
    val availableDiscounts by quotationViewModel.availableDiscounts.collectAsState()

    val datePickerDialog = remember {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                quotationViewModel.appointmentDate = "$year-${String.format("%02d", month + 1)}-${String.format("%02d", dayOfMonth)}"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    val startTimePickerDialog = remember {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                quotationViewModel.startHour = "${String.format("%02d", hour)}:${String.format("%02d", minute)}:00"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    val endTimePickerDialog = remember {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                quotationViewModel.endHour = "${String.format("%02d", hour)}:${String.format("%02d", minute)}:00"
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Generar Cotización") })
        }
    ) { paddingValues ->
        if (quotationViewModel._museumBasePrice == 0.0 && selectedMuseumId != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Cargando detalles del museo...",
                    modifier = Modifier.padding(top = 80.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Completa los detalles para tu cotización",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                item {
                    var museumDropdownExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = museumDropdownExpanded,
                        onExpandedChange = { museumDropdownExpanded = !museumDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = museums.find { it.id == quotationViewModel.selectedMuseumId }?.nombre ?: "Selecciona un museo",
                            onValueChange = { /* Solo lectura */ },
                            readOnly = true,
                            label = { Text("Museo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = museumDropdownExpanded) },
                            leadingIcon = { Icon(Icons.Default.Group, contentDescription = "Museo") },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = museumDropdownExpanded,
                            onDismissRequest = { museumDropdownExpanded = false }
                        ) {
                            museums.forEach { museum ->
                                DropdownMenuItem(
                                    text = { Text(museum.nombre) },
                                    onClick = {
                                        quotationViewModel.setInitialMuseumId(museum.id)
                                        museumDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = quotationViewModel.appointmentDate,
                        onValueChange = { /* No permitir edición directa */ },
                        label = { Text("Fecha de la Cita (YYYY-MM-DD)") },
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Fecha") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() }
                    )
                }

                item {
                    OutlinedTextField(
                        value = quotationViewModel.startHour,
                        onValueChange = { /* No permitir edición directa */ },
                        label = { Text("Hora de Inicio (HH:MM:SS)") },
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = "Hora de Inicio") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { startTimePickerDialog.show() }
                    )
                }

                item {
                    OutlinedTextField(
                        value = quotationViewModel.endHour,
                        onValueChange = { /* No permitir edición directa */ },
                        label = { Text("Hora de Fin (HH:MM:SS)") },
                        readOnly = true,
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = "Hora de Fin") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { endTimePickerDialog.show() }
                    )
                }

                item {
                    OutlinedTextField(
                        value = quotationViewModel.totalPeopleGeneral,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                quotationViewModel.totalPeopleGeneral = newValue
                            }
                        },
                        label = { Text("Personas Generales (Sin Descuento)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Group, contentDescription = "Personas Generales") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                itemsIndexed(quotationViewModel.discountedPeopleGroups) { index, group ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Grupo con Descuento #${index + 1}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                // CORRECCIÓN: Permitir eliminar si hay más de 1 grupo,
                                // o si es el único y el usuario quiere limpiar el campo (aunque se añade uno vacío por defecto)
                                if (quotationViewModel.discountedPeopleGroups.size > 1 || (index == 0 && quotationViewModel.discountedPeopleGroups.size == 1 && (group.count.toIntOrNull() ?: 0) == 0 && group.discount == null)) {
                                    IconButton(
                                        onClick = { quotationViewModel.removeDiscountedPeopleGroup(group) }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Eliminar grupo")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = group.count,
                                onValueChange = { newValue ->
                                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                        quotationViewModel.updateDiscountedPeopleGroupCount(group, newValue)
                                    }
                                },
                                label = { Text("Cantidad de Personas") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                leadingIcon = { Icon(Icons.Default.Group, contentDescription = "Cantidad") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            var discountDropdownExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = discountDropdownExpanded,
                                onExpandedChange = { discountDropdownExpanded = !discountDropdownExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = group.discount?.let {
                                        val discountValue = it.valorDescuento.toDoubleOrNull() ?: 0.0
                                        val displayPercentage = (discountValue * 100).roundToInt()
                                        "${it.descripcionAplicacion} ($displayPercentage%)"
                                    } ?: "Selecciona un descuento",
                                    onValueChange = { /* Solo lectura */ },
                                    readOnly = true,
                                    label = { Text("Tipo de Descuento") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = discountDropdownExpanded) },
                                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "Descuento") },
                                    modifier = Modifier
                                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                                        .fillMaxWidth()
                                )

                                ExposedDropdownMenu(
                                    expanded = discountDropdownExpanded,
                                    onDismissRequest = { discountDropdownExpanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Sin Descuento Específico") },
                                        onClick = {
                                            quotationViewModel.updateDiscountedPeopleGroupDiscount(group, null)
                                            discountDropdownExpanded = false
                                        }
                                    )
                                    availableDiscounts.forEach { discount ->
                                        DropdownMenuItem(
                                            text = {
                                                val discountValue = discount.valorDescuento.toDoubleOrNull()
                                                    ?: 0.0
                                                val displayPercentage = (discountValue * 100).roundToInt()
                                                Text("${discount.descripcionAplicacion} ($displayPercentage%)")
                                            },
                                            onClick = {
                                                quotationViewModel.updateDiscountedPeopleGroupDiscount(group, discount)
                                                discountDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { quotationViewModel.addDiscountedPeopleGroup() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = selectedMuseumId != null && availableDiscounts.isNotEmpty()
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Agregar Grupo con Descuento")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Agregar Grupo con Descuento")
                    }
                }

                item {
                    OutlinedTextField(
                        value = quotationViewModel.totalInfants,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                quotationViewModel.totalInfants = newValue
                            }
                        },
                        label = { Text("Total de Infantes") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.ChildCare, contentDescription = "Infantes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = quotationViewModel.calculatedTotalPeople.toString(),
                        onValueChange = { /* Solo lectura */ },
                        readOnly = true,
                        label = { Text("Total de Personas (Calculado)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = "Total de Personas") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = String.format("%.2f", quotationViewModel.calculatedPriceTotal),
                        onValueChange = { /* Solo lectura */ },
                        readOnly = true,
                        label = { Text("Precio Total (Calculado)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Precio Total") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Button(
                        onClick = { quotationViewModel.createQuotation() },
                        enabled = !isLoading && selectedMuseumId != null && quotationViewModel._museumBasePrice != 0.0 && quotationViewModel.calculatedTotalPeople > 0,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isLoading) "Generando..." else "Generar Cotización")
                    }
                }

                item {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
            LaunchedEffect(quotationResponse) {
                quotationResponse?.let { response ->
                    if (response.cotizacion != null) {
                        navController.navigate("quotation_details_screen") {
                            popUpTo("quotation_screen") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}