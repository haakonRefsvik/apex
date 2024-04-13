package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.model.getNumberOfDaysAhead
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard50

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherList(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
) {
    val forecast by homeScreenViewModel.weatherUiState.collectAsState()
    val openFilterDialog = remember { mutableStateOf(false) }
    val openTimeDialog = remember { mutableStateOf(false) }

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
        when {
            openTimeDialog.value -> {
                TimeDialog(
                    onDismissRequest = { openTimeDialog.value = false },
                    onConfirmation = { /*TODO*/ },
                    homeScreenViewModel = homeScreenViewModel
                )
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Row {

            Button(modifier = Modifier.width(155.dp),
                colors = ButtonColors(
                    containerColor = secondButton0,
                    contentColor = secondButton100,
                    disabledContainerColor = secondButton0,
                    disabledContentColor = secondButton100),
                onClick = {
                openTimeDialog.value = true
            }) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                )
                Spacer(modifier = Modifier.width(5.dp))

                Text("Change time")
            }

            Spacer(modifier = Modifier.width(25.dp))

            Button(modifier = Modifier
                .width(155.dp),
                colors = ButtonColors(
                    containerColor = secondButton0,
                    contentColor = secondButton100,
                    disabledContainerColor = secondButton0,
                    disabledContentColor = secondButton100),
                onClick = {
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

        LazyColumn(modifier = Modifier.background(main100),
            content = {

                item {
                    forecast.weatherAtPos.weatherList.forEach { input ->
                        val daysAhead = getNumberOfDaysAhead(input.date)

                        Spacer(modifier = Modifier.height(7.5.dp))
                        ElevatedCard(
                            modifier = Modifier
                                .height(80.dp)
                                .width(340.dp),
                            colors = CardColors(
                                containerColor = weatherCard50,
                                contentColor = weatherCard0,
                                disabledContainerColor = weatherCard50,
                                disabledContentColor = weatherCard0),
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
                                            color = weatherCard0,
                                            fontSize = 20.sp
                                        )
                                        if (daysAhead == 1) {
                                            Text(
                                                text = "Imorgen",
                                                fontSize = 14.sp,
                                                color = weatherCard0
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(50.dp))
                                    Column {
                                        when (homeScreenViewModel.markedCardIndex.intValue) {
                                            0 -> {

                                                Text(
                                                    "${input.series.data.instant.details.windSpeed} m/s",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = weatherCard0,
                                                    modifier = Modifier.width(75.dp)
                                                )
                                            }

                                            1 -> {
                                                Box(
                                                    modifier = Modifier.width(75.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.AutoMirrored.Filled.ArrowForward,
                                                        modifier = Modifier
                                                            .size(30.dp)
                                                            .rotate(90f + input.series.data.instant.details.windFromDirection.toFloat()),
                                                        tint = weatherCard0,
                                                        contentDescription = "Location"
                                                    )
                                                }
                                            }

                                            2 ->
                                                Text(
                                                    "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .width(75.dp),
                                                    color = weatherCard0
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
                                                    modifier = Modifier
                                                        .width(75.dp),
                                                    color = weatherCard0
                                                )
                                            }

                                            4 ->
                                                Text(
                                                    "${input.series.data.instant.details.relativeHumidity} %",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .width(75.dp),
                                                    color = weatherCard0
                                                )

                                            5 ->
                                                Text(
                                                    "${input.series.data.instant.details.dewPointTemperature} °c",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .width(75.dp),
                                                    color = weatherCard0
                                                )

                                            else ->
                                                Text(
                                                    "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier
                                                        .width(75.dp),
                                                    color = weatherCard0
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

                                    Icon(
                                        modifier = Modifier.size(20.dp),
                                        tint = weatherCard0,
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
    }
}

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

//    }



