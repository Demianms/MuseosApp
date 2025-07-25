package com.demian.chamus.screens.museums.componentsDetailMuseum

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
fun MuseumMoreInfoSection(museumUrl: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    SectionCard(
        icon = Icons.Default.Info,
        title = "Más Información",
        modifier = modifier
    ) {
        Column {
            Text(
                text = "Sitio web: $museumUrl",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, museumUrl!!.toUri())
                        val chooserIntent = Intent.createChooser(intent, "Abrir con...")
                        if (intent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(chooserIntent)
                        } else {
                            Toast.makeText(
                                context,
                                "No hay aplicaciones disponibles para abrir este enlace",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "URL inválida o error al abrir: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }
}