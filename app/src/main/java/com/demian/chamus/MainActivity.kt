package com.demian.chamus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.demian.chamus.screens.museums.DetailsMuseums
import com.demian.chamus.screens.museums.ListMuseumsScreen
import com.demian.chamus.screens.splash.SplashScreen
import com.demian.chamus.screens.weather.ListMuseumsTest
import com.demian.chamus.ui.theme.ChamusTheme

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
    }
}