package no.uio.ifi.in2000.rakettoppskytning.ui.home

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.gson.Gson
import no.uio.ifi.in2000.rakettoppskytning.data.forecast.ForeCastSymbols
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherColumn(navController: NavHostController, homeScreenViewModel: HomeScreenViewModel) {

    val forecast by homeScreenViewModel.foreCastUiState.collectAsState()

    val currentInstant = Instant.now()
    val formatter = DateTimeFormatter.ISO_INSTANT

    val formattedInstant = formatter.format(currentInstant)

    val newInstant = currentInstant.plus(7, ChronoUnit.HOURS)

    val formattedInstantAfter = formatter.format(newInstant)

    LazyColumn(content = {
        item {
            forecast.foreCast.forEach breaking@{ input ->
                input.properties.timeseries.forEach lit@{ tider ->
                    if (tider.time < formattedInstant) {
                        return@lit
                    }
                    if (formattedInstant < tider.time && tider.time < formattedInstantAfter) {
                        val klokkeslett = tider.time.substring(11, 16)
                        Spacer(modifier = Modifier.height(7.5.dp))
                        ElevatedCard(
                            modifier = Modifier
                                .height(80.dp)
                                .width(340.dp),
                            onClick = {
                                val json =
                                    Uri.encode(Gson().toJson(tider.data))
                                navController.navigate("DetailsScreen/${json}")
                            }
                        )
                        {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.width(15.dp))
                                Text(klokkeslett, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(55.dp))
                                Text(
                                    "${tider.data.next1Hours?.details?.precipitationAmount} mm",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(27.5.dp))

                                Spacer(modifier = Modifier.width(15.dp))
                                tider.data.next1Hours?.summary?.let {
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

                        Spacer(modifier = Modifier.height(7.5.dp))


                    } else {
                        return@breaking
                    }

                }

            }

        }
    })

}