package no.uio.ifi.in2000.rakettoppskytning.ui

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.ExperimentalCoroutinesApi
import no.uio.ifi.in2000.rakettoppskytning.data.database.AppDatabase
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteCardDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.FavoriteDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.RocketSpecsDao
import no.uio.ifi.in2000.rakettoppskytning.data.database.ThresholdsDao
import no.uio.ifi.in2000.rakettoppskytning.data.favoriteCards.FavoriteCardRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherRepository
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.data.settings.SettingsRepository
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun Navigation( context: MainActivity) {

    val db = AppDatabase.getInstance(context)

    val thresholdsDao = db.thresholdsDao
    val rocketSpecsDao = db.rocketSpecsDao
    val favoriteCardDao = db.favoriteCardDao
    val favoriteDao = db.favoriteDao

    val gribRepository = GribRepository()
    val settingsRepository = SettingsRepository(thresholdsDao, rocketSpecsDao)
    val weatherRepo = WeatherRepository(settingsRepository, gribRepository)
    val favoriteCardRepository = FavoriteCardRepository(favoriteCardDao, favoriteDao)

    val homeScreenViewModel = HomeScreenViewModel(weatherRepo, favoriteCardRepository)
    homeScreenViewModel.initialize()
    val detailsScreenViewModel = DetailsScreenViewModel(weatherRepo)
    val settingsViewModel = SettingsViewModel(settingsRepository)
    val favoriteCardViewModel = FavoriteCardViewModel(weatherRepo, favoriteCardRepository)
    val mapViewModel = MapViewModel()

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