package no.uio.ifi.in2000.rakettoppskytning.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.ExperimentalCoroutinesApi
import no.uio.ifi.in2000.rakettoppskytning.data.database.AppDatabase
import no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards.FavoriteCardRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsFactory
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteFactory
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeViewModelFactory
import no.uio.ifi.in2000.rakettoppskytning.ui.home.map.MapFactory
import no.uio.ifi.in2000.rakettoppskytning.ui.home.map.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsFactory
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel

/**
Sets up navigation and view models for the app's screens using Jetpack Compose's NavHost.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Navigation( context: MainActivity) {

    val db = AppDatabase.getInstance(context)

    val thresholdsDao = db.thresholdsDao
    val rocketSpecsDao = db.rocketSpecsDao
    val favoriteCardDao = db.favoriteCardDao
    val favoriteDao = db.favoriteDao

    val grbRepo = GribRepository()
    val setRepo = SettingsRepository(thresholdsDao, rocketSpecsDao)
    val favRepo = FavoriteCardRepository(favoriteCardDao, favoriteDao)
    val weaRepo = WeatherRepository(setRepo, grbRepo, favRepo)

    val homeScreenViewModel: HomeScreenViewModel = viewModel(factory = HomeViewModelFactory(weaRepo, favRepo))
    val detailsScreenViewModel: DetailsScreenViewModel = viewModel(factory = DetailsFactory(weaRepo))
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsFactory(setRepo))
    val favoriteCardViewModel: FavoriteCardViewModel = viewModel(factory = FavoriteFactory(weaRepo, favRepo))
    val mapViewModel: MapViewModel = viewModel(factory = MapFactory())

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("HomeScreen") {
            HomeScreen(
                navController,
                homeScreenViewModel = homeScreenViewModel,
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
                )
            }
        }
        composable("SettingsScreen") {
            SettingsScreen(
                navController,
                settingsViewModel,
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