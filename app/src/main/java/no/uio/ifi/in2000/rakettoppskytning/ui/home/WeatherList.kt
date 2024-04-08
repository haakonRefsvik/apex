package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.model.getNumberOfDaysAhead
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.ui.favorite.AddFavoriteDialog
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun WeatherList(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
) {
    val forecast by homeScreenViewModel.weatherUiState.collectAsState()
    val openFilterDialog = remember { mutableStateOf(false) }

    if (forecast.weatherAtPos.weatherList.isNotEmpty() || homeScreenViewModel.hasBeenFiltered.value) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
        when {

            openFilterDialog.value -> {
                FilterDialog(
                    onDismissRequest = {
                        openFilterDialog.value = false

                    },
                    onResetRequest = {
                        homeScreenViewModel.resetFilter()
                    },
                    onConfirmation = {
                        openFilterDialog.value = false
                        homeScreenViewModel.filterList()
                    },
                    homeScreenViewModel = homeScreenViewModel

                )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row {

            Button(modifier = Modifier.width(155.dp), onClick = {


            }) {

                Text("Change time")
            }
            Spacer(modifier = Modifier.width(25.dp))
            Button(modifier = Modifier.width(155.dp), onClick = {
                openFilterDialog.value = true

            }) {
                Icon(
                    modifier = Modifier
                        .size(20.dp),
                    painter = painterResource(R.drawable.filter),
                    contentDescription = "Filter"
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text("Filter")
            }


        }
        Spacer(modifier = Modifier.height(5.dp))



        LazyColumn(
            content = {

                item {
                    forecast.weatherAtPos.weatherList.forEach { input ->
                        val daysAhead = getNumberOfDaysAhead(input.date)

                        Spacer(modifier = Modifier.height(7.5.dp))
                        ElevatedCard(
                            modifier = Modifier
                                .height(80.dp)
                                .width(340.dp),
                            onClick = {
                                navController.navigate("DetailsScreen/${input.date}")
                            }
                        )
                        {
                            Row {
                                Spacer(
                                    modifier = Modifier
                                        .width(10.dp)
                                        .fillMaxHeight()
                                        .background(getColorFromStatusValue(input.closeToLimitScore))
                                )
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.width(65.dp),
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        Text(
                                            input.series.time.substring(11, 16),
                                            fontSize = 20.sp
                                        )
                                        if (daysAhead == 1) {
                                            Text(text = "Imorgen", fontSize = 14.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(40.dp))
                                    Column {
                                        when (homeScreenViewModel.markedCardIndex.intValue) {
                                            0 -> {


                                                Text(
                                                    "${input.series.data.instant.details.windSpeed} m/s",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.width(80.dp)
                                                )
                                            }

                                            1 -> {
                                                Box(
                                                    modifier = Modifier.width(80.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(

                                                        Icons.AutoMirrored.Filled.ArrowForward,
                                                        modifier = Modifier
                                                            .size(30.dp)
                                                            .rotate(90f + input.series.data.instant.details.windFromDirection.toFloat()),
                                                        contentDescription = "Location"
                                                    )


                                                }

                                            }


                                            2 ->
                                                Text(
                                                    "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.width(80.dp)
                                                )

                                            3 -> {
                                                val d = input.series.data.instant.details
                                                val fog: Double = d.fogAreaFraction ?: 0.0
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
                                                    modifier = Modifier.width(80.dp)
                                                )
                                            }


                                            4 ->
                                                Text(
                                                    "${input.series.data.instant.details.relativeHumidity} %",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.width(80.dp)
                                                )

                                            5 ->
                                                Text(
                                                    "${input.series.data.instant.details.dewPointTemperature} °c",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.width(80.dp)
                                                )

                                            else ->
                                                Text(
                                                    "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.width(80.dp)
                                                )
                                        }


                                    }

                                    Spacer(modifier = Modifier.width(27.5.dp))

                                    Spacer(modifier = Modifier.width(15.dp))


                                    input.series.data.next1Hours?.summary?.let {

                                        Image(
                                            modifier = Modifier.size(55.dp),

                                            painter = painterResource(
                                                id = ForeCastSymbols.valueOf(
                                                    it.symbolCode.uppercase()
                                                ).id
                                            ),
                                            contentDescription = it.symbolCode
                                        )
                                    }
                                        ?: run {
                                            Box(
                                                modifier = Modifier.size(55.dp),
                                                contentAlignment = Alignment.CenterStart
                                            ) {
                                                Text(
                                                    text = "N/A",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 20.sp
                                                )
                                            }

                                        }

                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                        contentDescription = "Arrow"
                                    )
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(7.5.dp))
                    }
                }
            })

//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                Color.Transparent,
//                                Color.Transparent,
//                                Color.Transparent,
//                                Color.Black // Adjust the color as needed
//                            ),
//                            startY = 0f,
//                            endY = 2800f // Adjust the endY value to control the gradient height
//                        )
//                    )
//            )
    }
//    }

}


