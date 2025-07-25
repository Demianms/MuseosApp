package com.demian.chamus.screens.museums.componentsDetailMuseum

import com.demian.chamus.models.Room
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RoomCard(
    room: Room,
    museumId: Int,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable {
                navController.navigate("room_detail/${room.id}?museumId=$museumId")
            },
        shape = MaterialTheme.shapes.medium,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column( // Puedes envolver el contenido en una Column si no lo está ya, para consistencia.
            modifier = Modifier.padding(16.dp)
        ) {
            // El AsyncImage de la sala está comentado, si lo activas, asegúrate de que Room.imagen sea el tipo correcto
            /*
            AsyncImage(
                model = room.imagen,
                contentDescription = "Imagen de ${room.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Spacer(modifier = Modifier.height(12.dp))
            */

            Text(
                text = room.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = room.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1 // Podrías querer más líneas o elipsis si la descripción es larga
            )
        }
    }
}