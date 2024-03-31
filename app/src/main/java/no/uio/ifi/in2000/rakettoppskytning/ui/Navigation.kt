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
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.network.InternetConnectionViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
    thresholdViewModel: ThresholdViewModel,
    weatherRepo: WeatherRepository,
    detailsScreenViewModel: DetailsScreenViewModel,
    internetConnectionViewModel: InternetConnectionViewModel
) {

    val navController = rememberNavController()


    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("HomeScreen") {
            HomeScreen(
                navController,
                homeScreenViewModel = homeScreenViewModel,
                state,
                onEvent,
                mapViewModel,
                thresholdViewModel,
                internetConnectionViewModel
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
                thresholdViewModel,
                weatherRepo
            )
        }
    }
}