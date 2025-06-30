package com.demian.chamus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.demian.chamus.screens.museums.DetailsMuseums
import com.demian.chamus.screens.museums.DetailsRoom
import com.demian.chamus.screens.museums.ListMuseumsScreen
import com.demian.chamus.screens.splash.SplashScreen
import com.demian.chamus.ui.theme.ChamusTheme
import com.demian.chamus.viewmodel.MuseumViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChamusTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val viewModel: MuseumViewModel = viewModel()

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
                navController = navController
            )
        }
        composable(
            "room_detail/{roomId}",
            arguments = listOf(navArgument("roomId") { type = NavType.IntType })
        ) { backStackEntry ->
            val roomId = backStackEntry.arguments?.getInt("roomId") ?: 0
            val room = viewModel.selectedMuseum.value?.rooms?.find { it.id == roomId }

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
    }
}
