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
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.ui.details.DetailsScreen
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreen


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

class DetailsArgType : JsonNavType<Details>() {
    override fun fromJsonParse(value: String): Details = Gson().fromJson(value, Details::class.java)

    override fun Details.getJsonParse(): String = Gson().toJson(this)
}
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RakettoppskytningTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "HomeScreen") {
                        composable("HomeScreen") { HomeScreen(navController) }
                        composable(
                            "DetailsScreen/{details}",
                            arguments = listOf(navArgument("details") { type = DetailsArgType() })
                        ) { backStackEntry ->
                            val details = backStackEntry.arguments?.getString("details")?.let { Gson().fromJson(it, Details::class.java) }
                            backStackEntry.arguments?.let { DetailsScreen(navController, details) }
                        }
                    }



                }
            }
        }
    }
}
