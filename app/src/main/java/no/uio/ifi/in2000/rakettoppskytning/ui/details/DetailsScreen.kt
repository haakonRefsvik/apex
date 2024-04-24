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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.model.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.getDayAndMonth
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
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilScore
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.LazyColumnScrollbar
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ListIndicatorSettings
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionActionable
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionMode
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,
    backStackEntry: String?,
    detailsScreenViewModel: DetailsScreenViewModel,
    mapViewModel: MapViewModel
) {
    val weatherUiState by detailsScreenViewModel.weatherUiState.collectAsState()
    val time: String = backStackEntry ?: ""
    detailsScreenViewModel.time.value = backStackEntry ?: ""
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
                colors = TopAppBarColors(main100, main100, main100, main100, main100),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ArrowBack",
                            tint = main0
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
            BottomAppBar(
                containerColor = main50,
                modifier = Modifier.shadow(
                    10.dp,
                    RectangleShape,
                    false,
                    DefaultShadowColor,
                    DefaultShadowColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = main50),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location",
                            tint = main0
                        )
                    }
                    Spacer(modifier = Modifier.width(94.dp))
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(R.drawable.rakket),
                            contentDescription = "Rakket",
                            tint = main0
                        )
                    }
                    Spacer(modifier = Modifier.width(95.dp))
                    IconButton(onClick = { navController.navigate("ThresholdScreen") }) {
                        Icon(
                            Icons.Sharp.Settings,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Settings",
                            tint = main0
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(main100),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (weatherAtPosHour.isEmpty()) {
                Text("Its empty here ...", color = main0)
            }

            weatherAtPosHour.forEach { weatherNow ->

                val fcData = weatherNow.series.data
                val statusMap = weatherNow.valuesToLimitMap
                val daysAhead = getNumberOfDaysAhead(weatherNow.date)
                val datePrefix = formatDate(weatherNow.date)
                Column(
                    modifier = Modifier.width(340.dp),
                )
                {

                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "$datePrefix at ${weatherNow.date.subSequence(11, 16)}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 30.sp,
                        color = main0
                    )

                    Spacer(modifier = Modifier.height(20.dp))


                }


                val listState = rememberLazyListState()

                LazyColumnScrollbar(
                    listState = listState,
                    modifier = Modifier,
                    rightSide = true,
                    alwaysShowScrollBar = false,
                    thickness = 5.dp,
                    padding = 10.dp,
                    thumbMinHeight = 0.1f,
                    thumbColor = Color.White.copy(alpha = 0.4F),
                    thumbSelectedColor = Color.White,
                    thumbShape = CircleShape,
                    selectionMode = ScrollbarSelectionMode.Thumb,
                    selectionActionable = ScrollbarSelectionActionable.Always,
                    hideDelay = 400.toDuration(DurationUnit.MILLISECONDS),
                    showItemIndicator = ListIndicatorSettings.EnabledMirrored(
                        100.dp,
                        Color.Gray
                    ),
                    enabled = true,
                    indicatorContent = { index, isThumbSelected ->
                        // Indicator content composable
                        // Replace with your own implementation
                    }
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LazyColumn(state = listState) {
                            item {
                                Spacer(modifier = Modifier.height(50.dp))

                                weatherNow.verticalProfile?.let {
                                    ShearWindCard(
                                        verticalProfile = it,
                                        statusCode = statusMap[ThresholdType.MAX_SHEAR_WIND.name]
                                            ?: 0.0
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
                                    statusCode = statusMap[ThresholdType.MAX_WIND.name] ?: 0.0
                                )
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            item {
                                Row {
                                    WeatherCard(
                                        iconId = R.drawable.luftfuktighet,
                                        desc = "Humidity",
                                        value = "${fcData.instant.details.relativeHumidity} %",
                                        info = "Relative humidity at 2m above the ground in",
                                        statusCode = statusMap[ThresholdType.MAX_HUMIDITY.name]
                                            ?: 0.0
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))

                                    WeatherCard(
                                        iconId = R.drawable.luftfuktighet,
                                        desc = "Dew Point",
                                        value = "${fcData.instant.details.dewPointTemperature} ℃",
                                        info = "Temperature where dew starts to form",
                                        statusCode = statusMap[ThresholdType.MAX_DEW_POINT.name]
                                            ?: 0.0
                                    )
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            item {
                                Row {
                                    WeatherCard(
                                        iconId = R.drawable.temp,
                                        desc = "Temperature",
                                        value = "${fcData.instant.details.airTemperature} ℃",
                                        info = "The temperature in 6 hours is min. ${fcData.next6Hours?.details?.airTemperatureMin} ℃",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    var valueText =
                                        "${fcData.next1Hours?.details?.precipitationAmount} mm"
                                    var descText =
                                        "${fcData.next12Hours?.details?.probabilityOfPrecipitation?.roundToInt()} % chance the next 12 hours"
                                    if (fcData.next1Hours == null) {
                                        valueText =
                                            "${fcData.next6Hours?.details?.precipitationAmount} mm"
                                        descText = "Rain in the next 6 hours"

                                    }

                                    WeatherCard(
                                        iconId = R.drawable.vann,
                                        desc = "Precipitation",
                                        value = valueText,
                                        info = descText,
                                        statusCode = statusMap[ThresholdType.MAX_PRECIPITATION.name]
                                            ?: 0.0
                                    )
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            item {
                                Row {
                                    var valueText =
                                        "${fcData.instant.details.fogAreaFraction?.roundToInt()} mm"
                                    var descText = "Amount of surrounding area covered in fog"
                                    var titleText = "Fog"
                                    if (fcData.instant.details.fogAreaFraction == null) {
                                        valueText =
                                            "${fcData.instant.details.cloudAreaFractionLow} %"
                                        descText = "Cloud cover lower than 2000m altitude"
                                        titleText = "Low clouds"
                                    }

                                    WeatherCard(
                                        iconId = R.drawable.fog,
                                        desc = titleText,
                                        value = valueText,
                                        info = descText
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))

                                    var info = "Moisture in the ground"

                                    if (weatherNow.soilMoisture != null) {
                                        info = getSoilDescription(weatherNow.soilMoisture)
                                    }

                                    WeatherCard(
                                        iconId = R.drawable.vann,
                                        desc = "Soil moisture",
                                        value = "${weatherNow.soilMoisture} %",
                                        info = info,
                                        statusCode = getSoilScore(weatherNow.soilMoisture)
                                    )

                                }
                                Spacer(modifier = Modifier.height(30.dp))
                            }
                            item {
                                Row {
                                    WeatherCard(
                                        iconId = R.drawable.cloudy,
                                        desc = "Cloud cover",
                                        value = "${fcData.instant.details.cloudAreaFraction.roundToInt()} %",
                                        info = "Total cloud cover for all heights in"
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
                                        desc = "Visibility",
                                        value = visibility,
                                        info = "Estimated vertical visibility"
                                    )
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                                Button(modifier = Modifier.width(360.dp),
                                    colors = ButtonColors(
                                        containerColor = firstButton0,
                                        contentColor = firstButton100,
                                        disabledContainerColor = firstButton0,
                                        disabledContentColor = firstButton100
                                    ),
                                    onClick = { mapViewModel.makeTra.value = true }
                                ) {
                                    Text("Calculate ballistic trajectory")
                                }
                                Spacer(modifier = Modifier.height(30.dp))
                                Button(
                                    modifier = Modifier.width(360.dp),
                                    colors = ButtonColors(
                                        containerColor = firstButton0,
                                        contentColor = firstButton100,
                                        disabledContainerColor = firstButton0,
                                        disabledContentColor = firstButton100
                                    ),
                                    onClick = { mapViewModel.makeTra.value = false },
                                    enabled = mapViewModel.makeTra.value
                                ) {
                                    Text("Remove ballistic trajectory")
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(50.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
