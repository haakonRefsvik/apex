package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.model.details.WeatherDetails
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "HomeScreen") {
        composable("HomeScreen") {
            HomeScreen(
                navController,
            )
        }
        composable(
            "DetailsScreen/{weatherdata}",
            arguments = listOf(navArgument("weatherdata") { type = DataArgType() })
        ) { backStackEntry ->
            val weatherdata = backStackEntry.arguments?.getString("weatherdata")
                ?.let { Gson().fromJson(it, WeatherDetails::class.java) }
            backStackEntry.arguments?.let {
                DetailsScreen(
                    navController,
                    weatherdata
                )
            }
        }
    }
}