package no.uio.ifi.in2000.rakettoppskytning.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.R.string
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
import no.uio.ifi.in2000.rakettoppskytning.model.details.WeatherDetails
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme


abstract class JsonNavType<T> : NavType<T>(isNullableAllowed = false) {
    abstract fun fromJsonParse(value: String): T
    abstract fun T.getJsonParse(): String

    override fun get(bundle: Bundle, key: String): T? =
        bundle.getString(key)?.let { parseValue(it) }

    override fun parseValue(value: String): T = fromJsonParse(value)

    override fun put(bundle: Bundle, key: String, value: T) {
        bundle.putString(key, value.getJsonParse())
    }
}

class DataArgType : JsonNavType<WeatherDetails>() {
    override fun fromJsonParse(value: String): WeatherDetails =
        Gson().fromJson(value, WeatherDetails::class.java)

    override fun WeatherDetails.getJsonParse(): String = Gson().toJson(this)
}

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ApiKeyHolder.in2000ProxyKey = resources.getString(string.in2000ProxyKey)
        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
            }
        }
    }
}
