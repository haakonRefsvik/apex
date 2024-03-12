package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.model.details.WeatherDetails
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherForeCastLocationRepo
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Data
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation() {

    val navController = rememberNavController()
    val forecastRepo = WeatherForeCastLocationRepo()
    val detailsScreenViewModel = DetailsScreenViewModel(forecastRepo)
    val homeScreenViewModel = HomeScreenViewModel(forecastRepo)

    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("HomeScreen") {
            HomeScreen(
                navController,
                homeScreenViewModel = homeScreenViewModel

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
    }
}