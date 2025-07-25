package com.demian.chamus.screens.museums.componentsDetailMuseum

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MuseumMapSection(
    museumName: String,
    latitude: Double?,
    longitude: Double?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val defaultLat = 19.4326 // Puedes mover estos valores a constantes si se usan mucho
    val defaultLng = -99.1332

    val museumLat = latitude ?: defaultLat
    val museumLng = longitude ?: defaultLng

    val museumLocation = LatLng(museumLat, museumLng)

    val mapCameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(museumLocation, 16f)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Ubicación del museo",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.matchParentSize(),
                    cameraPositionState = mapCameraPosition,
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        zoomGesturesEnabled = true
                    )
                ) {
                    Marker(
                        state = MarkerState(position = museumLocation),
                        title = museumName,
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Toca para ver en Maps",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        OutlinedButton(
            onClick = {
                val gmmIntentUri =
                    "geo:${museumLat},${museumLng}?q=${Uri.encode(museumName)}".toUri()
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                try {
                    context.startActivity(mapIntent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, "Instala Google Maps", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Abrir en Google Maps")
        }
    }
}