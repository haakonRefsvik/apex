package no.uio.ifi.in2000.rakettoppskytning.ui.home


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.model.formatting.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.getVerticalSightKm
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.LazyColumnScrollbar
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ListIndicatorSettings
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionActionable
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionMode
import no.uio.ifi.in2000.rakettoppskytning.ui.home.filter.FilterCategory
import no.uio.ifi.in2000.rakettoppskytning.ui.home.filter.FilterDialog
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.favoriteCard50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.secondButton100
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard50
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherList(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
    mapViewModel: MapViewModel,
) {
    val forecast by homeScreenViewModel.weatherUiState.collectAsState()
    val favoriteLocations by homeScreenViewModel.favoriteUiState.collectAsState()
    val openFilterDialog = remember { mutableStateOf(false) }
    val openTimeDialog = remember { mutableStateOf(false) }


    val lat by mapViewModel.lat
    val lon by mapViewModel.lon


    val controller = LocalSoftwareKeyboardController.current

    val scope = rememberCoroutineScope()
    val scaffoldState by homeScreenViewModel.bottomSheetScaffoldState

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
                onConfirmation = { },
                homeScreenViewModel = homeScreenViewModel
            )
        }
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

        LazyColumn(state = listState, modifier = Modifier.background(main100),
            content = {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (favoriteLocations.favorites.isNotEmpty()) {
                            Row(modifier = Modifier.width(340.dp)) {
                                if (favoriteLocations.favorites.size == 1) {
                                    Text("Favorite location:", fontSize = 14.sp, color = main50)

                                } else {
                                    Text("Favorite locations:", fontSize = 14.sp, color = main50)
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(2.5.dp))
                        LazyRow(
                            modifier = Modifier.width(340.dp)
                        )
                        {

                            favoriteLocations.favorites.reversed().forEach { favorite ->
                                item {
                                    OutlinedCard(
                                        modifier = Modifier
                                            .height(55.dp)
                                            .width(200.dp),
                                        colors = CardColors(
                                            containerColor = favoriteCard50,
                                            contentColor = favoriteCard0,
                                            disabledContentColor = favoriteCard50,
                                            disabledContainerColor = favoriteCard0
                                        ),
                                        border = BorderStroke(1.dp, color = favoriteCard100),
                                        onClick = {
                                            mapViewModel.lat.value = favorite.lat.toDouble()
                                            mapViewModel.lon.value = favorite.lon.toDouble()

                                            controller?.hide()
                                            homeScreenViewModel.getWeatherByCord(lat, lon)


                                            scope.launch {
                                                delay(200)
                                                scaffoldState.bottomSheetState.expand()
                                            }

                                        }
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxSize()


                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .width(175.dp),
                                                verticalAlignment = Alignment.CenterVertically

                                            ) {
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Icon(
                                                    modifier = Modifier.size(25.dp),
                                                    imageVector = Icons.Default.Place,
                                                    contentDescription = "Location",
                                                    tint = Color(216, 64, 64, 255)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    favorite.name,
                                                    fontSize = 18.sp,
                                                    color = favoriteCard100
                                                )

                                            }
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            )

                                            {
                                                IconButton(modifier = Modifier
                                                    .size(30.dp)
                                                    .padding(end = 5.dp),
                                                    onClick = {
                                                        homeScreenViewModel.deleteFavoriteLocation(favorite.name, favorite.lat, favorite.lon)
                                                    }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Delete favorite",
                                                        tint = favoriteCard100

                                                    )

                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                }

                            }


                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                }
                if (forecast.weatherAtPos.weatherList.isEmpty() && homeScreenViewModel.hasBeenFiltered.value) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {

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

                            Image(
                                modifier = Modifier.fillMaxSize(0.5f),

                                painter = painterResource(R.drawable.data_not_found),
                                contentDescription = "Filter"
                            )
                            Text(
                                text = "We are not able to show the desired data ",
                                fontSize = 18.sp, color = main50
                            )

                        }
                    }

                } else if (!homeScreenViewModel.getWeatherHasBeenCalled.value) {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Click on get weather data to get started",
                                fontSize = 18.sp, color = main50
                            )
                        }
                    }

                } else {
                    item {
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {

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
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ElevatedCard(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(340.dp),
                                colors = CardColors(
                                    containerColor = main100,
                                    contentColor = main100,
                                    disabledContainerColor = main100,
                                    disabledContentColor = main100
                                )
                            )
                            {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row {

                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.Start,
                                    ) {
                                        if(homeScreenViewModel.markedCardIndex.value != FilterCategory.UNFILTERED)
                                        Text(
                                            "Sorted by ${homeScreenViewModel.markedCardIndex.value.string}",
                                            fontSize = 16.sp,
                                            color = weatherCard0.copy(0.7F)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    forecast.weatherAtPos.weatherList.forEach { input ->
                        item {
                            WeatherCard(
                                weatherAtPosHour = input,
                                navController = navController,
                                homeScreenViewModel = homeScreenViewModel
                            )
                        }
                    }
                }
            })
    }

}
