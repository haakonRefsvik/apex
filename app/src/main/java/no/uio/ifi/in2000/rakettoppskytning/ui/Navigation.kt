package no.uio.ifi.in2000.rakettoppskytning.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecs
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.RocketSpecsEvent
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdState
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.ThresholdsEvent
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    state: FavoriteState,
    onEvent: (FavoriteEvent) -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
    settingsViewModel: SettingsViewModel,
    weatherRepo: WeatherRepository,
    detailsScreenViewModel: DetailsScreenViewModel,
    context: Context,
    favoriteCardViewModel: FavoriteCardViewModel,
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
                settingsViewModel,
                detailsScreenViewModel,
                context
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
                    detailsScreenViewModel = detailsScreenViewModel,
                    favoriteCardViewModel = favoriteCardViewModel,
                    context = context,
                    homeScreenViewModel = homeScreenViewModel,
                    mapViewModel = mapViewModel,
                    onEvent = onEvent,
                    state = state
                )
            }
        }
        composable("SettingsScreen") {
            SettingsScreen(
                navController,
                settingsViewModel,
                weatherRepo,
                homeScreenViewModel,
                mapViewModel,
            )
        }
        composable("FavoriteCardScreen") {
            FavoriteCardScreen(
                navController,
                favoriteCardViewModel,
                homeScreenViewModel
            )
        }
    }
}