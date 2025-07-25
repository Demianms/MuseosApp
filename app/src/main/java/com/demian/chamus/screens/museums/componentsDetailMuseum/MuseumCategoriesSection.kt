package com.demian.chamus.screens.museums.componentsDetailMuseum

import com.demian.chamus.models.Category
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MuseumCategoriesSection(categories: List<Category>, modifier: Modifier = Modifier) {
    SectionCard(
        icon = Icons.Default.Info,
        title = "Categorías",
        modifier = modifier
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                Box(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = category.nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}