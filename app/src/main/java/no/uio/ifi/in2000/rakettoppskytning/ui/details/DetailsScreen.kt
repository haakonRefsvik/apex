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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.model.getDayName
import no.uio.ifi.in2000.rakettoppskytning.model.getNumberOfDaysAhead
import no.uio.ifi.in2000.rakettoppskytning.model.grib.LevelData
import no.uio.ifi.in2000.rakettoppskytning.model.grib.ShearWind
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilDescription
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilCategory
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,
    backStackEntry: String?,
    detailsScreenViewModel: DetailsScreenViewModel,
) {
    val weatherUiState by detailsScreenViewModel.weatherUiState.collectAsState()
    val time: String = backStackEntry ?: ""
    var weatherAtPosHour: List<WeatherAtPosHour> = listOf()

    weatherUiState.weatherAtPos.weatherList.forEach {
        if (it.date == time) {
            weatherAtPosHour = listOf(it)
        }
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    IconButton(onClick = {navController.navigate("ThresholdScreen")}) {
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

            if (weatherAtPosHour.isEmpty()) {
                Text("Its empty here ...")
            }

            weatherAtPosHour.forEach { weatherNow ->

                val fcData = weatherNow.series.data
                val statusMap = weatherNow.valuesToLimitMap
                val datoPrefix: String = when{
                    getNumberOfDaysAhead(weatherNow.date) == 1 -> "Tomorrow"
                    getNumberOfDaysAhead(weatherNow.date) == 0 -> "Today"
                    getNumberOfDaysAhead(weatherNow.date) < 1 -> getDayName(weatherNow.date, getNumberOfDaysAhead(weatherNow.date))
                    getNumberOfDaysAhead(weatherNow.date) < 6 -> weatherNow.date

                    else -> ""
                }

                Spacer(modifier = Modifier.height(15.dp))
                Row(modifier = Modifier.padding(0.dp)) {
                    LazyColumn {
                        item {
                            Spacer(modifier = Modifier.width(25.dp))
                            Column(
                                modifier = Modifier.width(340.dp),
                            )
                            {
                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    text = "$datoPrefix at ${weatherNow.date.subSequence(11, 16)}",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 30.sp
                                )

                                Spacer(modifier = Modifier.height(40.dp))

                                HorizontalDivider(
                                    modifier = Modifier.width(340.dp),
                                    thickness = 1.dp, color = Color.Black.copy(alpha = 0.2f))
                                Spacer(modifier = Modifier.height(15.dp))
                            }
                        }

                        item {
                            weatherNow.verticalProfile?.let {
                                ShearWindCard(
                                    verticalProfile = it,
                                    statusCode = statusMap[ThresholdType.MAX_SHEAR_WIND.name]?: 0.0
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }

                        item {
                            weatherNow.verticalProfile?.let { ShearWindSpeedCard(verticalProfile = it) }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            weatherNow.verticalProfile?.let { ShearWindDirCard(verticalProfile = it) }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            WindCard(
                                details = fcData.instant.details,
                                statusCode = statusMap[ThresholdType.MAX_WIND.name]?: 0.0
                            )
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            weatherNow.soilMoisture?.let { SoilCard(soilPercentage = it) }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                WeatherCard(
                                    iconId = R.drawable.temp,
                                    desc = "Temperatur",
                                    value = "${fcData.instant.details.airTemperature} ℃",
                                    info = "Temperaturen om 6 timer er minimalt ${fcData.next6Hours?.details?.airTemperatureMin} ℃",
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                WeatherCard(
                                    iconId = R.drawable.vann,
                                    desc = "Nedbør",
                                    value = "${fcData.next1Hours?.details?.precipitationAmount} mm",
                                    info = "${fcData.next12Hours?.details?.probabilityOfPrecipitation?.roundToInt()} % sjanse for nedbør de neste 12 timene",
                                    statusCode = statusMap[ThresholdType.MAX_PRECIPITATION.name]?: 0.0
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                WeatherCard(
                                    iconId = R.drawable.fog,
                                    desc = "Tåke",
                                    value = "${fcData.instant.details.fogAreaFraction?.roundToInt()} %",
                                    info = "Tåkedekke på bakken"
                                )
                                Spacer(modifier = Modifier.width(20.dp))

                                val combinedStatus: Double
                                val d = statusMap[ThresholdType.MAX_DEW_POINT.name]?: 0.0
                                val h = statusMap[ThresholdType.MAX_HUMIDITY.name]?: 0.0

                                combinedStatus = if (d == 1.0 || h == 1.0) {
                                    1.0
                                } else {
                                    (d + h) / 2
                                }

                                WeatherCard(
                                    iconId = R.drawable.luftfuktighet,
                                    desc = "Luftfuktighet",
                                    value = "${fcData.instant.details.relativeHumidity.roundToInt()} %",
                                    info = "Duggpunktet er ${fcData.instant.details.dewPointTemperature} ℃",
                                    statusCode = combinedStatus
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                WeatherCard(
                                    iconId = R.drawable.cloudy,
                                    desc = "Skydekke",
                                    value = "${fcData.instant.details.cloudAreaFraction.roundToInt()} %",
                                    info = "Total skydekke for alle høyder"
                                )
                                Spacer(modifier = Modifier.width(20.dp))

                                val d = fcData.instant.details
                                val fog: Double = d.fogAreaFraction ?: 0.0
                                val visibility = getVerticalSightKm(
                                    fog,
                                    d.cloudAreaFractionLow,
                                    d.cloudAreaFractionMedium,
                                    d.cloudAreaFractionHigh,
                                )

                                WeatherCard(
                                    iconId = R.drawable.eye,
                                    desc = "Sikt",
                                    value = visibility,
                                    info = "Estimert vertikal sikt"
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            }
        }
    }

}
