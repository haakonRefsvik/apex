package no.uio.ifi.in2000.rakettoppskytning.ui.details

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
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.details.WeatherDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,

    backStackEntry: WeatherDetails?,

    ) {

    val data: List<WeatherDetails> = if (backStackEntry != null) {
        listOf(backStackEntry)

    } else {
        listOf()
    }

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

            data.forEach {val ground = it.forecastData

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
                            Text(text = "${it.forecastData.instant.details.windSpeed} m/s vind")

                            Spacer(
                                modifier = Modifier
                                    .height(0.3.dp)
                                    .width(200.dp)
                                    .background(MaterialTheme.colorScheme.onBackground)
                            )
                            Text(text = "${it.forecastData.instant.details.windSpeedOfGust} m/s vindkast")

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
                                        .rotate(270.0F + it.forecastData.instant.details.windFromDirection.toFloat()),
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
                                        .rotate(270.0F + it.forecastData.instant.details.windFromDirection.toFloat()),
                                    painter = painterResource(R.drawable.kompasspil),
                                    contentDescription = "kompasspil")

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
                                    unit = "℃",
                                    iconId = R.drawable.trykk,
                                    desc = "Temperatur",
                                    value = ground.instant.details.airTemperature
                                )
                                Spacer(modifier = Modifier.width(40.dp))
                                AddWeatherCard(
                                    unit = "hPa",
                                    iconId = R.drawable.trykk,
                                    desc = "Trykk",
                                    value = ground.instant.details.airPressureAtSeaLevel
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))

                        }

                        item {
                            Row {
                                AddWeatherCard(
                                    unit = "%",
                                    iconId = R.drawable.eye,
                                    desc = "Sikt",
                                    value = ground.instant.details.cloudAreaFraction
                                )
                                Spacer(modifier = Modifier.width(40.dp))
                                AddWeatherCard(
                                    unit = "%",
                                    iconId = R.drawable.luftfuktighet,
                                    desc = "Luftfuktighet",
                                    value = ground.instant.details.relativeHumidity
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))

                        }
                        item {
                            Row {
                                AddWeatherCard(
                                    unit = "%",
                                    iconId = R.drawable.fogsymbol,
                                    desc = "Skydekke",
                                    value = ground.instant.details.cloudAreaFraction
                                )

                                Spacer(modifier = Modifier.width(40.dp))
                                AddWeatherCard(
                                    unit = "mm",
                                    iconId = R.drawable.vann,
                                    desc = "nedbør",
                                    value = ground.next6Hours?.details?.precipitationAmount
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }
                }
            }
        }
    }

}
@Composable
fun AddWeatherCard(unit: String, iconId: Int, desc: String, value: Double?){
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
                text = "$value $unit",
                modifier = Modifier.padding(start = 10.dp, top = 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

}
