package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.data.formatting.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.ui.home.filter.FilterCategory
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard50

@Composable
fun WeatherCard(
    weatherAtPosHour: WeatherAtPosHour,
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel
){
    val data = weatherAtPosHour.series.data
    var precipText = "${data.next1Hours?.details?.precipitationAmount} mm"

    if (data.next1Hours == null) {
        precipText = "${data.next6Hours?.details?.precipitationAmount} mm"

        if(data.next6Hours?.details?.precipitationAmount == null){
            precipText = "N/A"
            Log.d("mais", "${weatherAtPosHour.date} has no precip")
        }

    }


    Spacer(modifier = Modifier.height(7.5.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedCard(
            modifier = Modifier
                .height(80.dp)
                .width(340.dp),
            colors = CardColors(
                containerColor = weatherCard50,
                contentColor = weatherCard0,
                disabledContainerColor = weatherCard50,
                disabledContentColor = weatherCard0
            ),
            onClick = {
                navController.navigate("DetailsScreen/${weatherAtPosHour.date}")
            }
        )
        {
            Row {
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                        .fillMaxHeight()
                        .background(getColorFromStatusValue(weatherAtPosHour.closeToLimitScore))
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.width(75.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            weatherAtPosHour.series.time.substring(11, 16),
                            fontSize = 20.sp,
                            color = weatherCard0
                        )
                        Text(
                            text = formatDate(weatherAtPosHour.series.time),
                            fontSize = 13.sp,
                            softWrap = true,
                            maxLines = 1,
                            color = weatherCard0.copy(alpha = 0.7F)
                        )
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                    Column {
                        when (homeScreenViewModel.markedCardIndex.value) {
                            FilterCategory.WIND_STRENGTH -> {
                                Text(
                                    "${weatherAtPosHour.series.data.instant.details.windSpeed} m/s",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(75.dp),
                                    color = weatherCard0,
                                )
                            }

                            FilterCategory.WIND_DIR -> {
                                Box(
                                    modifier = Modifier.width(75.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(

                                        Icons.AutoMirrored.Filled.ArrowForward,
                                        modifier = Modifier
                                            .size(30.dp)
                                            .rotate(90f + weatherAtPosHour.series.data.instant.details.windFromDirection.toFloat()),
                                        tint = weatherCard0,
                                        contentDescription = "Location"
                                    )
                                }

                            }

                            FilterCategory.RAIN ->
                                Text(
                                    precipText,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(75.dp),
                                    color = weatherCard0
                                )

                            FilterCategory.VIEW_DIST -> {
                                val d =
                                    weatherAtPosHour.series.data.instant.details
                                val fog: Double =
                                    d.fogAreaFraction ?: 0.0
                                val visibility = getVerticalSightKm(
                                    fog,
                                    d.cloudAreaFractionLow,
                                    d.cloudAreaFractionMedium,
                                    d.cloudAreaFractionHigh,
                                )

                                Text(
                                    visibility,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(75.dp),
                                    color = weatherCard0
                                )
                            }


                            FilterCategory.AIR_HUMID ->
                                Text(
                                    "${weatherAtPosHour.series.data.instant.details.relativeHumidity} %",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(75.dp),
                                    color = weatherCard0
                                )

                            FilterCategory.DEW_POINT ->
                                Text(
                                    "${weatherAtPosHour.series.data.instant.details.dewPointTemperature} °c",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(75.dp),
                                    color = weatherCard0
                                )

                            else ->
                                Text(
                                    precipText,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(75.dp),
                                    color = weatherCard0
                                )
                        }


                    }

                    Spacer(modifier = Modifier.width(42.dp))

                    var symbolId =
                        weatherAtPosHour.series.data.next1Hours?.summary?.symbolCode?.uppercase()
                            ?: "FAIR_DAY"

                    if (weatherAtPosHour.series.data.next1Hours == null) {
                        symbolId =
                            weatherAtPosHour.series.data.next12Hours?.summary?.symbolCode?.uppercase()
                                ?: "FAIR_DAY"
                    }

                    Image(
                        modifier = Modifier.size(55.dp),

                        painter = painterResource(
                            id = ForeCastSymbols.valueOf(
                                symbolId
                            ).id
                        ),
                        contentDescription = symbolId
                    )


                    Icon(
                        modifier = Modifier.size(20.dp),
                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                        contentDescription = "Arrow",
                        tint = weatherCard0
                    )
                }
            }

        }

    }
    Spacer(modifier = Modifier.height(7.5.dp))
}