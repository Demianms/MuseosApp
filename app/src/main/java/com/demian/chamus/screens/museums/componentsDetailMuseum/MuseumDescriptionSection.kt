package com.demian.chamus.screens.museums.componentsDetailMuseum

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MuseumDescriptionSection(description: String, modifier: Modifier = Modifier) {
    SectionCard(
        icon = Icons.Default.Info,
        title = "Descripción",
        modifier = modifier
    ) {
        Column {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}