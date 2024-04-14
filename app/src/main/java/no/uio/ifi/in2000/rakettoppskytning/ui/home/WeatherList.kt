package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.LazyColumnScrollbar
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ListIndicatorSettings
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionActionable
import no.uio.ifi.in2000.rakettoppskytning.scrollbar.ScrollbarSelectionMode
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.model.getNumberOfDaysAhead
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/*
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherList(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
) {
    val forecast by homeScreenViewModel.weatherUiState.collectAsState()





        val listState = rememberLazyListState()

        LazyColumnScrollbar(
            listState = listState,
            modifier = Modifier,
            rightSide = true,
            alwaysShowScrollBar = false,
            thickness = 6.dp,
            padding = 8.dp,
            thumbMinHeight = 0.1f,
            thumbColor = Color.Gray,
            thumbSelectedColor = Color.Gray,
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
            LazyColumn(state = listState) {
                item {
                    // Your actual content here...
                    // For example, you can use a foreach loop to display dynamic content
                    forecast.weatherAtPos.weatherList.forEach breaking@{ input ->
                        // Add your composable components that represent each item in the list
                        // Modify this according to your needs
                        //Text("${input.date}: ${input.hour}")
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
                                        Text(input.series.time.substring(11, 16), fontSize = 20.sp)
                                        if (daysAhead == 1) {
                                            Text(text = "Imorgen", fontSize = 14.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(50.dp))
                                    Column {
                                        Text(
                                            "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )

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
                                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                        contentDescription = "Arrow"
                                    )
                                }
                            }

                        }
                        Spacer(modifier = Modifier.height(7.5.dp))

                    }
                }



            }


        }


    }



 */


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherList(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
) {
    val forecast by homeScreenViewModel.weatherUiState.collectAsState()

    val listState = rememberLazyListState()

    LazyColumnScrollbar(
        listState = listState,
        modifier = Modifier,
        rightSide = true,
        alwaysShowScrollBar = false,
        thickness = 6.dp,
        padding = 8.dp,
        thumbMinHeight = 0.1f,
        thumbColor = Color.Gray,
        thumbSelectedColor = Color.Gray,
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



        LazyColumn(state = listState) {
            item {
                forecast.weatherAtPos.weatherList.forEach breaking@{ input ->
                    val daysAhead = getNumberOfDaysAhead(input.date)

                    Spacer(modifier = Modifier.height(7.5.dp))

                    // Wrap ElevatedCard inside a Row and center it horizontally
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                        Text(input.series.time.substring(11, 16), fontSize = 20.sp)
                                        if (daysAhead == 1) {
                                            Text(text = "Imorgen", fontSize = 14.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(50.dp))
                                    Column {
                                        Text(
                                            "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )

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
                                        imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                                        contentDescription = "Arrow"
                                    )
                                }
                            }

                            // Card content here
                        }
                    }

                    Spacer(modifier = Modifier.height(7.5.dp))
                }
            }
        }



    }


}





/*
        LazyColumn(content = {
            item {
                forecast.weatherAtPos.weatherList.forEach breaking@{ input ->
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
                                    Text(input.series.time.substring(11, 16), fontSize = 20.sp)
                                    if (daysAhead == 1) {
                                        Text(text = "Imorgen", fontSize = 14.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.width(50.dp))
                                Column {
                                    Text(
                                        "${input.series.data.next1Hours?.details?.precipitationAmount} mm",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

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
    */








