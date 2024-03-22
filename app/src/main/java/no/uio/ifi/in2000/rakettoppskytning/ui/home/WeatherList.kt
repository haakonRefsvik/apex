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
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import no.uio.ifi.in2000.rakettoppskytning.model.getNumberOfDaysAhead
import no.uio.ifi.in2000.rakettoppskytning.ui.settings.ThresholdViewModel
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherList(
    navController: NavHostController,
    homeScreenViewModel: HomeScreenViewModel,
) {
    val forecast by homeScreenViewModel.weatherUiState.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Add the gradient on top of LazyColumn
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black // Adjust the color as needed
                        ),
                        startY = 0f,
                        endY = 2100f // Adjust the endY value to control the gradient height
                    )
                )
        )
    }

}