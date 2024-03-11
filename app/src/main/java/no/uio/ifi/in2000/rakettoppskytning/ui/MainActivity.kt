package no.uio.ifi.in2000.rakettoppskytning.ui

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.RakettoppskytningTheme
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.R.*
import no.uio.ifi.in2000.rakettoppskytning.data.ApiKeyHolder
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
                                context = application
                            )
                        }
                        composable(
                            "DetailsScreen/{details}",
                            arguments = listOf(navArgument("details") { type = DetailsArgType() })
                        ) { backStackEntry ->
                            val details = backStackEntry.arguments?.getString("details")
                                ?.let { Gson().fromJson(it, Details::class.java) }
                            backStackEntry.arguments?.let { DetailsScreen(navController, details) }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun bark() {

    val details = (Details(
        airPressureAtSeaLevel = 1015.5,
        airTemperature = 4.4,
        airTemperaturePercentile10 = 4.4,
        airTemperaturePercentile90 = 4.4,
        cloudAreaFraction = 97.4,
        cloudAreaFractionHigh = 96.1,
        cloudAreaFractionLow = 27.4,
        cloudAreaFractionMedium = 0.0,
        dewPointTemperature = -1.3,
        fogAreaFraction = 0.0,
        relativeHumidity = 67.4,
        ultravioletIndexClearSky = 0.1,
        windFromDirection = 270.0,
        windSpeed = 7.8,
        windSpeedOfGust = 10.9,
        windSpeedPercentile10 = 6.0,
        windSpeedPercentile90 = 8.5
    ))
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(150.dp))
        ElevatedCard(

            modifier = Modifier
                .height(140.dp)
                .width(340.dp)
        ) {
            Row {
                Column {
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(10.dp)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            modifier = Modifier
                                .size(30.dp),
                            painter = painterResource(drawable.vind2),
                            contentDescription = "VindSymbol"
                        )


                    }
                    Spacer(
                        modifier = Modifier
                            .height(21.dp)
                            .width(200.dp)
                    )
                    Text(text = "${details.windSpeed} m/s vind")

                    Spacer(
                        modifier = Modifier
                            .height(0.3.dp)
                            .width(200.dp)
                            .background(MaterialTheme.colorScheme.onBackground)

                    )
                    Text(text = "${details.windSpeedOfGust} m/s vindkast")

                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "N", modifier = Modifier.padding(bottom = 60.dp))
                        Text(text = "S", modifier = Modifier.padding(top = 60.dp))

                        Text(text = "V", modifier = Modifier.padding(end = 60.dp))
                        Text(text = "Ø", modifier = Modifier.padding(start = 60.dp))
                        Icon(
                            modifier = Modifier
                                .width(50.dp)
                                .rotate(270.0F + details.windFromDirection.toFloat()),
                            painter = painterResource(drawable.kompasspil),
                            contentDescription = "kompasspil"
                        )

                        Icon(
                            painter = painterResource(drawable.kompass),
                            contentDescription = "Kompass",
                            modifier = Modifier.size(100.dp)
                        )
                        Icon(
                            modifier = Modifier
                                .width(50.dp)
                                .rotate(270.0F + details.windFromDirection.toFloat()),
                            painter = painterResource(drawable.kompasspil),
                            contentDescription = "kompasspil"
                        )


                    }

                }


            }


        }
        Spacer(modifier = Modifier.height(30.dp))
        Row {
            LazyColumn(content = {
                item {


                    ElevatedCard(
                        modifier = Modifier
                            .height(125.dp)
                            .width(150.dp)
                    ) {
                        Column {
                            Icon(
                                modifier = Modifier
                                    .width(50.dp),
                                painter = painterResource(drawable.temp),
                                contentDescription = "Temperatursymbol"
                            )
                            Text(
                                text = "${details.airTemperature} ℃",
                                modifier = Modifier.padding(start = 10.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }


                    }
                    Spacer(modifier = Modifier.height(30.dp))

                    ElevatedCard(
                        modifier = Modifier
                            .height(125.dp)
                            .width(150.dp)
                    ) {
                        Column {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp),
                                painter = painterResource(drawable.trykk),
                                contentDescription = "Trykk"
                            )
                            Text(
                                text = "${details.airPressureAtSeaLevel} hPa",
                                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }


                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    ElevatedCard(
                        modifier = Modifier
                            .height(125.dp)
                            .width(150.dp)
                    ) {
                        Column {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp),
                                painter = painterResource(drawable.eye),
                                contentDescription = "Øye/sikt"
                            )
                            Text(
                                text = "${details.fogAreaFraction} %",
                                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }


                    }


                }
            })
            Spacer(modifier = Modifier.width(40.dp))


            LazyColumn(content = {
                item {
                    ElevatedCard(
                        modifier = Modifier
                            .height(125.dp)
                            .width(150.dp)
                    ) {
                        Column {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp),
                                painter = painterResource(drawable.luftfuktighet),
                                contentDescription = "Luftfuktighet"
                            )
                            Text(
                                text = "${details.relativeHumidity}%",
                                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }


                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    ElevatedCard(
                        modifier = Modifier
                            .height(125.dp)
                            .width(150.dp)
                    ) {
                        Column {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp),
                                painter = painterResource(drawable.fogsymbol),
                                contentDescription = "Tåke"
                            )
                            Text(
                                text = "${details.fogAreaFraction}%",
                                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }


                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    ElevatedCard(
                        modifier = Modifier
                            .height(125.dp)
                            .width(150.dp)
                    ) {
                        Column {
                            Icon(
                                modifier = Modifier
                                    .width(30.dp),
                                painter = painterResource(drawable.vann),
                                contentDescription = "Vann"
                            )
                            Text(
                                text = "N/A mm",
                                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                        }


                    }

                }
            })


        }
    }

}