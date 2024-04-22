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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.CardColors
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.rakettoppskytning.R
import no.uio.ifi.in2000.rakettoppskytning.model.formatDate
import no.uio.ifi.in2000.rakettoppskytning.model.weatherAtPos.WeatherAtPosHour
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.getColorFromStatusValue
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard0
import no.uio.ifi.in2000.rakettoppskytning.ui.theme.weatherCard50

@Composable
fun FavoriteCard(name: String, input: FavCard, navController: NavController){
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
                navController.navigate("DetailsScreen/${input.data.date}")
            }
        )
        {
            Row {
                Spacer(
                    modifier = Modifier
                        .width(10.dp)
                        .fillMaxHeight()
                        .background(getColorFromStatusValue(input.data.closeToLimitScore))
                )
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.width(150.dp),
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
                            Column {
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
                                    Row {


                                        Text(
                                            text = "Lat:",
                                            fontSize = 17.sp,
                                            color = weatherCard0.copy(alpha = 0.7F)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = input.data.lat.toString(),
                                            fontSize = 17.sp,
                                            color = weatherCard0
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row {
                                        Text(
                                            text = "Lon:",
                                            fontSize = 17.sp,
                                            color = weatherCard0.copy(alpha = 0.7F)
                                        )
                                        Spacer(modifier = Modifier.width(7.dp))

                                        Text(
                                            text = input.data.lon.toString(),
                                            fontSize = 17.sp,
                                            color = weatherCard0
                                        )
                                    }
                                }
                            }

                        }
                    }
                    Spacer(modifier = Modifier.width(70.dp))

                    Column() {
                        Text(
                            text = input.data.date.substring(11, 16),
                            fontSize = 20.sp,
                            color = weatherCard0.copy(alpha = 0.7F)
                        )
                        Text(
                            text = formatDate(input.data.series.time),
                            fontSize = 13.sp,
                            softWrap = true,
                            maxLines = 1,
                            color = weatherCard0.copy(alpha = 0.7F)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))

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
}