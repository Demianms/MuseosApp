package com.demian.chamus.screens.museums.componentsDetailMuseum

import android.annotation.SuppressLint
import com.demian.chamus.models.Discount
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@SuppressLint("DefaultLocale")
@Composable
fun MuseumDiscountsSection(discounts: List<Discount>, modifier: Modifier = Modifier) {
    if (discounts.isNotEmpty()) {
        SectionCard(
            icon = Icons.Default.MoreVert,
            title = "Descuentos Disponibles",
            modifier = modifier
        ) {
            Column {
                discounts.forEach { discount ->
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        // Asegúrate de que valorDescuento sea un Float para este formato
                        Text(
                            text = "Descuento: ${String.format("%.0f%%", discount.valorDescuento.toFloat() * 100)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        discount.descripcionAplicacion?.let { description ->
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } ?: Text(
                            text = "Sin descripción adicional",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    } else {
        Text(
            text = "No hay descuentos disponibles",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}