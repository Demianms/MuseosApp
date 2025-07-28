package com.demian.chamus.screens.museums.componentsDetailMuseum

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row // Necesitas importar Row si la usas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth // Necesitas importar fillMaxWidth si la usas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card // Importa Card
import androidx.compose.material3.CardDefaults // Importa CardDefaults
import androidx.compose.material3.Icon // Importa Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment // Importa Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight // Importa FontWeight si lo usas
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun MuseumMoreInfoSection(museumUrl: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    if (museumUrl.isNullOrBlank()) {
        return
    }

    // COMENTA ESTO TEMPORALMENTE: SectionCard(...) {
    // Y DESCOMENTA ESTO ABAJO:
    Card( // Reemplaza SectionCard con un Card básico para la prueba
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { // Añade padding al Column
            Row(verticalAlignment = Alignment.CenterVertically) { // Para el icono y título
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary // Asegura el color del icono
                )
                Spacer(modifier = Modifier.width(8.dp)) // Añade este si no lo tienes
                Text(
                    text = "Más Información",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold // Si quieres negrita en el título
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // Espacio entre título y contenido

            Text(
                text = "Sitio web:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = museumUrl,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                ),
                modifier = Modifier.clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(museumUrl))
                        val chooserIntent = Intent.createChooser(intent, "Abrir con...")

                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(chooserIntent)
                        } else {
                            Toast.makeText(
                                context,
                                "No hay aplicaciones disponibles para abrir este enlace.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "URL inválida o error al abrir: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}