package com.demian.chamus.screens.museums.componentsDetailMuseum

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MuseumHoursAndPricesSection(
    openingTime: String?,
    closingTime: String?,
    price: String,
    modifier: Modifier = Modifier
) {
    SectionCard(
        icon = Icons.Default.DateRange,
        title = "Horarios y Precios",
        modifier = modifier
    ) {
        Column {
            Text(
                text = "Horarios de Operación:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Apertura: ${openingTime ?: "No disponible"}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Cierre: ${closingTime ?: "No disponible"}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "Precios de Entrada:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Entrada general: $${price ?: "No disponible"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}