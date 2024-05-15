package no.uio.ifi.in2000.rakettoppskytning.ui.favorites

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.data.formatting.extractHourAndMinutes
import no.uio.ifi.in2000.rakettoppskytning.data.formatting.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.main50
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard50

@Composable
fun FavoriteCardElement(
    name: String,
    lat: String,
    lon: String,
    date: String,
    navController: NavController,
    favoriteCardViewModel: FavoriteCardViewModel,
    weatherData: WeatherAtPosHour?
){
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
                /*
                Sending a flag 'f' behind the date to detailscreen so
                the detailscreen knows that the call came from a
                favoriteCard and to use the FavoriteCard-stateflow instead
                 */
                navController.navigate("DetailsScreen/${date}f")

            }
        )
        {
            Row {
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                        .fillMaxHeight()
                        .background(
                            getColorFromStatusValue(weatherData?.closeToLimitScore?: -1.0)
                        )
                )
                Spacer(modifier = Modifier.width(10.dp))

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.width(180.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Image(
                                    modifier = Modifier.size(35.dp),

                                    painter = painterResource(
                                        id = R.drawable.pin
                                    ),
                                    contentDescription = "pin"
                                )
                            }

                            Spacer(modifier = Modifier.width(13.dp))
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if(name != ""){
                                    Row {

                                        Text(
                                            text = name,
                                            fontSize = 17.sp,
                                            color = weatherCard0
                                        )
                                    }
                                }
                                else {
                                    val formatString = "%.${5}f"

                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Lat:",
                                            fontSize = 17.sp,
                                            color = weatherCard0.copy(alpha = 0.7F)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = String.format(formatString, lat.toDouble()),
                                            fontSize = 17.sp,
                                            color = weatherCard0
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        Text(
                                            text = "Lon:",
                                            fontSize = 17.sp,
                                            color = weatherCard0.copy(alpha = 0.7F)
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))

                                        Text(
                                            text = String.format(formatString, lon.toDouble()),
                                            fontSize = 17.sp,
                                            color = weatherCard0
                                        )
                                    }
                                }
                            }

                        }
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.width(70.dp),
                            horizontalAlignment = Alignment.Start

                        ) {

                            Text(
                                text = extractHourAndMinutes(date),
                                fontSize = 20.sp,
                                color = weatherCard0.copy(alpha = 0.7F)
                            )
                            Text(
                                text = formatDate(date),
                                fontSize = 13.sp,
                                softWrap = true,
                                maxLines = 1,
                                color = weatherCard0.copy(alpha = 0.7F)
                            )
                        }
                        IconButton(modifier = Modifier
                            .size(30.dp)
                            .padding(end = 5.dp),
                            onClick = {
                                favoriteCardViewModel.deleteFavoriteCard(lat, lon, date)
                            }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Delete favorite",
                                tint = main50
                            )
                        }
                    }
                }

            }
        }
    }
}