package no.uio.ifi.in2000.rakettoppskytning.ui.details

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.ThresholdRepository
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.WeatherAtPosRepo
import no.uio.ifi.in2000.rakettoppskytning.data.grib.GribRepository
import no.uio.ifi.in2000.rakettoppskytning.model.forecast.Details
import no.uio.ifi.in2000.rakettoppskytning.model.grib.VerticalProfile
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
fun DetailsScreenPreview() {
    val navController = rememberNavController()
    DetailsScreen(
        navController = navController,
        backStackEntry = "1",
        detailsScreenViewModel = DetailsScreenViewModel(WeatherAtPosRepo(ThresholdRepository(), GribRepository()))
    )
}
@RequiresApi(Build.VERSION_CODES.O)
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
        if(it.date == time){
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

            if (weatherAtPosHour.isEmpty()) {
                Text("Her var det tomt")
            }else{
                Text("Værdata for klokken ${weatherAtPosHour.first().hour}")
            }

            weatherAtPosHour.forEach {
                val fcData = it.series.data
                val verticalProfile = it.verticalProfile

                Spacer(modifier = Modifier.height(30.dp))
                Row(modifier = Modifier.padding(0.dp)) {
                    LazyColumn {
                        item {
                            if (verticalProfile != null) {
                                ShearWindCard(verticalProfile = verticalProfile)
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            WindCard(details = fcData.instant.details)
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                AddWeatherCard(
                                    iconId = R.drawable.temp,
                                    desc = "Temperatur",
                                    value = "${fcData.instant.details.airTemperature} ℃",
                                    info = "Temperaturen om 6 timer er minimalt ${fcData.next6Hours?.details?.airTemperatureMin} ℃"
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                AddWeatherCard(
                                    iconId = R.drawable.vann,
                                    desc = "Nedbør",
                                    value = "${fcData.next1Hours?.details?.precipitationAmount} mm" ,
                                    info = "${fcData.next12Hours?.details?.probabilityOfPrecipitation?.roundToInt()} % sjanse for nedbør de neste 12 timene"

                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                AddWeatherCard(
                                    iconId = R.drawable.fog,
                                    desc = "Tåke",
                                    value = "${fcData.instant.details.fogAreaFraction?.roundToInt()} %",
                                    info = "Tåkedekke på bakken"
                                )
                                Spacer(modifier = Modifier.width(20.dp))
                                AddWeatherCard(
                                    iconId = R.drawable.luftfuktighet,
                                    desc = "Luftfuktighet",
                                    value = "${fcData.instant.details.relativeHumidity.roundToInt()} %",
                                    info = "Duggpunktet er ${fcData.instant.details.dewPointTemperature} ℃"
                                )
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                        item {
                            Row {
                                AddWeatherCard(
                                    iconId = R.drawable.cloudy,
                                    desc = "Skydekke",
                                    value = "${fcData.instant.details.cloudAreaFraction.roundToInt()} %",
                                    info = "Total skydekke for alle høyder"
                                )
                                Spacer(modifier = Modifier.width(20.dp))

                                val d = fcData.instant.details
                                val fog: Double = d.fogAreaFraction ?: 0.0
                                val visibilityKm = visibilityConverter(
                                    fog,
                                    d.cloudAreaFractionLow,
                                    d.cloudAreaFractionMedium,
                                    d.cloudAreaFractionHigh
                                )

                                AddWeatherCard(
                                    iconId = R.drawable.eye,
                                    desc = "Sikt",
                                    value = "$visibilityKm km",
                                    info = "Estimert vertikal sikt"
                                )
                            }
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

fun visibilityConverter(fogGround: Double, cloudLow: Double, cloudMed: Double, cloudHigh: Double): String{
    // Convert fog percentage to visibility reduction factor
    val fogFactor: Double = fogGround * 0.01

    // Convert cloud covers to corresponding visibility reduction factors
    val lowCloudFactor: Double = cloudLow * 0.01    // 0 - 2000
    val medCloudFactor: Double = cloudMed * 0.01    // 2000 - 5000
    val highCloudFactor: Double = cloudHigh * 0.01  // 5000 - infinity

    val fogWeight = 1.0                     // sikt blir mer påvirket av tåke på bakken enn langt opp i høyden
    val lowCloudWeight: Double = 0.8
    val medCloudWeight: Double = 0.6
    val highCloudWeight: Double = 0.2

    // Calculate the combined impact of fog and cloud cover on visibility
    val visibilityReductionFactor: Double =
        1- (1 - (fogFactor * fogWeight))* (1 - (lowCloudFactor * lowCloudWeight)) * (1 - (medCloudFactor * medCloudWeight)) * (1 - (highCloudFactor * highCloudWeight))

    //Log.d("visibilityConverter", "\n LowC: $lowCloudFactor \nMedC: $medCloudFactor \nHighC: $highCloudFactor \nvisFactor: $visibilityReductionFactor \n")

    val visibility: Double = 8.0 / visibilityReductionFactor

    if(visibility > 50){
        return ">50"
    }

    return visibility.roundToInt().toString()
}


@Composable
fun AddWeatherCard(value: String, iconId: Int, desc: String, info: String = "") {
    ElevatedCard(
        modifier = Modifier
            .height(125.dp)
            .width(170.dp)
    ) {
        Column {
            Spacer(modifier = Modifier.height(10.dp))
            Row {
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    modifier = Modifier
                        .width(30.dp),
                    painter = painterResource(iconId),
                    contentDescription = desc
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = desc,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                Text(
                    text = value,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(modifier = Modifier.padding(horizontal = 15.dp)) {
                Text(
                    text = info,
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                )
            }
        }
    }
}

@Composable
fun WindCard(details: Details) {
    ElevatedCard(

        modifier = Modifier
            .height(140.dp)
            .width(360.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Row {
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp),
                        painter = painterResource(R.drawable.vind2),
                        contentDescription = "VindSymbol"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vind på bakkenivå",
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                        .width(200.dp)
                )
                Text(
                    text = "${details.windSpeed} m/s",
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Max vindkast er ${details.windSpeedOfGust} m/s",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                )

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
                            .rotate(270.0F + details.windFromDirection.toFloat()),
                        painter = painterResource(R.drawable.kompasspil),
                        contentDescription = "kompasspil"
                    )

                }
            }
        }
    }
}

@Composable
fun ShearWindCard(verticalProfile: VerticalProfile) {
    ElevatedCard(

        modifier = Modifier
            .height(140.dp)
            .width(360.dp)
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Row {
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp),
                        painter = painterResource(R.drawable.vind2),
                        contentDescription = "VindSymbol"
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Maksimalt vertikalt vindskjær",
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(
                    modifier = Modifier
                        .height(10.dp)
                        .width(200.dp)
                )
                Text(
                    text = String.format("%.1f", verticalProfile.getMaxSheerWind().windSpeed) + " m/s",
                    modifier = Modifier.padding(vertical = 5.dp),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Vindskjæret er på ca ${verticalProfile.getMaxSheerWind().upperLayer.getLevelHeightInMeters().roundToInt()} meters høyde",
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                )

            }
        }
    }
}
