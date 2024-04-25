package no.uio.ifi.in2000.rakettoppskytning.ui.home


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.sharp.LocationOn
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.model.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.getNumberOfDaysAhead
import no.uio.ifi.in2000.rakettoppskytning.model.savedInDB.FavoriteEvent
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.LazyColumnScrollbar
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ListIndicatorSettings
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionActionable
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionMode
import no.uio.ifi.in2000.rakettoppskytning.ui.home.favorite.AddFavoriteDialog
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.SettingsViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard50
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
        Spacer(modifier = Modifier.height(10.dp))
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
                    disabledContentColor = secondButton100
                ),
                onClick = {
                    openTimeDialog.value = true
                }) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = painterResource(R.drawable.outline_access_time_24),
                    contentDescription = "Edit",
                )
                Spacer(modifier = Modifier.width(5.dp))

                Text("Change time")
            }
            Spacer(modifier = Modifier.width(25.dp))
            Button(modifier = Modifier.width(155.dp),
                colors = ButtonColors(
                    containerColor = secondButton0,
                    contentColor = secondButton100,
                    disabledContainerColor = secondButton0,
                    disabledContentColor = secondButton100
                ),
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
        Spacer(modifier = Modifier.height(10.dp))


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

            LazyColumn(state = listState, modifier = Modifier.background(main100),
                content = {

                    item {
                        forecast.weatherAtPos.weatherList.forEach { input ->
                            var precipText =
                                "${input.series.data.next1Hours?.details?.precipitationAmount} mm"
                            if (input.series.data.next1Hours == null) {
                                precipText =
                                    "${input.series.data.next6Hours?.details?.precipitationAmount} mm"

                                if (input.series.data.next6Hours?.details?.precipitationAmount == null){
                                    precipText = "N/A"
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
                                            modifier = Modifier.width(75.dp),
                                            horizontalAlignment = Alignment.Start,
                                        ) {
                                            Text(
                                                input.series.time.substring(11, 16),
                                                fontSize = 20.sp,
                                                color = weatherCard0
                                            )
                                            Text(
                                                text = formatDate(input.series.time),
                                                fontSize = 13.sp,
                                                softWrap = true,
                                                maxLines = 1,
                                                color = weatherCard0.copy(alpha = 0.7F)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(40.dp))
                                        Column {
                                            when (homeScreenViewModel.markedCardIndex.intValue) {
                                                0 -> {
                                                    Text(
                                                        "${input.series.data.instant.details.windSpeed} m/s",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.width(75.dp),
                                                        color = weatherCard0,
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
                                                        precipText,
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.width(75.dp),
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
                                                        modifier = Modifier.width(75.dp),
                                                        color = weatherCard0
                                                    )
                                                }


                                                4 ->
                                                    Text(
                                                        "${input.series.data.instant.details.relativeHumidity} %",
                                                        fontSize = 20.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.width(75.dp),
                                                        color = weatherCard0
                                                    )

                                                5 ->
                                                    Text(
                                                        "${input.series.data.instant.details.dewPointTemperature} °c",
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

                                        Spacer(modifier = Modifier.width(27.5.dp))

                                        Spacer(modifier = Modifier.width(15.dp))


                                        var symbolId =
                                            input.series.data.next1Hours?.summary?.symbolCode?.uppercase()
                                                ?: "FAIR_DAY"

                                        if (input.series.data.next1Hours == null) {
                                            symbolId =
                                                input.series.data.next12Hours?.summary?.symbolCode?.uppercase()
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
                    }
                }
            )
        }
    }
}