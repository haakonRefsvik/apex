package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.details.WeatherDetails
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Data
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.grib.getVerticalProfileMap
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun getVerticalProfileNearestHour(allVp: List<VerticalProfile>, time: String): VerticalProfile? {
    var r: VerticalProfile? = null

    allVp.forEach breaking@{ vp ->
        if (vp.time <= time) {
            r = vp
            return@breaking
        }
    }
    return r
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,
    backStackEntry: String?,
    detailsScreenViewModel: DetailsScreenViewModel
) {
    val verticalProfileUiState by detailsScreenViewModel.verticalProfileUiState.collectAsState()
    val foreCastUiState by detailsScreenViewModel.foreCastUiState.collectAsState()
    var data: List<Data> = listOf()
    val time: String = backStackEntry ?: ""
    val verticalProfile =
        getVerticalProfileNearestHour(verticalProfileUiState.verticalProfiles, time)

    foreCastUiState.foreCast.forEach {
        it.properties.timeseries.forEach {series ->
            if (time == series.time) {
                data = listOf(series.data)
                verticalProfile?.addGroundInfo(series)
            }
        }
    }

    data.forEach { it.instant }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Sharp.Menu,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "ArrowBack"
                        )
                    }
                },
                title = {
                    ClickableText(
                        text = AnnotatedString(
                            text = "",
                            spanStyle = SpanStyle(
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 15.sp
                            )
                        ),
                        onClick = { navController.navigateUp() },
                    )
                },
            )
        },
        bottomBar = {
            BottomAppBar() {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(

                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location"
                        )
                    }
                    Spacer(modifier = Modifier.width(94.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(R.drawable.rakket),
                            contentDescription = "Rakket"
                        )
                    }
                    Spacer(modifier = Modifier.width(95.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Settings"
                        )
                    }
                }
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (data.isEmpty()) {
                Text("Her var det tomt")
            }

            ElevatedCard(

                modifier = Modifier
                    .height(140.dp)
                    .width(340.dp)
            ) {
                data.forEach {
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
                                    painter = painterResource(R.drawable.vind2),
                                    contentDescription = "VindSymbol"
                                )


                            }
                            Spacer(
                                modifier = Modifier
                                    .height(21.dp)
                                    .width(200.dp)
                            )
                            Text(text = "Max Shearwind")

                            Spacer(
                                modifier = Modifier
                                    .height(0.3.dp)
                                    .width(200.dp)
                                    .background(MaterialTheme.colorScheme.onBackground)
                            )
                            val maxSheerWind: String = String.format("%.2f", verticalProfile?.getMaxSheerWind()?.windSpeed)
                            val lowerLayerHeight = verticalProfile?.getMaxSheerWind()?.lowerLayer?.getLevelHeightInMeters()?.toInt()
                            val upperLayerHeight = verticalProfile?.getMaxSheerWind()?.upperLayer?.getLevelHeightInMeters()?.toInt()

                            Text(text = "$maxSheerWind m/s")
                            Text(text = "$lowerLayerHeight - $upperLayerHeight meters høyde")

                        }
                    }


                }


            }
            data.forEach {
                Spacer(modifier = Modifier.height(20.dp))
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
                                    painter = painterResource(R.drawable.vind2),
                                    contentDescription = "VindSymbol"
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .height(21.dp)
                                    .width(200.dp)
                            )
                            Text(text = "${it.instant.details.windSpeed} m/s vind")

                            Spacer(
                                modifier = Modifier
                                    .height(0.3.dp)
                                    .width(200.dp)
                                    .background(MaterialTheme.colorScheme.onBackground)
                            )
                            Text(text = "${it.instant.details.windSpeedOfGust} m/s vindkast")

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
                                        .rotate(270.0F + it.instant.details.windFromDirection.toFloat()),
                                    painter = painterResource(R.drawable.kompasspil),
                                    contentDescription = "kompasspil"
                                )

                                Icon(
                                    painter = painterResource(R.drawable.kompass),
                                    contentDescription = "Kompass",
                                    modifier = Modifier.size(100.dp)
                                )
                                Icon(
                                    modifier = Modifier
                                        .width(50.dp)
                                        .rotate(270.0F + it.instant.details.windFromDirection.toFloat()),
                                    painter = painterResource(R.drawable.kompasspil),
                                    contentDescription = "kompasspil"
                                )

                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
                Row(modifier = Modifier.padding(20.dp)) {
                    LazyColumn {
                        item {
                            Row {
                                AddWeatherCard(
                                    iconId = R.drawable.temp,
                                    desc = "Temperatur",
                                    text = "${it.instant.details.airTemperature} ℃"
                                )
                                Spacer(modifier = Modifier.width(40.dp))
                                AddWeatherCard(
                                    iconId = R.drawable.vann,
                                    desc = "nedbør",
                                    text = "${it.next6Hours?.details?.precipitationAmount} mm"
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))

                        }
                        item {
                            Row {
                                AddWeatherCard(
                                    iconId = R.drawable.eye,
                                    desc = "Sikt",
                                    text = "${it.instant.details.fogAreaFraction} %"
                                )
                                Spacer(modifier = Modifier.width(40.dp))
                                AddWeatherCard(
                                    iconId = R.drawable.luftfuktighet,
                                    desc = "Luftfuktighet",
                                    text = "${it.instant.details.relativeHumidity} %"
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                AddWeatherCard(
                                    iconId = R.drawable.fogsymbol,
                                    desc = "Skydekke",
                                    text = "${it.instant.details.cloudAreaFraction} %"
                                )

                                Spacer(modifier = Modifier.width(40.dp))
                                AddWeatherCard(
                                    iconId = R.drawable.vann,
                                    desc = "Duggpunkt",
                                    text = "Dewpoint\n ${it.instant.details.dewPointTemperature} ℃"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun AddWeatherCard(text: String, iconId: Int, desc: String, ) {
    ElevatedCard(
        modifier = Modifier
            .height(125.dp)
            .width(150.dp)
    ) {
        Column {
            Icon(
                modifier = Modifier
                    .width(30.dp),
                painter = painterResource(iconId),
                contentDescription = desc
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}
