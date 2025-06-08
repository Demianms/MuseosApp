package com.demian.chamus.screens.museums

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

private val ColorFondoApp = Color(0xFFE8F0F7)
private val ColorFondoEncabezado = Color(0xFFFFFFFF)
private val ColorAzulOscuroPrincipal = Color(0xFF003366)
private val ColorGrisTarjetaInactiva = Color(0xFFB0B0B0)
private val ColorAzulMedioEtiqueta = Color(0xFF4D6D9A)
private val ColorAzulClaroEtiqueta = Color(0xFF66CCFF)
private val ColorAzulMuyOscuroEtiqueta = Color(0xFF001A33)
private val ColorVerdeActivo = Color(0xFF4CAF50)
private val ColorRojoInactivo = Color(0xFFCC0000)
private val ColorTextoSobreOscuro = Color(0xFFFFFFFF)
private val ColorTextoSobreClaro = Color(0xFF000000)
private val ColorAzulCieloInfo = Color(0xFF87CEEB)

@Composable
fun ListMuseumsScreen() {
    val museums = remember {
        listOf(
            MuseumData("Museo Nacional de Arte", false, "https://media.admagazine.com/photos/618a6312532cae908aaf2ba5/master/w_1600%2Cc_limit/78769.jpg"),
            MuseumData("Museo de Historia Natural", true, "https://media.admagazine.com/photos/618a6312532cae908aaf2ba5/master/w_1600%2Cc_limit/78769.jpg"),
        )
    }

    Scaffold(
        containerColor = ColorFondoApp,
        contentColor = ColorTextoSobreClaro,
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ColorFondoEncabezado)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Encuentra tu museo favorito",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ColorAzulOscuroPrincipal,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Filter("Arte", ColorAzulOscuroPrincipal)
                        Spacer(Modifier.width(8.dp))
                        Filter("Historia", ColorAzulMedioEtiqueta)
                        Spacer(Modifier.width(8.dp))
                        Filter("Cultura", ColorAzulClaroEtiqueta)
                        Spacer(Modifier.width(8.dp))
                        Filter("Todos", ColorAzulMuyOscuroEtiqueta)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    museums.forEach { museum ->
                        MuseumCard(
                            name = museum.name,
                            isActive = museum.isActive,
                            imageUrl = museum.imageUrl,
                            onClick = { }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = ColorAzulCieloInfo,
                        contentColor = ColorTextoSobreOscuro
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "No hay más museos por mostrar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ColorTextoSobreOscuro
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    )
}

data class MuseumData(
    val name: String,
    val isActive: Boolean,
    val imageUrl: String? = null
)

@Composable
fun MuseumCard(
    name: String,
    isActive: Boolean,
    imageUrl: String?,
    onClick: () -> Unit
) {
    val cardBackgroundColor = if (isActive) ColorAzulOscuroPrincipal else ColorGrisTarjetaInactiva
    val textColor = ColorTextoSobreOscuro
    val statusTagColor = if (isActive) ColorVerdeActivo else ColorRojoInactivo

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 4.dp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (imageUrl != null) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Imagen de fondo del museo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(cardBackgroundColor)
                )
            }


            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusTagColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) "ACTIVO" else "INACTIVO",
                        color = ColorTextoSobreOscuro,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = name,
                    color = textColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun Filter(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = ColorFondoEncabezado,
        border = BorderStroke(1.dp, color),
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}