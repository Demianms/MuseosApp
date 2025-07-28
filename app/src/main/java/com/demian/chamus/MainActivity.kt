package com.demian.chamus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.demian.chamus.screens.QuotationDetailsScreen
import com.demian.chamus.screens.QuotationScreen
import com.demian.chamus.screens.SearchedQuotationDisplayScreen
import com.demian.chamus.screens.museums.DetailsMuseums
import com.demian.chamus.screens.museums.DetailsRoom
import com.demian.chamus.screens.museums.ListMuseumsScreen
import com.demian.chamus.screens.splash.SplashScreen
import com.demian.chamus.ui.theme.ChamusTheme
import com.demian.chamus.viewmodel.MuseumViewModel
import com.demian.chamus.viewmodel.QuotationViewModel // Importa QuotationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChamusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(activityViewModelStoreOwner = this)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(activityViewModelStoreOwner: ComponentActivity) {
    val navController = rememberNavController()
    val museumViewModel: MuseumViewModel = viewModel()

    // Inicializa QuotationViewModel aquí. Esta es la instancia compartida.
    val quotationViewModel: QuotationViewModel = viewModel(
        viewModelStoreOwner = activityViewModelStoreOwner
    )

    NavHost(
        navController = navController,
        startDestination = "splash_screen"
    ) {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("list_museums_screen") {
            ListMuseumsScreen(navController = navController)
        }
        composable(
            "museum_detail/{museumId}",
            arguments = listOf(navArgument("museumId") { type = NavType.IntType })
        ) { backStackEntry ->
            val museumId = backStackEntry.arguments?.getInt("museumId") ?: 0
            DetailsMuseums(
                museumId = museumId,
                navController = navController,
                // CORRECCIÓN: Si el parámetro en DetailsMuseums se llama 'quotationViewModel',
                // y la variable local también, basta con poner el nombre de la variable.
                // Si el parámetro se llama diferente (ej. 'viewModel'), entonces sería 'viewModel = quotationViewModel'.
                // Asumo que se llama 'quotationViewModel' en DetailsMuseums también.
                quotationViewModel = quotationViewModel
            )
        }
        composable(
            "room_detail/{roomId}?museumId={museumId}",
            arguments = listOf(
                navArgument("roomId") { type = NavType.IntType },
                navArgument("museumId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getInt("roomId") ?: 0
            val museumId = backStackEntry.arguments?.getInt("museumId") ?: 0

            LaunchedEffect(museumId) {
                museumViewModel.loadMuseumDetails(museumId)
            }

            val selectedMuseum by museumViewModel.selectedMuseum.collectAsState()
            val room = selectedMuseum?.rooms?.find { it.id == roomId }

            if (room != null) {
                DetailsRoom(
                    room = room,
                    navController = navController
                )
            } else {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Text(
                        text = "Sala no encontrada",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        composable(
            "quotation_screen?museumId={museumId}",
            arguments = listOf(navArgument("museumId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val museumId = backStackEntry.arguments?.getInt("museumId")
            val initialMuseumId = if (museumId != null && museumId != -1) museumId else null
            QuotationScreen(
                navController = navController,
                initialMuseumId = initialMuseumId,
                quotationViewModel = quotationViewModel
            )
        }

        composable("quotation_details_screen") {
            val currentQuotation by quotationViewModel.quotationResponse.collectAsState()

            QuotationDetailsScreen(
                navController = navController,
                quotationResponse = currentQuotation
            )
        }
        // Ruta para la cotización buscada
        composable("searched_quotation_display_screen") {
            SearchedQuotationDisplayScreen(
                navController = navController,
                quotationViewModel = quotationViewModel
            )
        }
    }
}