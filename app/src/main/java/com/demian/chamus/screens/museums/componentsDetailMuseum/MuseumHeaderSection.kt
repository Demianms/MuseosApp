package com.demian.chamus.screens.museums.componentsDetailMuseum

import com.demian.chamus.models.Museum
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MuseumHeaderSection(museum: Museum, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        AsyncImage(
            model = museum.imagen,
            contentDescription = "Imagen de ${museum.nombre}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp)
        ) {
            Text(
                text = museum.nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (museum.estado == "active") Color.Green.copy(alpha = 0.6f)
                            else Color.Red.copy(alpha = 0.6f),
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (museum.estado == "active") "ACTIVO" else "INACTIVO",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}