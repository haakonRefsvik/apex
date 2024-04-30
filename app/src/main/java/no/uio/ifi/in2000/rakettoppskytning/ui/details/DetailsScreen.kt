package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.sharp.Favorite
import androidx.compose.material.icons.sharp.FavoriteBorder
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.navigation.Routes
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.model.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.thresholds.ThresholdType
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilDescription
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.soil.getSoilScore
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.LazyColumnScrollbar
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ListIndicatorSettings
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionActionable
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionMode
import no.uio.ifi.in2000.rakettoppskytning.ui.favorites.FavoriteCardViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.details100
import no.uio.ifi.in2000.rakettoppskytning.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.home.MapViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.firstButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import kotlin.math.roundToInt
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontStyle
import androidx.media3.common.util.Log
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialog
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialogCorrect


@SuppressLint("ResourceType")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    navController: NavHostController,
    backStackEntry: String?,
    detailsScreenViewModel: DetailsScreenViewModel,
    favoriteCardViewModel: FavoriteCardViewModel,
    context: Context,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel
) {
    val weatherUiState by detailsScreenViewModel.weatherUiState.collectAsState()
    val favoriteUiState by detailsScreenViewModel.favoriteUiState.collectAsState()

    val time: String = backStackEntry ?: ""
    detailsScreenViewModel.time.value = backStackEntry ?: ""
    var weatherAtPosHour: List<WeatherAtPosHour> = listOf()
    val duration = Toast.LENGTH_SHORT
    detailsScreenViewModel.time.value = time

    if (time.last() == 'f') {
        favoriteUiState.weatherAtPos.weatherList.forEach {
            if (it.date == time.dropLast(1)) {
                weatherAtPosHour = listOf(it)
            }
        }
    } else {
        weatherUiState.weatherAtPos.weatherList.forEach {
            if (it.date == time) {
                weatherAtPosHour = listOf(it)
            }

        }
    }

    val scope = rememberCoroutineScope()
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
                            imageVector = Icons.Default.KeyboardArrowLeft,
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
                modifier = Modifier
                    .shadow(
                        10.dp,
                        RectangleShape,
                        false,
                        DefaultShadowColor,
                        DefaultShadowColor
                    )
                    .heightIn(max = 50.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = main50),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(modifier = Modifier.sizeIn(maxWidth = 38.dp), onClick = {
                        navController.navigate(Routes.favCards)
                    }) {
                        Icon(
                            Icons.Default.Favorite,
                            modifier = Modifier.fillMaxSize(),
                            contentDescription = "Favorite",
                            tint = main0,
                        )
                    }

                    Spacer(modifier = Modifier.width(110.dp))
                    IconButton(onClick = {
                        scope.launch { homeScreenViewModel.scaffold.bottomSheetState.partialExpand() }
                        navController.popBackStack("HomeScreen", false)
                    }) {
                        Icon(
                            Icons.Sharp.LocationOn,
                            modifier = Modifier.size(40.dp),
                            contentDescription = "Location",
                            tint = main0
                        )
                    }

                    Spacer(modifier = Modifier.width(110.dp))
                    IconButton(onClick = { navController.navigate(Routes.settings) }) {
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
                val datePrefix = formatDate(weatherNow.date)
                Column(
                    modifier = Modifier.width(340.dp),
                )
                {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$datePrefix at ${weatherNow.date.subSequence(11, 16)}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                            color = main0
                        )
                        Spacer(modifier = Modifier.width(60.dp))
                        IconButton(onClick = {
                            detailsScreenViewModel.toggleFavorite(
                                lat = weatherNow.lat,
                                lon = weatherNow.lon,
                                date = weatherNow.date,
                                !weatherNow.favorite.value

                            )
                            if (weatherNow.favorite.value) {
                                favoriteCardViewModel.addFavoriteCard(
                                    lat = weatherNow.lat.toString(),
                                    lon = weatherNow.lon.toString(),
                                    date = weatherNow.date
                                )
                                val toast =
                                    Toast.makeText(context, "Added card to favorites", duration)
                                toast.show()
                            } else {
                                favoriteCardViewModel.deleteFavoriteCard(
                                    lat = weatherNow.lat.toString(),
                                    lon = weatherNow.lon.toString(),
                                    date = weatherNow.date
                                )
                                val toast =
                                    Toast.makeText(context, "Removed card from favorites", duration)
                                toast.show()
                            }
                        }) {
                            Icon(
                                if (weatherNow.favorite.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                modifier = Modifier.size(30.dp),
                                contentDescription = "favorited",
                                tint = main0
                            )
                        }

                    }
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
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(modifier = Modifier.width(360.dp),
                            colors = ButtonColors(
                                containerColor = firstButton0,
                                contentColor = firstButton100,
                                disabledContainerColor = firstButton0,
                                disabledContentColor = firstButton100
                            ),
                            onClick = {
                                mapViewModel.makeTra.value = true
                                scope.launch { homeScreenViewModel.scaffold.bottomSheetState.partialExpand() }
                                navController.popBackStack("HomeScreen", false)
                            }
                        ) {

                            Text("Calculate ballistic trajectory")
                        }
                        if (weatherNow.verticalProfile == null) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Spacer(modifier = Modifier.height(15.dp))
                                Text(
                                    modifier = Modifier.widthIn(max = 360.dp),
                                    text = "*Insufficient grib data, trajectroy will be inaccurate.",
                                    color = main50,
                                    fontStyle = FontStyle.Italic
                                )
                                Spacer(modifier = Modifier.height(15.dp))

                            }

                        }
                        Spacer(modifier = Modifier.height(15.dp))

                        LazyColumn(state = listState) {

                            item {


                                weatherNow.verticalProfile?.let {
                                    Spacer(modifier = Modifier.height(20.dp))
                                    ShearWindCard(
                                        verticalProfile = it,
                                        statusCode = statusMap[ThresholdType.MAX_SHEAR_WIND.name]
                                            ?: 0.0
                                    )
                                    Spacer(modifier = Modifier.height(30.dp))
                                }

                            }

                            item {
                                weatherNow.verticalProfile?.let {
                                    ShearWindSpeedCard(verticalProfile = it)
                                    Spacer(modifier = Modifier.height(30.dp))
                                }

                            }
                            item {
                                weatherNow.verticalProfile?.let {
                                    ShearWindDirCard(verticalProfile = it)
                                    Spacer(modifier = Modifier.height(30.dp))
                                }

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
                                        info = "The temperature in 6 hours is min. ${fcData.next6Hours?.details?.airTemperatureMin ?: "N/A"} ℃",
                                    )
                                    Spacer(modifier = Modifier.width(20.dp))
                                    var valueText =
                                        "${fcData.next1Hours?.details?.precipitationAmount} mm"
                                    var descText =
                                        "${fcData.next12Hours?.details?.probabilityOfPrecipitation?.roundToInt()} % chance the next 12 hours"

                                    if (fcData.next12Hours?.details?.probabilityOfPrecipitation == null) {
                                        descText = "Rain in the next hour"
                                    }

                                    if (fcData.next1Hours == null) {
                                        valueText =
                                            "${fcData.next6Hours?.details?.precipitationAmount} mm"
                                        descText = "Rain in the next 6 hours"
                                        if (fcData.next6Hours?.details?.precipitationAmount == null) {
                                            valueText =
                                                "N/A"
                                            descText = "There is no data for this period"
                                        }
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
                                        descText = "Cloud cover below 2000m altitude"
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
