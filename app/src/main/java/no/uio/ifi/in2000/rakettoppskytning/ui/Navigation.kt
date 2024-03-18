package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherForeCastLocationRepo
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {

    val navController = rememberNavController()
    val thresholdRepository = ThresholdRepository()
    val forecastRepo = WeatherForeCastLocationRepo(thresholdRepository)

    val detailsScreenViewModel = DetailsScreenViewModel(forecastRepo)
    val homeScreenViewModel = HomeScreenViewModel(forecastRepo)
    val mapViewModel = MapViewModel()
    val thresholdViewModel = ThresholdViewModel(thresholdRepository)

    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("HomeScreen") {
            HomeScreen(
                navController,
                homeScreenViewModel = homeScreenViewModel,
                mapViewModel,
                thresholdViewModel
            )
        }
        composable(
            "DetailsScreen/{Tid}",
            arguments = listOf(navArgument("Tid") { type = NavType.StringType })
        ) { backStackEntry ->
            val data = backStackEntry.arguments?.getString("Tid")
            backStackEntry.arguments?.let {
                DetailsScreen(
                    navController = navController,
                    backStackEntry = data,
                    detailsScreenViewModel = detailsScreenViewModel
                )
            }
        }
        composable("ThresholdScreen") {
            ThresholdScreen(
                navController,
                thresholdViewModel
            )
        }
    }
}